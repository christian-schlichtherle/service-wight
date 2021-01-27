package global.namespace.service.wight.it.case2;

import global.namespace.service.wight.core.annotation.ServiceInterface;

import java.util.function.Supplier;

@ServiceInterface
public interface Subject extends Supplier<String> { }
