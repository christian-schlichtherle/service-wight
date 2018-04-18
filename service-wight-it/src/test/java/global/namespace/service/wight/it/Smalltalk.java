package global.namespace.service.wight.it;

import global.namespace.service.wight.annotation.ServiceImplementation;
import global.namespace.service.wight.function.Decorator;

@ServiceImplementation(Decorator.class)
public final class Smalltalk implements Decorator<String> {

    @Override
    public String apply(String text) { return text + " How do you do?"; }
}
