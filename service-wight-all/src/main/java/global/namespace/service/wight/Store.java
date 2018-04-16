/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight;

import java.util.function.Supplier;

/** @author Christian Schlichtherle */
final class Store<P> implements Container<P> {

    private final P product;

    Store(final Supplier<P> supplier) { this.product = supplier.get(); }

    @Override public P get() { return product; }
}
