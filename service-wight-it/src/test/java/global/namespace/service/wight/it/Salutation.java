package global.namespace.service.wight.it;

import global.namespace.service.wight.annotation.ServiceImplementation;
import global.namespace.service.wight.function.Mapping;

import java.util.Locale;

@ServiceImplementation(value = Mapping.class, priority = -1)
public final class Salutation implements Mapping<String> {

    @Override
    public String apply(String text) { return String.format(Locale.ENGLISH, "Hello %s!", text); }
}
