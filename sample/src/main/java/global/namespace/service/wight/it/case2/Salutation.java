package global.namespace.service.wight.it.case2;

import global.namespace.service.wight.annotation.ServiceInterface;

import java.util.function.UnaryOperator;

@ServiceInterface
public interface Salutation extends UnaryOperator<String> { }
