/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.function;

/**
 * Creates products.
 * <p>
 * Implementations should be thread-safe.
 *
 * @param  <P> the type of the products to create.
 * @author Christian Schlichtherle
 */
@FunctionalInterface
public interface Factory<P> extends Provider<P> {

    /**
     * Returns a <em>new</em> product upon each call.
     *
     * @return A <em>new</em> product.
     */
    @Override
    P get();
}