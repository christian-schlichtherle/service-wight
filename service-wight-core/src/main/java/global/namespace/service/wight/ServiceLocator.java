/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight;

import global.namespace.service.wight.annotation.ServiceImplementation;
import global.namespace.service.wight.function.Decorator;
import global.namespace.service.wight.function.Factory;
import global.namespace.service.wight.function.Mapping;
import global.namespace.service.wight.function.Provider;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
 * {@linkplain ServiceImplementation#priority() priority}.
 * Only the instance with the highest priority is kept for subsequent use.
 * <p>
 * Next, the class is searched again for any resources with the name
 * {@code "META-INF/services/"} plus the name of the given locatable
 * <i>function</i> class.
 * If this yields some results, the classes with the names contained in these
 * resources get loaded and instantiated by calling their public no-argument
 * constructor.
 * Next, the instances get sorted in ascending order of their
 * {@linkplain ServiceImplementation#priority() priority} and kept for subsequent use.
 * <p>
 * Finally, depending on the requesting method either a container or a factory gets created which will use the
 * instantiated provider and mappings to obtain a product and map it in order of their priorities.
 *
 * @see    ServiceLoader
 * @author Christian Schlichtherle
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class ServiceLocator {

    private static final Comparator<Mapping<?>> MAPPING_COMPARATOR = (
            comparingInt(mapping ->
                    ofNullable(mapping.getClass().getDeclaredAnnotation(ServiceImplementation.class))
                    .map(ServiceImplementation::priority)
                    .orElse(0)
            )
    );

    private static final Comparator<Provider<?>> PROVIDER_COMPARATOR = (
            comparingInt((Provider<?> provider) ->
                    ofNullable(provider.getClass().getDeclaredAnnotation(ServiceImplementation.class))
                    .map(ServiceImplementation::priority)
                    .orElse(0)
            ).reversed()
    );

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
    public <P, FP extends Factory<P>>
    CompositeFactory<P> factory(Class<FP> factory) { return factory(factory, empty()); }

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
    public <P, FP extends Factory<P>, MP extends Mapping<P>>
    CompositeFactory<P> factory(Class<FP> factory, Class<MP> mapping) { return factory(factory, of(mapping)); }

    private <P> CompositeFactory<P>
    factory(Class<? extends Factory<P>> factory, Optional<Class<? extends Mapping<P>>> mapping) {
        return new CompositeFactory<>(
                providers(factory),
                mapping.map(this::<P, Mapping<P>>mappings).orElseGet(Collections::emptyList)
        );
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
    public <P, PP extends Provider<P>>
    CompositeContainer<P> container(Class<PP> provider) { return container(provider, empty()); }

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
    public <P, PP extends Provider<P>, DP extends Decorator<P>>
    CompositeContainer<P> container(Class<PP> provider, Class<DP> decorator) {
        return container(provider, of(decorator));
    }

    private <P> CompositeContainer<P>
    container(Class<? extends Provider<P>> provider, Optional<Class<? extends Decorator<P>>> decorator) {
        return new CompositeContainer<>(
                providers(provider),
                decorator.map(this::<P, Decorator<P>>mappings).orElseGet(Collections::emptyList)
        );
    }

    private <P, PP extends Provider<P>> List<PP> providers(final Class<? extends PP> service) {
        final List<PP> providers = new ArrayList<>();
        instancesOf(service).forEach(providers::add);
        providers.sort(PROVIDER_COMPARATOR);
        instanceOf(service).map(s -> {
            providers.add(0, s);
            return null;
        });
        if (providers.isEmpty()) {
            throw new ServiceConfigurationError("No service located for " + service + ".");
        }
        return providers;
    }

    private <P, MP extends Mapping<P>> List<MP> mappings(final Class<? extends MP> service) {
        final List<MP> mappings = new ArrayList<>();
        instancesOf(service).forEach(mappings::add);
        mappings.sort(MAPPING_COMPARATOR);
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
