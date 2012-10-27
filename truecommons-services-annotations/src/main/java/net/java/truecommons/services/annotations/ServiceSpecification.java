/*
 * Copyright (C) 2005-2012 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truecommons.services.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ServiceLoader;

/**
 * Indicates that the annotated class or interface specifies a locatable service.
 *
 * @see    ServiceImplementation
 * @see    ServiceLoader
 * @see    Locator
 * @author Christian Schlichtherle
 */
@Target(ElementType.TYPE)
@Documented
public @interface ServiceSpecification { }