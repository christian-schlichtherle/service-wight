/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight;

import global.namespace.service.wight.function.Mapping;
import global.namespace.service.wight.function.Provider;

import java.util.ArrayList;
import java.util.List;

/**
 * A provider of some product which is composed of a list of providers and a list of mappings for the product.
 * This class is provided to allow callers of the various {@code factory} and {@code container} methods in
 * {@link ServiceLocator} to analyze the findings of the service location process and potentially modify it.
 *
 * @author Christian Schlichtherle
 */
public abstract class CompositeProvider<P, PP extends Provider<P>, MP extends Mapping<P>> implements Provider<P> {

    private final List<PP> providers;
    private final List<MP> mappings;

    /**
     * Constructs a composite provider.
     *
     * @param providers a non-empty list of providers. Only the first element is used on a call to {@link #get()}.
     * @param mappings a (possibly empty) list of mappings. All elements are used in order on a call to {@link #get()}.
     */
    protected CompositeProvider(final List<PP> providers, final List<MP> mappings) {
        if (providers.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.providers = new ArrayList<>(providers);
        this.mappings = new ArrayList<>(mappings);
    }

    /**
     * Returns a protective copy of the list of providers.
     * The list is never empty.
     */
    public List<PP> providers() { return new ArrayList<>(providers); }

    /**
     * Returns a protective copy of the list of mappings.
     * The list may be empty.
     */
    public List<MP> mappings() { return new ArrayList<>(mappings); }

    @Override
    public P get() {
        P product = providers.get(0).get();
        for (Mapping<P> mapping : mappings) {
            product = mapping.apply(product);
        }
        return product;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[providers = " + providers + ", mappings = " + mappings + ']';
    }
}
