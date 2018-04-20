# Service Wight

Service Wight composes service factories or containers from factories, containers, modifiers and decorators it locates
on the class path at runtime. 
Think of it as [`ServiceLoader`] on steroids.

It also generates service declarations in `META-INF/services` with the help of the `@ServiceImplementation` annotation.

Service Wight targets Java SE 8 and is covered by the Apache License, Version 2.

## Basic Usage

### Dependencies

In Maven:

```xml
<dependency>
    <groupId>global.namespace.service-wight</groupId>
    <artifactId>service-wight-core</artifactId>
    <version>0.4.0</version>
</dependency>
```

### Imports

For writing service interfaces:

```java
import java.util.function.*;
```

For making services locatable:

```java
import global.namespace.service.wight.core.annotation.*;
```

For using the service locator and its findings:

```java
import global.namespace.service.wight.core.*;
```

### Designing A Locatable Service Provider

A _locatable service provider_ is simply a locatable service which supplies some product.
First, the service interface:

```java
@ServiceInterface
public interface Subject extends Supplier<String> { }
```

Next, the service implementation:

```java
@ServiceImplementation
public class World implements Subject {

    @Override    
    public String get() { return "World"; }
}
```

Finally, the service location:

```java
Supplier<String> supplier = new ServiceLocator().provider(Subject.class);
System.out.println(provider.get());
```

Not surprisingly, this prints `World`.

In this example, `ServiceLocator` works pretty much like `ServiceLoader`, except for two things:

1. You don't have to write an entry in `META-INF/services/...Subject` which references the `World` class.
   The processor for the `@ServiceImplementation` annotation does that for you.
2. The `Subject` interface needs to extend the [`Supplier`] interface.

The second point may look like a constraint, but it's not:
In fact, this design adds a level of indirection which allows you to supply products which `ServiceLoader` could not 
locate directly on the classpath - like `String` in this case.

### Adding A Locatable Service Transformation

Let's add a salutation for the supplied subject.
For this we need a _locatable service transformation_, which is simply a unary operator on some product.
A unary operator is simply a function where the input and output parameters have the same type.
First, the service interface:

```java
@ServiceInterface
public interface Salutation extends UnaryOperator<String> { }
```

Note that the base interface is [`UnaryOperator`] this time - not [`Supplier`].

Next, the service implementation:

```java
@ServiceImplementation
public class Hello implements Salutation {

    @Override
    public String apply(String subject) { return String.format(Locale.ENGLISH, "Hello %s!", subject); }
}
```

Finally, the service location:

```java
Supplier<String> supplier = new ServiceLocator().provider(Subject.class, Salutation.class);
System.out.println(provider.get());
```

Note that the `provider` method now takes two parametes, the first is the service interface for the locatable service 
provider and the second is the service interface for the locatable service transformation.

The preceding code prints `Hello World!`, but why?
Service Wight composes all service providers and transformations it locates on the classpath into a custom provider.
In this case, first it locates all `Subject` implementations (there is only one for now) and sorts them by descending 
priority.
Second, it locates all `Salutation` implementations and sorts them by ascending priority.
Third, it creates a composite provider which selects the first `Subject` and applies all `Salutation`s in order.  

### Overriding The Locatable Service Provider

For overriding the selection of the locatable service provider, you simply need to implement another locatable service 
provider with a higher priority (the default priority is `0`):

```java
@ServiceImplementation(priority = 10)
public class Christian implements Subject {

    @Override
    public String get() { return "Christian"; }
}
```

Now you can run the service location code again and it will print `Hello Christian!`, without any changes. 

### Adding Another Locatable Service Transformation

Similar to a locatable service provider, you can add another locatable service transformation.
Again, the default priority is `0`:

```java
@ServiceImplementation(priority = 10)
public final class Smalltalk implements Salutation {

    @Override
    public String apply(String text) { return text + " How do you do?"; }
}
```

Now you can run the service location code again and it prints `Hello Christian! How do you do?`.

### Conclusion

Service Wight adds a level of indirection to locatable services and partitions them into service providers and 
service transformations at design time.
Based on their priority, providers and transformations are selected and sorted for composition into custom providers at 
runtime. 
This simple design results in a fairly flexible schema for locating services on the class path.
Leveraging this schema, you can easily design complex plugin architectures where features are encapsulated in plugins 
which users can compose into solutions simply by adding them to the runtime classpath of their application. 

## Advanced Usage

### Introspecting The Findings Of The Service Locator

The `provider` method of the `ServiceLocator` class actually returns a `CompositeProvider`, not just a [`Supplier`], so 
you can write this:

```java
CompositeProvider<String, Subject, Salutation> provider = new ServiceLocator().provider(Subject.class, Salutation.class);
System.out.println(provider.get());
```

The `CompositeProvider` class provides access to the list of located service providers and transformations by its 
`providers()` and `transformations()` properties.
You can use these properties to inspect the findings of the service locator.
For example, you may want to log the classes and the priorities of the located service providers and transformations for 
post mortem analysis.

You can also create your own `CompositeProvider`.
For example, you may want to override the priority based selection and sorting of service providers and transformations.
You can do so by calling the `providers()` and `transformations()` properties, modifying the returned lists and creating 
a new `CompositeProvider` from them like this:

```java
List<Subject> subjects = provider.providers();
Collections.reverse(subjects);
List<Salutation> salutations = provider.transformations();
Collections.reverse(salutations);
CompositeProvider<String, Subject, Salutation> update = new CompositeProvider<>(subjects, salutations);
System.out.println(update.get());
```

This prints `Hello World How do yo do?!`.

### Avoiding Dependencies

Maybe you want to avoid a dependency on `service-wight-core` in your service interfaces?
No problem!
You can remove the `@ServiceInterface` annotation at the expense of declaring the service interface in the 
`@ServiceImplementation` annotation.
So the service interface now looks like this:

```java
public interface Subject extends Supplier<String> { }
```

Note that there is no more dependency on `service-wight-core`.
The service implementation now looks like this:

```java
@ServiceImplementation(Subject.class)
public class World implements Subject {

    @Override    
    public String get() { return "World"; }
}
```

The code for the service location remains unchanged:

```java
Supplier<String> supplier = new ServiceLocator().provider(Subject.class);
System.out.println(provider.get());
```

### Using The Annotations Standalone

The `@ServiceInterface` and `@ServiceImplementation` annotations can be used standalone, i.e. without using the 
`ServiceLocator`.
This is useful when you don't want your service interfaces to extend `Supplier` or `UnaryOperator` for some reason, but
you still want some entries in `META-INF/services/` to be generated. 

[`ServiceLoader`]: https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html
[`Supplier`]: https://docs.oracle.com/javase/8/docs/api/java/util/function/Supplier.html
[`UnaryOperator`]: https://docs.oracle.com/javase/8/docs/api/java/util/function/UnaryOperator.html
