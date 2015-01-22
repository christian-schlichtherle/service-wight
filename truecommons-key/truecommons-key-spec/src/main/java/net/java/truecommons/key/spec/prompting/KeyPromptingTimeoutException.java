/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truecommons.key.spec.prompting;

import javax.annotation.CheckForNull;
import javax.annotation.concurrent.ThreadSafe;
import net.java.truecommons.key.spec.UnknownKeyException;

/**
 * Thrown to indicate that prompting for a key to open or create a
 * protected resource has timed out.
 *
 * @since  TrueCommons 2.2
 * @author Christian Schlichtherle
 */
@ThreadSafe
public class KeyPromptingTimeoutException extends UnknownKeyException {
    private static final long serialVersionUID = 7656348612765052586L;

    public KeyPromptingTimeoutException() {
        super("Key prompting has timed out!");
    }

    public KeyPromptingTimeoutException(@CheckForNull Throwable cause) {
        super("Key prompting has timed out!");
        super.initCause(cause);
    }
}