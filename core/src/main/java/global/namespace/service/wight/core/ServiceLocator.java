/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.core;

import global.namespace.service.wight.annotation.ServiceImplementation;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static java.util.Comparator.comparingInt;
import static java.util.Optional.*;

/**
 * Locates services on the classpath and creates composite product providers from located service providers and
 * filters.
 * Locating services is done in several steps:
 * <p>
 * First, the fully qualified name of a given service interface class is used as the key string to lookup a
 * {@link System#getProperty system property}.
 * If this yields a value then it's supposed to name a class which gets loaded and instantiated by calling its public
 * no-argument constructor.
 * <p>
 * Otherwise, the class path is searched for any resources with the name {@code "META-INF/services/"} plus the name of
 * the given <i>service provider</i> class.
 * If this yields no results, a {@link ServiceConfigurationError} is thrown.
 * <p>
 * Otherwise the classes with the names contained in these resources get loaded and instantiated by calling their
 * public no-argument constructor.
 * Next, the instances are sorted by descending {@linkplain ServiceImplementation#priority() priority}.
 * Only the first instance (i.e. the one with the highest priority) is used for providing a product.
 * <p>
 * Next, the classpath is searched again for any resources with the name {@code "META-INF/services/"} plus the name of
 * the given <i>service filter</i> class.
 * If this yields some results, the classes with the names contained in these resources get loaded and instantiated by
 * calling their public no-argument constructor.
 * Next, the instances get sorted by ascending {@linkplain ServiceImplementation#priority() priority} for subsequent
 * use.
 * <p>
 * Finally, a composite provider gets created from the lists of product providers and filters.
 * The composite provider uses only the first product provider, but all product filters.
 * Client applications can introspect, and potentially modify, the lists of product providers and filters.
 *
 * @author Christian Schlichtherle
 * @see ServiceLoader
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class ServiceLocator {

    private static final Comparator<Supplier<?>> PROVIDER_COMPARATOR =
            comparingInt((Supplier<?> provider) ->
                    ofNullable(provider.getClass().getDeclaredAnnotation(ServiceImplementation.class))
                            .map(ServiceImplementation::priority)
                            .orElse(0)
            ).reversed();

    private static final Comparator<UnaryOperator<?>> FILTER_COMPARATOR =
            comparingInt(mapping ->
                    ofNullable(mapping.getClass().getDeclaredAnnotation(ServiceImplementation.class))
                            .map(ServiceImplementation::priority)
                            .orElse(0)
            );

    private final Optional<ClassLoader> classLoader;

    /**
     * Constructs a new service locator using the current thread's context classloader.
     */
    public ServiceLocator() {
        this(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Constructs a new service locator using the given class loader.
     */
    public ServiceLocator(ClassLoader cl) {
        this.classLoader = ofNullable(cl);
    }

    /**
     * Returns a provider of some service.
     *
     * @param <S>      the type of the service.
     * @param <SP>     the type of the service providers.
     * @param provider the interface class of the locatable service providers.
     * @return A new composite provider for some service.
     * @throws ServiceConfigurationError if loading or instantiating a located class fails for some reason.
     */
    public <S, SP extends Supplier<S>>
    CompositeProvider<S, SP, ? extends UnaryOperator<S>> provider(Class<SP> provider) {
        return provider(provider, empty());
    }

    /**
     * Returns a provider of some service.
     *
     * @param <S>      the type of the service.
     * @param <SP>     the type of the service providers.
     * @param <SF>     the type of the service filters.
     * @param provider the interface class of the locatable service providers.
     * @param filter   the interface class of the locatable service filters.
     * @return A new composite provider form some service.
     * @throws ServiceConfigurationError if loading or instantiating a located class fails for some reason.
     */
    public <S, SP extends Supplier<S>, SF extends UnaryOperator<S>>
    CompositeProvider<S, SP, SF> provider(Class<SP> provider, Class<SF> filter) {
        return provider(provider, of(filter));
    }

    private <S, SP extends Supplier<S>, SF extends UnaryOperator<S>>
    CompositeProvider<S, SP, SF> provider(Class<SP> factory, Optional<Class<SF>> filter) {
        return new CompositeProvider<>(providers(factory),
                filter.map(this::filters).orElseGet(Collections::emptyList));
    }

    private <S, SP extends Supplier<S>> List<SP> providers(final Class<? extends SP> service) {
        final List<SP> providers = new ArrayList<>();
        instancesOf(service).forEach(providers::add);
        providers.sort(PROVIDER_COMPARATOR);
        instanceOf(service).map(s -> {
            providers.add(0, s);
            return null;
        });
        if (providers.isEmpty()) {
            throw new ServiceConfigurationError("No providers located for " + service + ".");
        }
        return providers;
    }

    private <S, SF extends UnaryOperator<S>> List<SF> filters(final Class<? extends SF> service) {
        final List<SF> mappings = new ArrayList<>();
        instancesOf(service).forEach(mappings::add);
        mappings.sort(FILTER_COMPARATOR);
        return mappings;
    }

    private <S> ServiceLoader<S> instancesOf(Class<S> service) {
        return ServiceLoader.load(service, classLoader.orElse(null));
    }

    private <S> Optional<S> instanceOf(final Class<S> service) {
        return ofNullable(System.getProperty(service.getName())).map(name -> {
            try {
                return service.cast(Class
                        .forName(name, false, classLoader.orElse(null))
                        .getDeclaredConstructor()
                        .newInstance());
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                    InstantiationException | InvocationTargetException e) {
                throw new ServiceConfigurationError(e.toString(), e);
            }
        });
    }
}
