package global.namespace.service.wight.it;

import global.namespace.service.wight.annotation.ServiceImplementation;
import global.namespace.service.wight.function.Provider;

@ServiceImplementation(Provider.class)
public final class Christian implements Provider<String> {

    public String get() { return "Christian"; }
}
