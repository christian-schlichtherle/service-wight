/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight;

import global.namespace.service.wight.function.Container;
import global.namespace.service.wight.function.Factory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiFunction;

import static java.util.Comparator.comparingInt;
import static java.util.Optional.*;

/**
 * Creates containers or factories of products.
 * Resolving service instances is done in several steps:
 * <p>
 * First, the name of a given service interface class is used as the
 * key string to lookup a {@link System#getProperty system property}.
 * If this yields a value then it's supposed to name a class which gets loaded
 * and instantiated by calling its public no-argument constructor.
 * <p>
 * Otherwise, the class path is searched for any resources with the name
 * {@code "META-INF/services/"} plus the name of the given service interface class.
 * If this yields no results, a {@link ServiceConfigurationError} is thrown.
 * <p>
 * Otherwise the classes with the names contained in these resources get loaded
 * and instantiated by calling their public no-argument constructor.
 * Next, the instances are filtered according to their
 * {@linkplain LocatableService#getPriority() priority}.
 * Only the instance with the highest priority is kept for subsequent use.
 * <p>
 * Next, the class is searched again for any resources with the name
 * {@code "META-INF/services/"} plus the name of the given locatable
 * <i>function</i> class.
 * If this yields some results, the classes with the names contained in these
 * resources get loaded and instantiated by calling their public no-argument
 * constructor.
 * Next, the instances get sorted in ascending order of their
 * {@linkplain LocatableService#getPriority() priority} and kept for subsequent use.
 * <p>
 * Finally, depending on the requesting method either a container or a factory gets created which will use the
 * instantiated provider and mappings to obtain a product and map it in order of their priorities.
 *
 * @see    ServiceLoader
 * @author Christian Schlichtherle
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class ServiceLocator {

    private static final Comparator<LocatableService> LOCATABLE_MAPPING_COMPARATOR =
            comparingInt(LocatableService::getPriority);

    private static final Comparator<LocatableService> LOCATABLE_PROVIDER_COMPARATOR =
            LOCATABLE_MAPPING_COMPARATOR.reversed();

    private final Optional<ClassLoader> classLoader;

    /** Constructs a new service locator using the current thread's context classloader. */
    public ServiceLocator() { this(Thread.currentThread().getContextClassLoader()); }

    /** Constructs a new service locator using the given class loader. */
    public ServiceLocator(ClassLoader cl) { this.classLoader = ofNullable(cl); }

    /**
     * Creates a new factory for products.
     *
     * @param  <P> the type of the products to create.
     * @param  factory the class of the locatable factory for the products.
     * @return A new factory of products.
     * @throws ServiceConfigurationError if loading or instantiating
     *         a located class fails for some reason.
     */
    public <P> Factory<P> factory(Class<? extends LocatableFactory<P>> factory) { return factory(factory, empty()); }

    /**
     * Creates a new factory for products.
     *
     * @param  <P> the type of the products to create.
     * @param  factory the class of the locatable factory for the products.
     * @param  mapping the class of the locatable mapping for the products.
     * @return A new factory of products.
     * @throws ServiceConfigurationError if loading or instantiating
     *         a located class fails for some reason.
     */
    public <P> Factory<P> factory(Class<? extends LocatableFactory<P>> factory,
                                  Class<? extends LocatableMapping<P>> mapping) {
        return factory(factory, of(mapping));
    }

    private <P> Factory<P> factory(Class<? extends LocatableFactory<P>> factory,
                                   Optional<Class<? extends LocatableMapping<P>>> mapping) {
        return factory(factory, mapping, (f, m) -> {
            final LocatableFactory<P> f0 = f.get(0);
            return m.isEmpty() ? f0 : new FactoryWithSomeMappings<>(f0, m);
        });
    }

    private <P, C> C factory(Class<? extends LocatableFactory<P>> factory,
                             Optional<Class<? extends LocatableMapping<P>>> mapping,
                             BiFunction<List<? extends LocatableFactory<P>>, List<? extends LocatableMapping<P>>, C> combinator) {
        return combinator.apply(providers(factory), mapping.map(this::mappings).orElseGet(Collections::emptyList));
    }

    /**
     * Creates a new container with a single product.
     *
     * @param  <P> the type of the product to contain.
     * @param  provider the class of the locatable provider for the product.
     * @return A new container with a single product.
     * @throws ServiceConfigurationError if loading or instantiating
     *         a located class fails for some reason.
     */
    public <P> Container<P> container(Class<? extends LocatableProvider<P>> provider) {
        return container(provider, empty());
    }

    /**
     * Creates a new container with a single product.
     *
     * @param  <P> the type of the product to contain.
     * @param  provider the class of the locatable provider for the product.
     * @param  decorator the class of the locatable decoractors for the product.
     * @return A new container with a single product.
     * @throws ServiceConfigurationError if loading or instantiating
     *         a located class fails for some reason.
     */
    public <P> Container<P> container(Class<? extends LocatableProvider<P>> provider,
                                      Class<? extends LocatableDecorator<P>> decorator) {
        return container(provider, of(decorator));
    }

    private <P> Container<P> container(Class<? extends LocatableProvider<P>> provider,
                                       Optional<Class<? extends LocatableDecorator<P>>> decorator) {
        return container(provider, decorator, (p, d) -> {
            final LocatableProvider<P> p0 = p.get(0);
            return new Store<>(d.isEmpty() ? p0 : new ProviderWithSomeMappings<>(p0, d));
        });
    }

    private <P, C> C container(Class<? extends LocatableProvider<P>> provider,
                               Optional<Class<? extends LocatableDecorator<P>>> decorator,
                               BiFunction<List<? extends LocatableProvider<P>>, List<? extends LocatableDecorator<P>>, C> combinator) {
        return combinator.apply(providers(provider), decorator.map(this::mappings).orElseGet(Collections::emptyList));
    }

    private <S extends LocatableProvider<?>> List<S> providers(final Class<S> service) {
        final List<S> providers = new ArrayList<>();
        instancesOf(service).forEach(providers::add);
        providers.sort(LOCATABLE_PROVIDER_COMPARATOR);
        instanceOf(service, empty()).map(s -> {
            providers.add(0, s);
            return null;
        });
        if (providers.isEmpty()) {
            throw new ServiceConfigurationError("No service located for " + service + ".");
        }
        return providers;
    }

    private <S extends LocatableMapping<?>> List<S> mappings(final Class<S> service) {
        final List<S> mappings = new ArrayList<>();
        instancesOf(service).forEach(mappings::add);
        mappings.sort(LOCATABLE_MAPPING_COMPARATOR);
        return mappings;
    }

    private <S> ServiceLoader<S> instancesOf(Class<S> service) {
        return ServiceLoader.load(service, classLoader.orElse(null));
    }

    private <S> Optional<S> instanceOf(final Class<S> service, final Optional<Class<? extends S>> impl) {
        return ofNullable(System.getProperty(service.getName(), impl.map(Class::getName).orElse(null)))
                .map(name -> {
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
