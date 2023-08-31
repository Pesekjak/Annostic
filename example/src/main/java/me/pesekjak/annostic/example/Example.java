package me.pesekjak.annostic.example;

import me.pesekjak.annostic.StaticProvider;

public @interface Example {

    @StaticProvider(target = Example.class)
    interface Static {

        static int one() {
            return 1;
        }

        static void throwAnException(Exception exception) throws Exception {
            throw exception;
        }

        static <T> T generics(T value) {
            return value;
        }

    }

}
