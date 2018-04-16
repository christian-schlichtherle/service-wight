/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.annotation.processing;

import java.util.*;
import javax.annotation.processing.*;
import javax.lang.model.*;
import javax.lang.model.element.*;

import static javax.lang.model.element.ElementKind.*;
import static javax.lang.model.element.Modifier.*;

import global.namespace.service.wight.annotation.ServiceInterface;

/**
 * Processes the {@link ServiceInterface} annotation.
 *
 * @author Christian Schlichtherle
 * @since TrueCommons 2.1
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("*")
public final class ServiceInterfaceProcessor extends ServiceAnnnotationProcessor {

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        for (final Element elem : roundEnv.getElementsAnnotatedWith(ServiceInterface.class)) {
            if (elem instanceof TypeElement) {
                valid((TypeElement) elem);
            } else {
                warning("Expected a type element here.", elem);
            }
        }
        return false; // critical!
    }

    private boolean valid(final TypeElement iface) {
        {
            final Set<Modifier> modifiers = iface.getModifiers();
            if (!modifiers.contains(PUBLIC) || modifiers.contains(FINAL)) {
                return error("Not a public and non-final class or interface.", iface);
            }
            if (iface.getNestingKind().isNested()) {
                if (!modifiers.contains(STATIC)) {
                    return error("Impossible to implement outside of the lexical scope of the enclosing class.", iface);
                }
                warning("Bad practice: Not a top-level class or interface.", iface);
            }
        }
        final Collection<ExecutableElement> constructors = new LinkedList<>();
        for (final Element elem : iface.getEnclosedElements()) {
            if (elem.getKind() == CONSTRUCTOR) {
                constructors.add((ExecutableElement) elem);
            }
        }
        return constructors.isEmpty() || valid(constructors) ||
                error("No public or protected constructor with zero parameters available.", iface);
    }

    private boolean valid(final Collection<ExecutableElement> constructors) {
        for (final ExecutableElement ctor : constructors) {
            if (valid(ctor)) {
                return true;
            }
        }
        return false;
    }

    private boolean valid(final ExecutableElement ctor) {
        return (ctor.getModifiers().contains(PUBLIC) ||
                ctor.getModifiers().contains(PROTECTED)) &&
                ctor.getParameters().isEmpty();
    }
}
