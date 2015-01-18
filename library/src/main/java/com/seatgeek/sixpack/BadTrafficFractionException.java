package com.seatgeek.sixpack;

public class BadTrafficFractionException extends RuntimeException {

    public BadTrafficFractionException() {
        super("Traffic Fraction must be >= 0 and <= 1");
    }
}
