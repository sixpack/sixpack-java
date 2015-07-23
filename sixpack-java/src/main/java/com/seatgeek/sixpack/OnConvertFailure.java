package com.seatgeek.sixpack;

@FunctionalInterface
public interface OnConvertFailure {

    void onConvertFailure(ParticipatingExperiment experiment, Throwable error);
}
