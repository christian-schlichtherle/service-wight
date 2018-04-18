/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.annotation;

import java.lang.annotation.*;

/**
 * Indicates that the annotated class implements a locatable service.
 *
 * @see    <a href="package-summary.html">Package Summary</a>
 * @since  TrueCommons 2.1
 * @author Christian Schlichtherle
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceImplementation {

    /**
     * Returns the service interface classes.
     * If empty, all superclasses and implemented interfaces get scanned for
     * {@link ServiceInterface} annotations.
     *
     * @return The service interface classes.
     */
    Class<?>[] value() default {};

    int priority() default 0;
}
