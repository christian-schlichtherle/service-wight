/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight;

import global.namespace.service.wight.function.Mapping;
import global.namespace.service.wight.function.Provider;

import java.util.List;

/** @author Christian Schlichtherle */
class ProviderWithSomeMappings<P> implements Provider<P> {

    private final Provider<P> provider;
    private final List<? extends Mapping<P>> mappings;

    ProviderWithSomeMappings(final Provider<P> provider, final List<? extends Mapping<P>> mappings) {
        assert 0 != mappings.size();
        this.provider = provider;
        this.mappings = mappings;
    }

    @Override
    public P get() {
        P product = provider.get();
        for (Mapping<P> mapping : mappings) {
            product = mapping.apply(product);
        }
        return product;
    }
}
