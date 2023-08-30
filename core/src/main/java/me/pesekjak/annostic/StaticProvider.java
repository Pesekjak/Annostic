package me.pesekjak.annostic;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface StaticProvider {

    Class<? extends Annotation> target();

}
