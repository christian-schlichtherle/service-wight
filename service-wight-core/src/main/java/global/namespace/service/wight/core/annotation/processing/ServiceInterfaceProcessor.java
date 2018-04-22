/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.core.annotation.processing;

import global.namespace.service.wight.core.annotation.ServiceInterface;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import static javax.lang.model.element.ElementKind.CONSTRUCTOR;
import static javax.lang.model.element.Modifier.*;

/**
 * Processes the {@link ServiceInterface} annotation.
 *
 * @author Christian Schlichtherle
 * @since TrueCommons 2.1
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
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

    private void valid(final TypeElement iface) {
        {
            final Set<Modifier> modifiers = iface.getModifiers();
            if (!modifiers.contains(PUBLIC) || modifiers.contains(FINAL)) {
                error("Not a public and non-final class or interface.", iface);
                return;
            }
            if (iface.getNestingKind().isNested()) {
                if (!modifiers.contains(STATIC)) {
                    error("Impossible to implement outside of the lexical scope of the enclosing class.", iface);
                    return;
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
        if (!constructors.isEmpty() && !valid(constructors)) {
            error("No public or protected constructor with zero parameters available.", iface);
        }
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
