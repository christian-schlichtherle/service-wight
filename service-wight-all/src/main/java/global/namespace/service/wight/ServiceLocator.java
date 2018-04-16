/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
 * Finally, depending on the requesting method either a container or a factory
 * gets created which will use the instantiated supplier and functions
 * to obtain a product and map it in order of their priorities.
 *
 * @see    ServiceLoader
 * @author Christian Schlichtherle
 */
public final class ServiceLocator {

    private static final Logger log = LoggerFactory.getLogger(ServiceLocator.class);

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
    public <P> Factory<P> factory(Class<? extends LocatableFactory<P>> factory) {
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
                                  final Class<? extends LocatableFunction<P>> functions) {
        return factory(factory, of(functions));
    }

    private <P> Factory<P> factory(final Class<? extends LocatableFactory<P>> factory,
                                   final Optional<Class<? extends LocatableFunction<P>>> functions) {
        final LocatableFactory<P> p = supplier(factory);
        final List<? extends LocatableFunction<P>> f = functions.map(this::functions).orElseGet(Collections::emptyList);
        return f.isEmpty() ? p : new FactoryWithSomeFunctions<P>(p, f);
    }

    /**
     * Creates a new container with a single product.
     *
     * @param  <P> the type of the product to contain.
     * @param  supplier the class of the locatable supplier for the product.
     * @return A new container with a single product.
     * @throws ServiceConfigurationError if loading or instantiating
     *         a located class fails for some reason.
     */
    public <P> Container<P> container(Class<? extends LocatableSupplier<P>> supplier) {
        return container(supplier, empty());
    }

    /**
     * Creates a new container with a single product.
     *
     * @param  <P> the type of the product to contain.
     * @param  supplier the class of the locatable supplier for the product.
     * @param  decorator the class of the locatable decoractors for the product.
     * @return A new container with a single product.
     * @throws ServiceConfigurationError if loading or instantiating
     *         a located class fails for some reason.
     */
    public <P> Container<P> container(Class<? extends LocatableSupplier<P>> supplier,
                                      Class<? extends LocatableDecorator<P>> decorator) {
        return container(supplier, of(decorator));
    }

    private <P> Container<P> container(final Class<? extends LocatableSupplier<P>> supplier,
                                       final Optional<Class<? extends LocatableDecorator<P>>> decorator) {
        final LocatableSupplier<P> p = supplier(supplier);
        final List<? extends LocatableDecorator<P>> d = decorator.map(this::functions).orElseGet(Collections::emptyList);
        return new Store<P>(d.isEmpty() ? p : new SupplierWithSomeFunctions<P>(p, d));
    }

    private <S extends LocatableSupplier<?>> S supplier(final Class<S> iface) {
        Optional<S> service = loader.instanceOf(iface, Optional.empty());
        if (!service.isPresent()) {
            for (final S newService : loader.instancesOf(iface)) {
                log.debug("Located {}.", newService);
                if (service.isPresent()) {
                    final int op = service.get().getPriority();
                    final int np = newService.getPriority();
                    if (op < np) {
                        service = of(newService);
                    } else if (op == np) {
                        // Mind you that the loader may return multiple class
                        // instances with an equal name which are loaded by
                        // different class loaders.
                        if (!service.getClass().getName().equals(newService.getClass().getName())) {
                            log.warn("Found two services with the same priority {}\nFirst: {}\nSecond: {}",
                                    op, service.get(), newService);
                        }
                    }
                } else {
                    service = of(newService);
                }
            }
        }
        return service.map(s -> {
            log.debug("Selecting {}.", s);
            return s;
        }).orElseThrow(() -> new ServiceConfigurationError("No service located for " + iface + "."));
    }

    private <S extends LocatableFunction<?>> List<S> functions(final Class<S> iface) {
        final List<S> list = new ArrayList<S>();
        loader.instancesOf(iface).forEach(list::add);
        list.sort(new LocatableComparator());
        list.forEach(service -> log.debug("Selecting {}.", service));
        return list;
    }
}
