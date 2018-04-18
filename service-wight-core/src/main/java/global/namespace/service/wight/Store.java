/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight;

import global.namespace.service.wight.function.Container;
import global.namespace.service.wight.function.Provider;

/** @author Christian Schlichtherle */
final class Store<P> implements Container<P> {

    private final Provider<P> provider;

    private volatile P product;

    Store(final Provider<P> provider) { this.provider = provider; }

    public P get() {
        if (null == product) {
            synchronized (this) {
                if (null == product) {
                    product = provider.get();
                }
            }
        }
        return product;
    }
}
