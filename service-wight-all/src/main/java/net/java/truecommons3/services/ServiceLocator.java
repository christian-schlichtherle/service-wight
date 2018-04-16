/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truecommons3.services;

import net.java.truecommons3.logging.BundledMessage;
import net.java.truecommons3.logging.LocalizedLogger;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.*;

import static java.util.Optional.*;

/**
 * Creates containers or factories of products.
 * Resolving service instances is done in several steps:
 * <p>
 * First, the name of a given <i>provider</i> service class is used as the
 * key string to lookup a {@link System#getProperty system property}.
 * If this yields a value then it's supposed to name a class which gets loaded
 * and instantiated by calling its public no-argument constructor.
 * <p>
 * Otherwise, the class path is searched for any resources with the name
 * {@code "META-INF/services/"} plus the name of the given locatable
 * <i>provider<i> class.
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
 * Finally, depending on the requesting method either a container or a factory
 * gets created which will use the instantiated provider and functions
 * to obtain a product and map it in order of their priorities.
 *
 * @see    ServiceLoader
 * @author Christian Schlichtherle
 */
public final class ServiceLocator {

    private static final Logger logger = new LocalizedLogger(ServiceLocator.class);
    private static final Marker CONFIG = MarkerFactory.getMarker("CONFIG");

    private final Loader loader;

    /**
     * Constructs a new locator which uses the class loader of the given client
     * class before using the current thread context's class loader unless the
     * latter is identical to the former.
     *
     * @param client the class which identifies the calling client.
     */
    public ServiceLocator(Class<?> client) { this(ofNullable(client.getClassLoader())); }

    /**
     * Constructs a new locator which uses the given class loader before using
     * the current thread context's class loader unless the latter is identical
     * to the former.
     *
     * @param loader the class loader to use before the current thread
     *        context's class loader unless the the latter is identical to the
     *        former.
     * @since TrueCommons 1.0.13
     */
    public ServiceLocator(final Optional<ClassLoader> loader) { this.loader = new Loader(loader); }

    /**
     * Creates a new factory for products.
     *
     * @param  <P> the type of the products to create.
     * @param  factory the class of the locatable factory for the products.
     * @return A new factory of products.
     * @throws ServiceConfigurationError if loading or instantiating
     *         a located class fails for some reason.
     */
    public <P> Factory<P> factory(Class<? extends LocatableFactory<P>> factory) throws ServiceConfigurationError {
        return factory(factory, empty());
    }

    /**
     * Creates a new factory for products.
     *
     * @param  <P> the type of the products to create.
     * @param  factory the class of the locatable factory for the products.
     * @param  functions the class of the locatable functions for the products.
     * @return A new factory of products.
     * @throws ServiceConfigurationError if loading or instantiating
     *         a located class fails for some reason.
     */
    public <P> Factory<P> factory(final Class<? extends LocatableFactory<P>> factory,
                                  final Class<? extends LocatableFunction<P>> functions)
            throws ServiceConfigurationError {
        return factory(factory, of(functions));
    }

    private <P> Factory<P> factory(final Class<? extends LocatableFactory<P>> factory,
                                   final Optional<Class<? extends LocatableFunction<P>>> functions)
            throws ServiceConfigurationError {
        final LocatableFactory<P> p = provider(factory);
        final List<? extends LocatableFunction<P>> f = functions.map(this::functions).orElseGet(Collections::emptyList);
        return f.isEmpty() ? p : new FactoryWithSomeFunctions<P>(p, f);
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
    public <P> Container<P> container(Class<? extends LocatableProvider<P>> provider)
            throws ServiceConfigurationError {
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
                                      Class<? extends LocatableDecorator<P>> decorator)
            throws ServiceConfigurationError {
        return container(provider, of(decorator));
    }

    private <P> Container<P> container(final Class<? extends LocatableProvider<P>> provider,
                                       final Optional<Class<? extends LocatableDecorator<P>>> decorator)
            throws ServiceConfigurationError {
        final LocatableProvider<P> p = provider(provider);
        final List<? extends LocatableDecorator<P>> d = decorator.map(this::functions).orElseGet(Collections::emptyList);
        return new Store<P>(d.isEmpty() ? p : new ProviderWithSomeFunctions<P>(p, d));
    }

    private <S extends LocatableProvider<?>> S provider(final Class<S> spec) {
        Optional<S> service = loader.instanceOf(spec, Optional.empty());
        if (!service.isPresent()) {
            for (final S newService : loader.instancesOf(spec)) {
                logger.debug(CONFIG, "located", newService);
                if (service.isPresent()) {
                    final int op = service.get().getPriority();
                    final int np = newService.getPriority();
                    if (op < np) {
                        service = of(newService);
                    } else if (op == np) {
                        // Mind you that the loader may return multiple class
                        // instances with an equal name which are loaded by
                        // different class loaders.
                        if (!service.getClass().getName().equals(newService.getClass().getName()))
                            logger.warn("collision", op, service.get(), newService);
                    }
                } else {
                    service = of(newService);
                }
            }
        }
        if (service.isPresent()) {
            logger.debug(CONFIG, "selecting", service);
            return service.get();
        } else {
            throw new ServiceConfigurationError(
                    new BundledMessage(ServiceLocator.class, "null", spec).toString());
        }
    }

    private <S extends LocatableFunction<?>> List<S> functions(final Class<S> spec) {
        final List<S> list = new ArrayList<S>();
        for (final S service : loader.instancesOf(spec)) {
            list.add(service);
        }
        list.sort(new LocatableComparator());
        for (final S service : list) {
            logger.debug(CONFIG, "selecting", service);
        }
        return list;
    }
}
