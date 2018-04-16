/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truecommons3.services;

import java.util.List;

/** @author Christian Schlichtherle */
final class FactoryWithSomeFunctions<P> extends ProviderWithSomeFunctions<P> implements Factory<P> {

    FactoryWithSomeFunctions(Factory<P> factory, List<? extends Function<P>> functions) {
        super(factory, functions);
    }
}
