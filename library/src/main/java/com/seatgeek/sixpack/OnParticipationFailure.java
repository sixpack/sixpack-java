package com.seatgeek.sixpack;

@FunctionalInterface
public interface OnParticipationFailure {
    public void onParticipationFailed(Experiment experiment, Throwable error);
}
