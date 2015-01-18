package com.seatgeek.sixpack;

public class ParticipatingExperiment {

    private final Sixpack sixpack;
    private final Experiment experiment;

    ParticipatingExperiment(Sixpack sixpack, Experiment experiment) {
        this.sixpack = sixpack;
        this.experiment = experiment;
    }

    public Sixpack getSixpack() {
        return sixpack;
    }

    public Experiment getBaseExperiment() {
        return experiment;
    }

    public void convert(OnConvertSuccess success, OnConvertFailure failure) {
        sixpack.convert(this, success, failure);
    }
}
