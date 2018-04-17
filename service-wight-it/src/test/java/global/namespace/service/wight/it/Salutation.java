package global.namespace.service.wight.it;

import global.namespace.service.wight.LocatableDecorator;
import global.namespace.service.wight.annotation.ServiceImplementation;

import java.util.Locale;

@ServiceImplementation(LocatableDecorator.class)
public final class Salutation extends LocatableDecorator<String> {

    @Override
    public String apply(String text) { return String.format(Locale.ENGLISH, "Hello %s!", text); }

    @Override
    public int getPriority() { return -1; }
}
