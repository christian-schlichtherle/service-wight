package global.namespace.service.wight.it;

import global.namespace.service.wight.LocatableDecorator;
import global.namespace.service.wight.annotation.ServiceImplementation;

import java.util.Locale;

@ServiceImplementation(LocatableDecorator.class)
public final class Smalltalk extends LocatableDecorator<String> {

    @Override
    public String apply(String text) { return text + " How do you do?"; }
}
