/*
 * Copyright (C) 2012 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truecommons.services;

import javax.annotation.concurrent.ThreadSafe;

/**
 * A locatable function.
 * <p>
 * If multiple function classes get located on the class path at run time,
 * the instances get applied in ascending order of their
 * {@linkplain #getPriority() priority} so that the result of the instance
 * with the greatest number becomes the result of the entire function chain.
 * <p>
 * Implementations should be thread-safe.
 *
 * @see    Locator
 * @param  <P> the type of the products to map.
 * @author Christian Schlichtherle
 */
@ThreadSafe
public abstract class LocatableFunction<P>
extends Locatable implements Function<P> {
}
