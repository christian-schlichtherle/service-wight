# Service Wight

Service Wight composes service factories or containers from factories, containers, modifiers and decorators it locates
on the class path at runtime. 
Think of it as [ServiceLoader](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html) on steroids.

It also generates service declarations in `META-INF/services` with the help of the `@ServiceImplementation` annotation.

Service Wight targets Java SE 8 and is covered by the Apache License, Version 2.

## Usage

### Maven

    <dependency>
        <groupId>global.namespace.service-wight</groupId>
        <artifactId>service-wight</artifactId>
        <version>0.2.0</version>
    </dependency>

### Imports

For using the service locator and its results:

    import global.namespace.service.wight.*;

For implementing services:

    import global.namespace.service.wight.function.*;

For making services locatable:

    import global.namespace.service.wight.annotation.*;

### Implementing A Locatable Service Container

    @ServiceImplementation(value = Provider.class)
    public class World implements Provider<String> {

        @Override    
        public String get() { return "world"; }
    }

### Implementing A Locatable Service Decorator

    @ServiceImplementation(value = Decorator.class)
    public class Hello implements Decorator<String> {
    
        @Override
        public String apply(String text) { return String.format(Locale.ENGLISH, "Hello %s!", text); }
    }

### Locating And Composing Services

    final Container<String> container = new ServiceLocator().container(String.class, String.class);
    System.out.println(container.get());

This should print `Hello world!`.

### A More Realistic Example

The sample above is not ready for production because the interface for the provider and its decorators is just `String`, 
which is too generic. 
In production, you should provide proper service interfaces and locate them instead.
Refactoring the above code could result in the following code.
First the interfaces:

    @ServiceInterface
    public interface Subject extends Provider<String> { }

    ...
    
    @ServiceInterface
    public interface Salutation extends Decorator<String> { }

Next their implementations:    
    
    @ServiceImplementation
    public class World implements Subject {

        @Override    
        public String get() { return "world"; }
    }

    ...
    
    @ServiceImplementation
    public class Hello implements Salutation {
    
        @Override
        public String apply(String text) { return String.format(Locale.ENGLISH, "Hello %s!", text); }
    }

And finally the service location:

    final Container<String> container = new ServiceLocator().container(Subject.class, Salutation.class);
    System.out.println(container.get());
