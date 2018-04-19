/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight;

import global.namespace.service.wight.function.Container;
import global.namespace.service.wight.function.Decorator;
import global.namespace.service.wight.function.Provider;

import java.util.List;

/**
 * A container of some product which is composed of a list of providers and a list of decorators for the product.
 * This class is provided to allow callers of the various {@code factory} and {@code container} methods in
 * {@link ServiceLocator} to analyze the findings of the service location process and potentially modify it.
 *
 * @author Christian Schlichtherle
 */
public final class CompositeContainer<P> extends CompositeProvider<P, Provider<P>, Decorator<P>> implements Container<P> {

    private volatile P product;

    /**
     * Constructs a composite container.
     *
     * @param providers a non-empty list of providers. Only the first element is used on a call to {@link #get()}.
     * @param decorators a (possibly empty) list of decorators. All elements are used in order on a call to {@link #get()}.
     */
    public CompositeContainer(List<Provider<P>> providers, List<Decorator<P>> decorators) { super(providers, decorators); }

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
