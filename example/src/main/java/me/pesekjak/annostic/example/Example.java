package me.pesekjak.annostic.example;

import me.pesekjak.annostic.StaticProvider;

public @interface Example {

    @StaticProvider(target = Example.class)
    interface Static {

        static int one() {
            return 1;
        }

    }

}
