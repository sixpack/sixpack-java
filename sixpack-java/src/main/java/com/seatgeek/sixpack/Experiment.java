package com.seatgeek.sixpack;

import java.util.Set;

public class Experiment {

    public final Sixpack sixpack;

    public final String name;

    public final Set<Alternative> alternatives;

    public final Alternative forcedChoice;

    public final Double trafficFraction;

    Experiment(Sixpack sixpack, String name, Set<Alternative> alternatives, Alternative forcedChoice, Double trafficFraction) {
        this.sixpack = sixpack;
        this.name = name;
        this.alternatives = alternatives;
        this.forcedChoice = forcedChoice;
        this.trafficFraction = trafficFraction;
    }

    public void participate(OnParticipationSuccess callback, OnParticipationFailure failureCallback) {
        sixpack.participateIn(this, callback, failureCallback);
    }

    public boolean hasForcedChoice() {
        return forcedChoice != null;
    }

    @Override
    public String toString() {
        return name;
    }
}
