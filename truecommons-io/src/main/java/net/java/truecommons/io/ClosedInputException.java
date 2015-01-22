/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truecommons.io;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Indicates that an input resource (stream, channel etc.) has been closed.
 *
 * @see    ClosedOutputException
 * @author Christian Schlichtherle
 */
@ThreadSafe
public class ClosedInputException extends ClosedStreamException {
    private static final long serialVersionUID = 4563928734723923649L;
}
