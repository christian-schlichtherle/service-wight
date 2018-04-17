package global.namespace.service.wight.it;

import global.namespace.service.wight.LocatableFactory;
import global.namespace.service.wight.annotation.ServiceImplementation;

@ServiceImplementation(LocatableFactory.class)
public final class World extends LocatableFactory<String> {

    @SuppressWarnings("RedundantStringConstructorCall")
    public String get() { return new String("World"); }

    @Override
    public int getPriority() { return -1; }
}
