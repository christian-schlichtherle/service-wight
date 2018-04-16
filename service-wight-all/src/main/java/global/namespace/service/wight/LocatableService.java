/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight;

import java.util.Locale;

/**
 * A locatable service.
 *
 * @see    ServiceLocator
 * @author Christian Schlichtherle
 */
public abstract class LocatableService {

    /**
     * Returns a priority to help {@link ServiceLocator}s to prioritize this object.
     * <p>
     * The implementation in the class {@link LocatableService} returns zero.
     *
     * @return A priority to help {@link ServiceLocator}s to prioritize this object.
     */
    public int getPriority() { return 0; }

    /**
     * Returns a string representation of this locatable object for debugging
     * and logging purposes.
     */
    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%s[priority=%d]", getClass().getName(), getPriority());
    }
}
