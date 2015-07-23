package com.seatgeek.sixpack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ExperimentBuilder {
    private Sixpack sixpack;
    private String name;
    private Set<Alternative> alternatives;
    private Alternative forcedChoice;
    private Double trafficFraction;

    public ExperimentBuilder(Sixpack sixpack) {
        this.sixpack = sixpack;
    }

    public ExperimentBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ExperimentBuilder withAlternatives(Set<Alternative> alternatives) {
        this.alternatives = alternatives;
        return this;
    }

    public ExperimentBuilder withAlternatives(Alternative... alternatives) {
        this.alternatives = new HashSet<>(alternatives.length);
        Collections.addAll(this.alternatives, alternatives);
        return this;
    }

    public ExperimentBuilder withAlternative(Alternative alternative) {
        if (this.alternatives == null) {
            this.alternatives = new HashSet<>();
        }
        this.alternatives.add(alternative);
        return this;
    }

    public ExperimentBuilder withForcedChoice(Alternative forcedChoice) {
        this.forcedChoice = forcedChoice;
        return this;
    }

    public ExperimentBuilder withTrafficFraction(Double fraction) {
        if (fraction < 0 || fraction > 1) {
            throw new BadTrafficFractionException();
        }

        this.trafficFraction = fraction;
        return this;
    }

    public Experiment build() {
        if (name == null || name.length() == 0) {
            throw new NoExperimentNameException();
        }

        if (alternatives == null || alternatives.isEmpty()) {
            throw new NoAlternativesException();
        }

        sixpack.logNewExperiment(name, alternatives, forcedChoice, trafficFraction);

        return new Experiment(sixpack, name, alternatives, forcedChoice, trafficFraction);
    }
}
