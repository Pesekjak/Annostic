package me.pesekjak.annostic.example;

public class ExampleUsage {

    public boolean test() {
        return Example.one() == 1;
    }

    public Exception exception(Exception exception) {
        try {
            Example.throwAnException(exception);
            return null;
        } catch (Exception e) {
            return e;
        }
    }

    public String generics() {
        return Example.generics("Hello");
    }

}
