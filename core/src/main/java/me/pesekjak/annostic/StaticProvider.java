package me.pesekjak.annostic;

import java.lang.annotation.*;

/**
 * The {@code @StaticProvider} annotation establishes a connection between annotation and interface,
 * allowing static methods defined within the interface to be inherited by the annotated annotation.
 * These static methods can then be accessed through the annotation as if they were part of it.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface StaticProvider {

    /**
     * Specifies the target annotation class with which the annotated interface's static methods will be associated.
     * The static methods of the interface will be inherited by the target annotation.
     *
     * @return The class of the target annotation.
     */
    Class<? extends Annotation> target();

}
