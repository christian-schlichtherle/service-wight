/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truecommons3.services;

import javax.inject.Provider;
import java.util.List;

/** @author Christian Schlichtherle */
class ProviderWithSomeFunctions<P> implements Provider<P> {

    private final Provider<P> provider;
    private final List<? extends Function<P>> functions;

    ProviderWithSomeFunctions(final Provider<P> provider, final List<? extends Function<P>> functions) {
        assert 0 != functions.size();
        this.provider = provider;
        this.functions = functions;
    }

    @Override
    public P get() {
        P product = provider.get();
        for (Function<P> function : functions) {
            product = function.apply(product);
        }
        return product;
    }
}
