/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.function;

/**
 * Maps products.
 * <p>
 * Implementations should be thread-safe.
 *
 * @param  <P> the type of the products to map.
 * @author Christian Schlichtherle
 */
@FunctionalInterface
public interface Mapping<P> {

    /**
     * Maps the given product.
     *
     * @param  product the product to map.
     * @return A new product or the same, possibly modified, product.
     */
    P apply(P product);
}