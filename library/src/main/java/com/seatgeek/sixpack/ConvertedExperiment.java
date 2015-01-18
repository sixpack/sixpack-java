package com.seatgeek.sixpack;

public class ConvertedExperiment {

    private final Sixpack sixpack;
    private final Experiment experiment;

    ConvertedExperiment(Sixpack sixpack, Experiment experiment) {
        this.sixpack = sixpack;
        this.experiment = experiment;
    }

    public Sixpack getSixpack() {
        return sixpack;
    }

    public Experiment getBaseExperiment() {
        return experiment;
    }
}
