package com.rm.darya.model;

public class Pair<F, S> {
    private final F first;
    private S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> Pair<A, B> create(A a, B b) {
        return new Pair<>(a, b);
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public void setSecond(S second) {
        this.second = second;
    }
}