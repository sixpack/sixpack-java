package com.seatgeek.sixpack;

public class NoAlternativesException extends RuntimeException {

    public NoAlternativesException() {
        super("You must specify at least one Alternative for an Experiment");
    }
}
