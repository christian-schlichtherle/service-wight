/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.annotation.processing;

import global.namespace.service.wight.annotation.ServiceImplementation;
import global.namespace.service.wight.annotation.ServiceInterface;
import lombok.val;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;
import javax.lang.model.util.SimpleTypeVisitor8;
import java.io.IOException;
import java.util.*;

import static java.util.Comparator.comparing;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;
import static javax.lang.model.element.Modifier.*;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

/**
 * Processes the {@link ServiceImplementation} annotation to register the annotated type in a service provider
 * configuration file in {@code META-INF/services}.
 *
 * @author Christian Schlichtherle
 */
@SupportedAnnotationTypes("global.namespace.service.wight.annotation.ServiceImplementation")
public final class ServiceImplementationProcessor extends ServiceAnnnotationProcessor {

    private static final Comparator<TypeElement> TYPE_ELEMENT_COMPARATOR =
            comparing(o -> o.getQualifiedName().toString());

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        val registry = new Registry();
        for (val elem : roundEnv.getElementsAnnotatedWith(ServiceImplementation.class)) {
            if (elem instanceof TypeElement) {
                val impl = (TypeElement) elem;
                if (valid(impl)) {
                    if (!processAnnotations(impl, registry)) {
                        if (!processTypeHierarchy(impl, registry)) {
                            error("Cannot find any service interface.", impl);
                        }
                    }
                }
            } else {
                warning("Expected a type element here.", elem);
            }
        }
        registry.persist();
        return true;
    }

    private boolean valid(final TypeElement impl) {
        {
            val modifiers = impl.getModifiers();
            if (!modifiers.contains(PUBLIC)
                    || modifiers.contains(ABSTRACT)
                    || impl.getKind() != CLASS) {
                error("Not a public and non-abstract class.", impl);
                return false;
            }
            if (impl.getNestingKind().isNested()) {
                if (!modifiers.contains(STATIC)) {
                    error("Impossible to instantiate without an instance of the enclosing class.", impl);
                    return false;
                }
            }
        }
        val constructors = new LinkedList<ExecutableElement>();
        for (val elem : impl.getEnclosedElements()) {
            if (elem.getKind() == CONSTRUCTOR) {
                constructors.add((ExecutableElement) elem);
            }
        }
        if (!constructors.isEmpty() && !valid(constructors)) {
            error("No public constructor with zero parameters available.", impl);
            return false;
        }
        return true;
    }

    private boolean valid(final Collection<? extends ExecutableElement> ctors) {
        for (val ctor : ctors) {
            if (valid(ctor)) {
                return true;
            }
        }
        return false;
    }

    private boolean valid(ExecutableElement ctor) {
        return ctor.getModifiers().contains(PUBLIC) && ctor.getParameters().isEmpty();
    }

    private boolean processAnnotations(final TypeElement impl, final Registry registry) {
        val implType = (DeclaredType) impl.asType();
        for (val mirror : processingEnv.getElementUtils().getAllAnnotationMirrors(impl)) {
            if (!ServiceImplementation.class.getName().equals(
                    ((TypeElement) mirror.getAnnotationType().asElement()).getQualifiedName().toString())) {
                continue;
            }
            val values = mirror.getElementValues();
            for (val entry : values.entrySet()) {
                val element = entry.getKey();
                if (!"value".equals(element.getSimpleName().toString())) {
                    continue;
                }

                class Visitor extends SimpleAnnotationValueVisitor8<Boolean, Void> {

                    private Visitor() {
                        super(false);
                    }

                    @Override
                    public Boolean visitType(final TypeMirror type, final Void p) {
                        if (processingEnv.getTypeUtils().isAssignable(implType, type)) {
                            registry.add(impl, (TypeElement) ((DeclaredType) type).asElement());
                        } else {
                            error("Unassignable to " + type + ".", impl);
                        }
                        return Boolean.TRUE;
                    }

                    @Override
                    public Boolean visitArray(final List<? extends AnnotationValue> values, final Void p) {
                        boolean found = false;
                        for (val value : values) {
                            found |= value.accept(this, p);
                        }
                        return found;
                    }
                }

                return entry.getValue().accept(new Visitor(), null);
            }
        }
        return false;
    }

    private boolean processTypeHierarchy(final TypeElement impl, final Registry registry) {

        class Visitor extends SimpleTypeVisitor8<Boolean, Void> {

            private Visitor() {
                super(false);
            }

            @Override
            public Boolean visitDeclared(final DeclaredType type, final Void p) {
                boolean found = false;
                final TypeElement elem = (TypeElement) type.asElement();
                if (null != elem.getAnnotation(ServiceInterface.class)) {
                    found = true;
                    registry.add(impl, elem);
                }
                for (final TypeMirror m : elem.getInterfaces()) {
                    found |= m.accept(this, p);
                }
                return elem.getSuperclass().accept(this, p) || found;
            }
        }

        return impl.asType().accept(new Visitor(), null);
    }

    private final class Registry {

        final Elements elements = processingEnv.getElementUtils();
        final Map<TypeElement, Collection<TypeElement>> services = new HashMap<>();

        void add(final TypeElement impl, final TypeElement iface) {
            Collection<TypeElement> coll = services.get(iface);
            if (null == coll) {
                coll = new TreeSet<>(TYPE_ELEMENT_COMPARATOR);
            }
            coll.add(impl);
            services.put(iface, coll);
        }

        void persist() {
            val filer = processingEnv.getFiler();
            for (val entry : services.entrySet()) {
                val iface = entry.getKey();
                val coll = entry.getValue();
                if (!coll.isEmpty()) {
                    val path = "META-INF/services/" + name(iface);
                    try {
                        val fo = filer.createResource(CLASS_OUTPUT, "", path);
                        try (val w = fo.openWriter()) {
                            for (val impl : coll) {
                                w.append(name(impl)).append("\n");
                                debug(String.format(Locale.ENGLISH, "Registered in: %s", path), impl);
                            }
                        }
                    } catch (IOException e) {
                        error(String.format(Locale.ENGLISH, "Failed to register %d service implementation class(es) at: %s: %s", coll.size(), path, e.getMessage()));
                    }
                }
            }
        }

        CharSequence name(TypeElement elem) {
            return elements.getBinaryName(elem);
        }
    }
}
