package com.seatgeek.sixpack;

public class NoExperimentNameException extends RuntimeException {
    public NoExperimentNameException() {
        super("Your Experiment must have a non-null, non-empty name");
    }
}
