/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.function;

/**
 * Modifies products.
 * <p>
 * Implementations should be thread-safe.
 *
 * @param  <P> the type of the products to modify.
 * @author Christian Schlichtherle
 */
@FunctionalInterface
public interface Modifier<P> extends Mapping<P> {

    /**
     * Possibly modifies the given product and returns it again.
     *
     * @param  product the product to modify.
     * @return the <em>same</em>, but possibly modified, {@code product}.
     */
    @Override
    P apply(P product);
}
