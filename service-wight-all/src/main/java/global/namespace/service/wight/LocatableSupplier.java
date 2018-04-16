/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight;

import java.util.function.Supplier;

/**
 * A locatable supplier.
 * <p>
 * If multiple supplier classes get located on the class path at run time,
 * the instance with the greatest {@linkplain #getPriority() priority} gets
 * selected.
 * <p>
 * Implementations should be thread-safe.
 *
 * @see    ServiceLocator
 * @param  <P> the type of the products to provide.
 * @author Christian Schlichtherle
 */
public abstract class LocatableSupplier<P> extends LocatableService implements Supplier<P> { }
