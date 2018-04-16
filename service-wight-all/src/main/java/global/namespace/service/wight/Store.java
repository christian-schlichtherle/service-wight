/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight;

import global.namespace.service.wight.function.Container;
import global.namespace.service.wight.function.Provider;

/** @author Christian Schlichtherle */
final class Store<P> implements Container<P> {

    private final P product;

    Store(final Provider<P> provider) { this.product = provider.get(); }

    public P get() { return product; }
}
