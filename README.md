# Service Wight

Service Wight composes service factories or containers from factories, containers, modifiers and decorators it locates
on the class path at runtime. 
Think of it as [`ServiceLoader`] on steroids.

It also generates service declarations in `META-INF/services` with the help of the `@ServiceImplementation` annotation.

Service Wight targets Java SE 8 and is covered by the Apache License, Version 2.

## Usage

### Dependencies

In Maven:

```xml
<dependency>
    <groupId>global.namespace.service-wight</groupId>
    <artifactId>service-wight-core</artifactId>
    <version>0.3.1</version>
</dependency>
```

### Imports

For writing service interfaces:

```java
import global.namespace.service.wight.core.function.*;
```

For making service implementations locatable:

```java
import global.namespace.service.wight.core.annotation.*;
```

For using the service locator and its results:

```java
import global.namespace.service.wight.core.*;
```

### Designing A Locatable Service Provider

First, the service interface:

```java
@ServiceInterface
public interface Subject extends Provider<String> { }
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
Provider<String> provider = new ServiceLocator().provider(Subject.class);
System.out.println(provider.get());
```

Not surprisingly, this prints `World`.

In this example, `ServiceLocator` works pretty much like `ServiceLoader`, except for two things:

1. You don't have to write an entry in `META-INF/services/...Subject` referencing the `World` class.
   The processor for the `@ServiceImplementation` annotation does that for you.
2. The `Subject` interface needs to implement the `Provider` interface, which extends [`Supplier`].

The second point may look like a constraint, but it's not:
In fact, this design adds a level of indirection which allows you to locate products which `ServiceLoader` could not 
locate directly - like `String` in this case.  

### Adding A Locatable Service Mapping

Let's add a salutation for the located subject.
First, the service interface:

```java
@ServiceInterface
public interface Salutation extends Mapping<String> { }
```

Note that the base interface is `Mapping` this time - not `Provider`.
A `Mapping` is simply a function where the input and output parameters have the same type.
In fact, it extends [`UnaryOperator`].

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
Provider<String> provider = new ServiceLocator().provider(Subject.class, Salutation.class);
System.out.println(provider.get());
```

This prints `Hello World!`, but why?

Service Wight composes all providers and mappings it finds on the class path into a new provider.
In this case, first it locates all implementations of the `Subject` interface (there is only one for now) and sorts them 
by descending priority.
Second, it locates all implementations of the `Salutation` interface and sorts them by ascending priority.
Third, it creates a composite provider which selects the first `Subject` and applies all `Salutation`s in order.  

### Overriding The Locatable Service Provider

For overriding the selection of the locatable service provider, you simply need to implement another locatable service 
provider with a higher priority - the default priority is `0`:

```java
@ServiceImplementation(priority = 10)
public class Christian implements Subject {

    @Override
    public String get() { return "Christian"; }
}
```

Now you can run the service location code again and it will print `Hello Christian!`, without any changes. 

### Adding Another Locatable Service Mapping

Similar to a locatable service provider, you can add another locatable service mapping.
Again, the default priority is `0`:

```java
@ServiceImplementation(priority = 10)
public final class Smalltalk implements Salutation {

    @Override
    public String apply(String text) { return text + " How do you do?"; }
}
```

Now you can run the service location code again and it prints `Hello Christian! How do you do?`.

### The Composite Provider

Note that the `provider` method of the `ServiceLocator` class actually returns a `CompositeProvider`, not just a 
`Provider`, so you can write this:

```java
CompositeProvider<String, Subject, Salutation> provider = new ServiceLocator().provider(Subject.class, Salutation.class);
System.out.println(provider.get());
```

The `CompositeProvider` class provides access to the list of located service providers and mappings by its `providers()` 
and `mappings()` methods.
You can use these properties to inspect the findings of the service location.
For example, you may want to log the classes and the priorities of the located service providers and mappings for post
mortem analysis.

You can also create your own `CompositeProvider`.
For example, you may want to override the priority based selection and sorting of providers and mappings
You can do so by calling the `providers` and `mappings` functions, modifying the returned lists and creating a new 
`CompositeProvider` from them:

```java
List<Subject> subjects = provider.providers();
Collections.reverse(subjects);
List<Salutation> salutations = provider.mappings();
Collections.reverse(salutations);
CompositeProvider<String, Subject, Salutation> update = new CompositeProvider<>(subjects, salutations);
System.out.println(update.get());
```

This prints `Hello World How do yo do?!`.
 
## Conclusion

Service Wight adds a level of indirection to locatable services and partitions them into providers and mappings at 
design time.
Based on their priority then, providers and mappings are selected and sorted for composition into custom providers at 
runtime. 
This simple design results in a fairly flexible schema for locating services on the class path.
Leveraging this schema, you can easily design complex plugin architectures where features are encapsulated in plugins 
which users can compose into solutions simply by adding them to the runtime classpath of their application. 

[`ServiceLoader`]: https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html
[`Supplier`]: https://docs.oracle.com/javase/8/docs/api/java/util/function/Supplier.html
[`UnaryOperator`]: https://docs.oracle.com/javase/8/docs/api/java/util/function/UnaryOperator.html
