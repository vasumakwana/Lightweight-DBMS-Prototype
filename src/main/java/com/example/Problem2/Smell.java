package com.example.Problem2;

public class Smell {
    private ClassB b;

    public void doSomething() {
        b.doSomethingElse();
    }
}

class ClassB {
    private ClassC c;

    public void doSomethingElse() {
        c.doAnotherThing();
    }
}

class ClassC {
    private Smell a;

    public void doAnotherThing() {
        a.doSomething();
    }
}
