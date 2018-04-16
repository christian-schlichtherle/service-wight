/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.function;

/**
 * Provides one or more products.
 * <p>
 * Implementations should be thread-safe.
 *
 * @param <P> the type of the product(s).
 * @author Christian Schlichtherle
 */
@FunctionalInterface
public interface Provider<P> {

    /**
     * Provides a product.
     * There is no requirement that a new or distinct product should be returned each time the provider is invoked.
     *
     * @return some product.
     */
    P get();
}
