package me.pesekjak.annostic.example;

import org.junit.jupiter.api.Test;

public class ExampleTest {

    @Test
    public void test() {
        assert new ExampleUsage().test();
    }

    @Test
    public void exception() {
        Exception exception = new RuntimeException();
        assert new ExampleUsage().exception(exception) == exception;
    }

    @Test
    public void generics() {
        assert new ExampleUsage().generics().equals("Hello");
    }

}
