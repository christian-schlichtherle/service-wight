/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * A provider of some service which is generated from a list of service providers and a list of service filters.
 * This class enables callers of the various {@code provider} methods in {@link ServiceLocator} to
 * introspect the results of the service location process and potentially modify it.
 *
 * @param <S>  the type of the service.
 * @param <SP> the type of the service providers.
 * @param <SF> the type of the service filters.
 * @author Christian Schlichtherle
 */
public final class CompositeProvider<S, SP extends Supplier<S>, SF extends UnaryOperator<S>> implements Supplier<S> {

    private final List<SP> providers;
    private final List<SF> filters;

    /**
     * Constructs a composite provider.
     *
     * @param providers a non-empty list of service providers.
     *                  Only the first element is used on a call to {@link #get()}.
     * @param filters   a (possibly empty) list of service filters.
     *                  All elements are used in order on a call to {@link #get()}.
     */
    public CompositeProvider(final List<SP> providers, final List<SF> filters) {
        if (providers.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.providers = new ArrayList<>(providers);
        this.filters = new ArrayList<>(filters);
    }

    /**
     * Returns a protective copy of the list of service providers.
     * The list is never empty.
     */
    public List<SP> providers() {
        return new ArrayList<>(providers);
    }

    /**
     * Returns a protective copy of the list of service filters.
     * The list may be empty.
     */
    public List<SF> filters() {
        return new ArrayList<>(filters);
    }

    @Override
    public S get() {
        S product = providers.get(0).get();
        for (UnaryOperator<S> filter : filters) {
            product = filter.apply(product);
        }
        return product;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[providers = " + providers + ", filters = " + filters + ']';
    }
}
