/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight;

import java.util.List;

/** @author Christian Schlichtherle */
final class FactoryWithSomeFunctions<P> extends SupplierWithSomeFunctions<P> implements Factory<P> {

    FactoryWithSomeFunctions(Factory<P> factory, List<? extends Function<P>> functions) {
        super(factory, functions);
    }
}
