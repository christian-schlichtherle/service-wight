/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight;

import global.namespace.service.wight.function.Factory;
import global.namespace.service.wight.function.Mapping;

import java.util.List;

/** @author Christian Schlichtherle */
final class FactoryWithSomeFunctions<P> extends ProviderWithSomeFunctions<P> implements Factory<P> {

    FactoryWithSomeFunctions(Factory<P> factory, List<? extends Mapping<P>> functions) {
        super(factory, functions);
    }
}
