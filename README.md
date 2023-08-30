# Annostic
**Annotations with static methods.**

[![license](https://img.shields.io/github/license/pesekjak/annostic?style=for-the-badge&color=657185)](LICENSE)

Annostic project introduces enhancement to the Java programming language's annotation
capabilities by introducing static methods within annotations.

---

## Usage

Annostic offers single annotation [@StaticProvider](core/src/main/java/me/pesekjak/annostic/StaticProvider.java) that allows other annotations to 
inherit static methods from annotated interface.

```java
public @interface Example {
    @StaticProvider(target = Example.class)
    interface Static {

        static int one() {
            return 1;
        }

    }
}
```

In this example, the `@Example` annotation is defined,
and it includes an inner Static interface.
The Static interface contains a static method called `one()` which returns the value 1.

The `@StaticProvider` annotation acts as a bridge between the annotation specified
with the `target` parameter and the interface's static methods,
making these methods accessible as if they were part of the annotation itself.

> **_NOTE:_** To use the static methods within the project, it is required to access them through
the annotated interface (static provider).

During compilation, the interfaces annotated with static providers are hidden as
synthetic classes and their targets inherit all their static methods as shown in the
example below.

```java
public class ExampleUsage {

    public boolean test() {
        return Example.one() == 1;
    }

}
```