package com.seatgeek.sixpack;

public class ConversionError extends RuntimeException {

    public ConversionError(final Throwable e, Experiment experiment) {
        super("Error converting experiment " + experiment, e);
    }
}
