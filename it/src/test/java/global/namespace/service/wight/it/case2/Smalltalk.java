package global.namespace.service.wight.it.case2;

import global.namespace.service.wight.core.annotation.ServiceImplementation;

@ServiceImplementation(priority = 10)
public final class Smalltalk implements Salutation {

    @Override
    public String apply(String text) { return text + " How do you do?"; }
}
