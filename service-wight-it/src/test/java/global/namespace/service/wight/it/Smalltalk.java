package global.namespace.service.wight.it;

import global.namespace.service.wight.annotation.ServiceImplementation;
import global.namespace.service.wight.function.Mapping;

@ServiceImplementation(Mapping.class)
public final class Smalltalk implements Mapping<String> {

    @Override
    public String apply(String text) { return text + " How do you do?"; }
}
