# Service Wight

Service Wight composes service factories or containers from factories, containers, modifiers and decorators it locates
on the class path at runtime. 
Think of it as [ServiceLoader] on steroids.

It also generates service declarations in `META-INF/services` with the help of the `@ServiceImplementation` annotation.

Service Wight targets Java SE 8 and is covered by the Apache License, Version 2.

## Usage

### Dependencies

In Maven:

```xml
<dependency>
    <groupId>global.namespace.service-wight</groupId>
    <artifactId>service-wight</artifactId>
    <version>0.3.0</version>
</dependency>
```

### Imports

For using the service locator and its results:

```java
import global.namespace.service.wight.*;
```

For writing service interfaces and implementing them:

```java
import global.namespace.service.wight.function.*;
```

For making services locatable:

```java
import global.namespace.service.wight.annotation.*;
```

### Defining And Implementing A Locatable Service Provider

First, the service interface:

```java
@ServiceInterface
public interface Subject extends Provider<String> { }
```

Next, a service implementation:

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
2. The `World` class needs to implement the `Provider` interface (which extends `java.util.function.Supplier`).

The second point may look like a constraint, but it's not:
To the contrary, this design adds a level of indirection which allows you to locate products which `ServiceLoader` could
not locate otherwise - `String` in this case.  

### Defining And Implementing A Locatable Service Mapping

Let's add a salutation for the located subject.
First, the service interface:

```java
@ServiceInterface
public interface Salutation extends Mapping<String> { }
```

Next, a service implementation:

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
In this case, first it locates all implementations of the `Subject` interface (there is only one for now) and selects 
the one with the highest priority as you will see next.
Second, it locates all implementations of the `Salutation` interface and orders them by ascending priority.
Third, it composes a provider which applies the selected `Subject` and applies the sorted `Salutation`s in order.  

### Overriding A Locatable Service Provider

For overriding the selection of the locatable service provider, you simply need to implement another locatable service 
provider with a higher priority - the default priority is `0`:

```java
@ServiceImplementation(priority = 10)
public class Christian implements Subject {

    @Override
    public String get() { return "Christian"; }
}
```

Now you can run the service location code again and it will print `Hello Christian!` without any changes. 

### Adding Another Locatable Service Mapping

Similar to a locatable service provider, you can add another locatable service mapping.
However, all located service mappings are applied to the selected located service provider in ascending order.
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
`Provider`:

```java
CompositeProvider<String, Subject, Salutation> provider = new ServiceLocator().provider(Subject.class, Salutation.class);
System.out.println(provider.get());
```

The `CompositeProvider` class returns the list of located service providers and mappings by its `providers` and 
`mappings` methods.
You can use these to inspect the findings of the service location.
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

Adding a level of indirection for the service lookup and partitioning locatable services into providers and mappings
with priority based selection and sorting results in a fairly flexible, yet simple mechanism for locating services on
the class path.   

[ServiceLoader]: https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html
