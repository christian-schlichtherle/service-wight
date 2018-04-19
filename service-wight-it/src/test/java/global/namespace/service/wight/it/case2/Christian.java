package global.namespace.service.wight.it.case2;

import global.namespace.service.wight.annotation.ServiceImplementation;

@ServiceImplementation(priority = 10)
public class Christian implements Subject {

    @Override
    public String get() { return "Christian"; }
}
