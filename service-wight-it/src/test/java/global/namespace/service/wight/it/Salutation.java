package global.namespace.service.wight.it;

import global.namespace.service.wight.annotation.ServiceImplementation;
import global.namespace.service.wight.function.Decorator;

import java.util.Locale;

@ServiceImplementation(value = Decorator.class, priority = -1)
public final class Salutation implements Decorator<String> {

    @Override
    public String apply(String text) { return String.format(Locale.ENGLISH, "Hello %s!", text); }
}
