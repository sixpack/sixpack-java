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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConvertedExperiment)) return false;

        ConvertedExperiment that = (ConvertedExperiment) o;

        if (!experiment.equals(that.experiment)) return false;
        if (!sixpack.equals(that.sixpack)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sixpack.hashCode();
        result = 31 * result + experiment.hashCode();
        return result;
    }
}
