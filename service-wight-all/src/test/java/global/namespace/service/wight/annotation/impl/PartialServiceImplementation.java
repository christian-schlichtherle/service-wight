/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.annotation.impl;

import global.namespace.service.wight.annotation.ServiceImplementation;
import global.namespace.service.wight.annotation.api.YetAnotherServiceInterface;

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
