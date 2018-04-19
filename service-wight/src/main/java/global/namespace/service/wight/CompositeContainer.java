/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight;

import global.namespace.service.wight.function.Container;
import global.namespace.service.wight.function.Mapping;
import global.namespace.service.wight.function.Provider;

import java.util.List;

/**
 * A container of some product which is composed of a list of providers and a list of decorators for the product.
 * This class is provided to allow callers of the various {@code factory} and {@code container} methods in
 * {@link ServiceLocator} to analyze the findings of the service location process and potentially modify it.
 *
 * @author Christian Schlichtherle
 */
public final class
CompositeContainer<P, PP extends Provider<P>, MP extends Mapping<P>> extends CompositeProvider<P, PP, MP>
        implements Container<P> {

    private volatile P product;

    /**
     * Constructs a composite container.
     *
     * @param providers a non-empty list of providers. Only the first element is used on a call to {@link #get()}.
     * @param mappings a (possibly empty) list of mappings. All elements are used in order on a call to {@link #get()}.
     */
    public CompositeContainer(List<PP> providers, List<MP> mappings) { super(providers, mappings); }

    @Override
    public P get() {
        if (null == product) {
            synchronized (this) {
                if (null == product) {
                    product = super.get();
                }
            }
        }
        return product;
    }
}
