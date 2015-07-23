package com.seatgeek.sixpack;

@FunctionalInterface
public interface OnParticipationSuccess {

    void onParticipation(ParticipatingExperiment experiment);
}
