/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight;

import java.util.List;
import java.util.function.Supplier;

/** @author Christian Schlichtherle */
class SupplierWithSomeFunctions<P> implements Supplier<P> {

    private final Supplier<P> supplier;
    private final List<? extends Function<P>> functions;

    SupplierWithSomeFunctions(final Supplier<P> supplier, final List<? extends Function<P>> functions) {
        assert 0 != functions.size();
        this.supplier = supplier;
        this.functions = functions;
    }

    @Override
    public P get() {
        P product = supplier.get();
        for (Function<P> function : functions) {
            product = function.apply(product);
        }
        return product;
    }
}
