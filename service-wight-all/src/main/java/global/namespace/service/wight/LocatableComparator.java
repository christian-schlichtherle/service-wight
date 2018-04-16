/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight;

import java.util.Comparator;

/**
 * Compares {@link LocatableService}s.
 *
 * @author Christian Schlichtherle
 */
class LocatableComparator implements Comparator<LocatableService> {

    public int compare(LocatableService o1, LocatableService o2) {
        return Integer.compare(o1.getPriority(), o2.getPriority());
    }
}
