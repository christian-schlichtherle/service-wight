/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.core.function;

/**
 * Maps products.
 * <p>
 * There is no requirement that a new or distinct result should be returned each time the provider is called.
 * So an implementation may decide to do any changes in place.
 *
 * @param  <P> the type of the product to map.
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
