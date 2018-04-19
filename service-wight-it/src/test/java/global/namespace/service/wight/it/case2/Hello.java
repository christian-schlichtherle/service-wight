package global.namespace.service.wight.it.case2;

import global.namespace.service.wight.annotation.ServiceImplementation;

import java.util.Locale;

@ServiceImplementation
public class Hello implements Salutation {

    @Override
    public String apply(String subject) { return String.format(Locale.ENGLISH, "Hello %s!", subject); }
}
