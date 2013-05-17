/*
 * Copyright (C) 2005-2013 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truecommons.key.swing.util;

import java.util.EventListener;

/**
 * Used to notify listeners of an {@link net.java.truecommons.key.swing.util.EnhancedPanel}
 * that its ancestor window is shown or hidden.
 *
 * @since  TrueCommons 2.2
 * @author Christian Schlichtherle
 */
public interface PanelListener extends EventListener {

    /**
     * Invoked when the ancestor window of an
     * {@link net.java.truecommons.key.swing.util.EnhancedPanel} is shown.
     *
     * @param evt A panel event.
     */
    void ancestorWindowShown(PanelEvent evt);

    /**
     * Invoked when the ancestor window of an
     * {@link net.java.truecommons.key.swing.util.EnhancedPanel} is hidden.
     *
     * @param evt A panel event.
     */
    void ancestorWindowHidden(PanelEvent evt);
}
