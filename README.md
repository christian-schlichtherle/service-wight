# Service Wight

Service Wight composes service factories or containers from factories, containers, modifiers and decorators it locates
on the class path at runtime. 
Think of it as [ServiceLoader](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html) on steroids.

It also generates service declarations in `META-INF/services` with the help of the `@ServiceImplementation` annotation.

Service Wight targets Java SE 8 and is covered by the Apache License, Version 2.

## Usage

### Maven

```xml
<dependency>
    <groupId>global.namespace.service-wight</groupId>
    <artifactId>service-wight</artifactId>
    <version>0.2.0</version>
</dependency>
```

### Imports

For using the service locator and its results:

```java
import global.namespace.service.wight.*;
```

For implementing services:

```java
import global.namespace.service.wight.function.*;
```

For making services locatable:

```java
import global.namespace.service.wight.annotation.*;
```

### Implementing A Locatable Service Container

```java
@ServiceImplementation(value = Provider.class)
public class World implements Provider<String> {

    @Override    
    public String get() { return "world"; }
}
```

### Implementing A Locatable Service Decorator

```java
@ServiceImplementation(value = Decorator.class)
public class Hello implements Mapping<String> {

    @Override
    public String apply(String text) { return String.format(Locale.ENGLISH, "Hello %s!", text); }
}
```

### Locating And Composing Services

```java
Provider<String> provider = new ServiceLocator().provider(String.class, String.class);
System.out.println(provider.get());
```

This should print `Hello world!`.

### A More Realistic Example

The sample above is not ready for production because the interface for the provider and its decorators is just `String`, 
which is too generic. 
In production, you should provide proper service interfaces and locate them instead.
Refactoring the above code could result in the following code.
First the interfaces:

```java
@ServiceInterface
public interface Subject extends Provider<String> { }

...

@ServiceInterface
public interface Salutation extends Mapping<String> { }
```

Next their implementations:    

```java
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
```    

And finally the service location:

```java
Provider<String> provider = new ServiceLocator().provider(Subject.class, Salutation.class);
System.out.println(provider.get());
```
