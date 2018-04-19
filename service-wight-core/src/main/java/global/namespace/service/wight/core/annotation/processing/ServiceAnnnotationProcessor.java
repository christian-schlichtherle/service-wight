/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.core.annotation.processing;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;

import static javax.tools.Diagnostic.Kind.*;

/**
 * Common super class for {@link ServiceInterfaceProcessor} and
 * {@link ServiceImplementationProcessor}.
 *
 * @author Christian Schlichtherle
 */
abstract class ServiceAnnnotationProcessor extends AbstractProcessor {

    final void debug(CharSequence msg, Element e) { getMessager().printMessage(NOTE, msg, e); }

    final void warning(CharSequence message, Element e) { getMessager().printMessage(MANDATORY_WARNING, message, e); }

    final void error(CharSequence message) { getMessager().printMessage(ERROR, message); }

    final void error(CharSequence message, Element e) { getMessager().printMessage(ERROR, message, e); }

    private Messager getMessager() { return processingEnv.getMessager(); }
}
