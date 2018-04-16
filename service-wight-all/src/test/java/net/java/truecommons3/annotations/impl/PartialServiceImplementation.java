/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truecommons3.annotations.impl;

import net.java.truecommons3.annotations.ServiceImplementation;
import net.java.truecommons3.annotations.api.YetAnotherServiceInterface;

/**
 * Is this all?!
 *
 * @author Christian Schlichtherle
 */
@ServiceImplementation
public class PartialServiceImplementation
extends YetAnotherServiceInterface {

    @ServiceImplementation
    public static class BadPracticeImplementation
    extends BadPracticeInterface {
    }
}
