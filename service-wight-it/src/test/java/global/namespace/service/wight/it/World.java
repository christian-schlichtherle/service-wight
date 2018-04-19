package global.namespace.service.wight.it;

import global.namespace.service.wight.annotation.ServiceImplementation;
import global.namespace.service.wight.function.Provider;

@ServiceImplementation(value = Provider.class, priority = -1)
public final class World implements Provider<String> {

    public String get() { return "World"; }
}
