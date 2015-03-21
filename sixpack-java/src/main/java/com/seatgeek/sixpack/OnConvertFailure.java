package com.seatgeek.sixpack;

@FunctionalInterface
public interface OnConvertFailure {
    public void onConvertFailure(ParticipatingExperiment experiment, Throwable error);
}
