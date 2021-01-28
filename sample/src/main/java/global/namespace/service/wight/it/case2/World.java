package global.namespace.service.wight.it.case2;

import global.namespace.service.wight.annotation.ServiceImplementation;

@ServiceImplementation
public class World implements Subject {

    @Override
    public String get() { return "World"; }
}
