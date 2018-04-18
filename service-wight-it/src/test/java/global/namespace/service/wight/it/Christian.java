package global.namespace.service.wight.it;

import global.namespace.service.wight.annotation.ServiceImplementation;
import global.namespace.service.wight.function.Factory;

@ServiceImplementation(Factory.class)
public final class Christian implements Factory<String> {

    @SuppressWarnings("RedundantStringConstructorCall")
    public String get() { return new String("Christian"); }
}
