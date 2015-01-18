package com.seatgeek.sixpack;

@FunctionalInterface
public interface OnParticipationSuccess {
    public void onParticipation(ParticipatingExperiment experiment, Alternative selectedAlternative);
}
