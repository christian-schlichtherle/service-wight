/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that the annotated type is an implementation of a locatable service.
 *
 * @author Christian Schlichtherle
 */
@Target(TYPE)
@Documented
@Retention(RUNTIME)
public @interface ServiceImplementation {

    /**
     * Returns the service interface classes.
     * If empty, all superclasses and implemented interfaces get scanned for {@link ServiceInterface} annotations.
     *
     * @return The service interface classes.
     */
    Class<?>[] value() default {};

    /**
     * Returns the priority of this service implementation.
     */
    int priority() default 0;
}
