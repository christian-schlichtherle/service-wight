/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight;

import global.namespace.service.wight.function.Factory;
import global.namespace.service.wight.function.Mapping;

import java.util.List;

/**
 * A factory of some product which is composed of a list of factories and a list of mappings for the product.
 * This class is provided to allow callers of the various {@code factory} and {@code container} methods in
 * {@link ServiceLocator} to analyze the findings of the service location process and potentially modify it.
 *
 * @author Christian Schlichtherle
 */
public final class CompositeFactory<P> extends CompositeProvider<P, Factory<P>, Mapping<P>> implements Factory<P> {

    /**
     * Constructs a composite factory.
     *
     * @param factories a non-empty list of factories. Only the first element is used on a call to {@link #get()}.
     * @param mappings a (possibly empty) list of mappings. All elements are used in order on a call to {@link #get()}.
     */
    public CompositeFactory(List<Factory<P>> factories, List<Mapping<P>> mappings) { super(factories, mappings); }
}
