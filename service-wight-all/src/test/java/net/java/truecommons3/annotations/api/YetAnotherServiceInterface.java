/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truecommons3.annotations.api;

import net.java.truecommons3.annotations.ServiceInterface;

/**
 * Yet another service interface.
 *
 * @author Christian Schlichtherle
 */
@ServiceInterface
public abstract class YetAnotherServiceInterface {

    protected YetAnotherServiceInterface() { }

    @ServiceInterface
    public static abstract class BadPracticeInterface { }
}
