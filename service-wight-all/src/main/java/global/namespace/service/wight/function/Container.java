/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.function;

/**
 * Contains a single product.
 * <p>
 * Implementations should be thread-safe.
 *
 * @param  <P> the type of the product to contain.
 * @author Christian Schlichtherle
 */
@FunctionalInterface
public interface Container<P> extends Provider<P> {

    /** Returns the <em>same</em> product on each call. */
    @Override
    P get();
}
