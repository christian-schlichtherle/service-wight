package global.namespace.service.wight.it;

import global.namespace.service.wight.annotation.ServiceImplementation;
import global.namespace.service.wight.function.Factory;

@ServiceImplementation(value = Factory.class, priority = -1)
public final class World implements Factory<String> {

    @SuppressWarnings("RedundantStringConstructorCall")
    public String get() { return new String("World"); }
}
