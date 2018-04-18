/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight;

import global.namespace.service.wight.function.Mapping;

/**
 * A locatable service which maps products.
 * <p>
 * If multiple mapping classes get located on the class path at run time,
 * the instances get applied in ascending order of their
 * {@linkplain #getPriority() priority} so that the result of the instance
 * with the greatest number becomes the result of the entire function chain.
 * <p>
 * Implementations should be thread-safe.
 *
 * @see    ServiceLocator
 * @param  <P> the type of the products to map.
 * @author Christian Schlichtherle
 */
public abstract class LocatableMapping<P> extends LocatableService implements Mapping<P> { }
