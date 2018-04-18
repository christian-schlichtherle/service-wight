/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight;

import global.namespace.service.wight.function.Factory;
import global.namespace.service.wight.function.Mapping;

import java.util.List;

/** @author Christian Schlichtherle */
final class FactoryWithSomeMappings<P> extends ProviderWithSomeMappings<P> implements Factory<P> {

    FactoryWithSomeMappings(Factory<P> factory, List<? extends Mapping<P>> mappings) { super(factory, mappings); }
}
