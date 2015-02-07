package com.seatgeek.sixpack;

import java.util.Set;

public class Experiment {
    private final Sixpack sixpack;
    private final String name;
    private final Set<Alternative> alternatives;
    private final Alternative forcedChoice;
    private final Double trafficFraction;

    Experiment(Sixpack sixpack, String name, Set<Alternative> alternatives, Alternative forcedChoice, Double trafficFraction) {
        this.sixpack = sixpack;
        this.name = name;
        this.alternatives = alternatives;
        this.forcedChoice = forcedChoice;
        this.trafficFraction = trafficFraction;
    }

    public Sixpack getSixpack() {
        return sixpack;
    }

    public String getName() {
        return name;
    }

    public Set<Alternative> getAlternatives() {
        return alternatives;
    }

    public boolean hasForcedChoice() {
        return forcedChoice != null;
    }

    public Alternative getForcedChoice() {
        return forcedChoice;
    }

    public Double getTrafficFraction() {
        return trafficFraction;
    }

    public void participate(OnParticipationSuccess callback, OnParticipationFailure failureCallback) {
        sixpack.participateIn(this, callback, failureCallback);
    }

    @Override
    public String toString() {
        return name;
    }
}
