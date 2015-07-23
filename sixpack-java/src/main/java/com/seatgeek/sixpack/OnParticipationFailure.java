package com.seatgeek.sixpack;

@FunctionalInterface
public interface OnParticipationFailure {

    void onParticipationFailed(Experiment experiment, Throwable error);
}
