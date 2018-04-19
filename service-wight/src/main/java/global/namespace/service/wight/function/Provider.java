/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.function;

import java.util.function.Supplier;

/**
 * Provides some product.
 * <p>
 * Like with its superinterface, there is no requirement that a new or distinct result should be returned each
 * time the provider is called.
 *
 * @param <P> the type of the product.
 * @author Christian Schlichtherle
 */
@FunctionalInterface
public interface Provider<P> extends Supplier<P> {

    /**
     * Returns some product.
     *
     * @return some product.
     */
    @Override
    P get();
}
