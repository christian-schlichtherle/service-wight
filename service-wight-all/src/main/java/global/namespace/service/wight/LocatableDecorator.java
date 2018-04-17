/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight;

import global.namespace.service.wight.function.Decorator;

/**
 * A locatable mapping for decorating products.
 * For best results, clients should create another abstract subclass which just
 * specifies the type parameter {@code P}.
 * The following example accompanies the example for {@link LocatableContainer},
 * so the type parameter is specified as {@link String} again:
 * <p>
 * <pre>{@code
 * package com.company.api;
 *
 * import LocatableDecorator;
 *
 * public abstract class StringDecorator
 * extends LocatableDecorator<StringBuilder> {
 * }
 * }</pre>
 * <p>
 * An implementation could now implement this service as follows:
 * <pre>{@code
 * package com.company.impl;
 *
 * import com.company.api.StringDecorator;
 *
 * public class SmalltalkDecorator extends StringDecorator {
 *     \@Override
 *     public String apply(String s) {
 *         // decorate the given string with a new string and return it!
 *         return s + " How do you do?";
 *     }
 * }
 * }</pre>
 * <p>
 * Next, the implementation needs to advertise its service by providing a file
 * with the name {@code META-INF/services/com.company.api.StringDecorator}
 * on the run time class path with the following single line content:
 * <pre>{@code
 * com.company.impl.SmalltalkDecorator
 * }</pre>
 * <p>
 * If multiple modifier services are locatable on the class path at run
 * time, they are applied in ascending order of their
 * {@linkplain #getPriority() priority} so that the result of the modifier
 * service with the greatest number becomes the result of the entire
 * modifier chain.
 * <p>
 * Finally, a client could now simply compose a container with some decorators
 * according to the {@code StringContainer} and
 * {@code StringDecorator} interface by calling:
 * <pre>{@code
 * package com.company.client;
 *
 * import net.java.truecommons3.services.Locator;
 * import com.company.api.StringContainer;
 * import com.company.api.StringDecorator;
 *
 * public class Main {
 *     public static void main(String[] args) {
 *         Locator l = new Locator(Main.class); // specify calling class
 *         Container<String> c = l.factory(StringContainer.class,
 *                                         StringDecorator.class);
 *         String s = c.apply(); // obtain product
 *         System.out.println(s); // use product
 *     }
 * }
 * }</pre>
 * <p>
 * Note that multiple calls to {@code c.apply()} would always return the same
 * product again because {@code c} is a container, not a factory.
 * <p>
 * Implementations should be thread-safe.
 *
 * @see    ServiceLocator
 * @param  <P> the type of the products to decorate.
 * @author Christian Schlichtherle
 */
public abstract class LocatableDecorator<P> extends LocatableMapping<P> implements Decorator<P> { }