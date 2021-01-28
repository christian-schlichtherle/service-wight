/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.annotation.processing;

import global.namespace.service.wight.annotation.ServiceImplementation;
import global.namespace.service.wight.annotation.ServiceInterface;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.tools.FileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.Map.Entry;

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
        final Registry registry = new Registry();
        for (final Element elem : roundEnv.getElementsAnnotatedWith(ServiceImplementation.class)) {
            if (elem instanceof TypeElement) {
                final TypeElement impl = (TypeElement) elem;
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
            final Set<Modifier> modifiers = impl.getModifiers();
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
        final Collection<ExecutableElement> constructors = new LinkedList<>();
        for (final Element elem : impl.getEnclosedElements()) {
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

    private boolean valid(final Collection<ExecutableElement> ctors) {
        for (final ExecutableElement ctor : ctors) {
            if (valid(ctor)) {
                return true;
            }
        }
        return false;
    }

    private boolean valid(ExecutableElement ctor) {
        return ctor.getModifiers().contains(PUBLIC) && ctor.getParameters().isEmpty();
    }

    private boolean processAnnotations(
            final TypeElement impl,
            final Registry registry) {
        final DeclaredType implType = (DeclaredType) impl.asType();
        for (final AnnotationMirror mirror : processingEnv.getElementUtils().getAllAnnotationMirrors(impl)) {
            if (!ServiceImplementation.class.getName().equals(
                    ((TypeElement) mirror.getAnnotationType().asElement()).getQualifiedName().toString())) {
                continue;
            }
            final Map<? extends ExecutableElement, ? extends AnnotationValue>
                    values = mirror.getElementValues();
            for (final Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : values.entrySet()) {
                final ExecutableElement element = entry.getKey();
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
                        for (AnnotationValue value : values) {
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
            final Filer filer = processingEnv.getFiler();
            for (final Entry<TypeElement, Collection<TypeElement>> entry : services.entrySet()) {
                final TypeElement iface = entry.getKey();
                final Collection<TypeElement> coll = entry.getValue();
                if (!coll.isEmpty()) {
                    final String path = "META-INF/services/" + name(iface);
                    try {
                        final FileObject fo = filer.createResource(CLASS_OUTPUT, "", path);
                        try (Writer w = fo.openWriter()) {
                            for (final TypeElement impl : coll) {
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
