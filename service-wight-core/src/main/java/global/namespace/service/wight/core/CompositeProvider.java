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
 * A provider of some product which is composed of a list of product providers and a list of product transformations.
 * This class is provided to allow callers of the various {@code provider} methods in {@link ServiceLocator} to
 * introspect the results of the service location process and potentially modify it.
 *
 * @param <P> the type of the product
 * @param <PP> the type of the product providers
 * @param <PT> the type of the product transformations
 * @author Christian Schlichtherle
 */
public final class CompositeProvider<P, PP extends Supplier<P>, PT extends UnaryOperator<P>> implements Supplier<P> {

    private final List<PP> providers;
    private final List<PT> transformations;

    /**
     * Constructs a composite provider.
     *
     * @param providers a non-empty list of product providers.
     *                  Only the first element is used on a call to {@link #get()}.
     * @param transformations a (possibly empty) list of product transformations.
     *                        All elements are used in order on a call to {@link #get()}.
     */
    public CompositeProvider(final List<PP> providers, final List<PT> transformations) {
        if (providers.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.providers = new ArrayList<>(providers);
        this.transformations = new ArrayList<>(transformations);
    }

    /**
     * Returns a protective copy of the list of product providers.
     * The list is never empty.
     */
    public List<PP> providers() { return new ArrayList<>(providers); }

    /**
     * Returns a protective copy of the list of product transformations.
     * The list may be empty.
     */
    public List<PT> transformations() { return new ArrayList<>(transformations); }

    @Override
    public P get() {
        P product = providers.get(0).get();
        for (UnaryOperator<P> operation : transformations) {
            product = operation.apply(product);
        }
        return product;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[providers = " + providers + ", transformations = " + transformations + ']';
    }
}
