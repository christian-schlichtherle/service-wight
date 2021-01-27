/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Indicates that the annotated type is the interface of a locatable service.
 *
 * @author Christian Schlichtherle
 */
@Target(TYPE)
@Documented
public @interface ServiceInterface {
}
