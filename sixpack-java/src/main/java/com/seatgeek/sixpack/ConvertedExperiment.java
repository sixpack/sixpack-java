package com.seatgeek.sixpack;

public class ConvertedExperiment {

    public final Sixpack sixpack;

    public final Experiment baseExperiment;

    ConvertedExperiment(Sixpack sixpack, Experiment baseExperiment) {
        this.sixpack = sixpack;
        this.baseExperiment = baseExperiment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConvertedExperiment)) return false;

        ConvertedExperiment that = (ConvertedExperiment) o;

        if (!baseExperiment.equals(that.baseExperiment)) return false;
        if (!sixpack.equals(that.sixpack)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sixpack.hashCode();
        result = 31 * result + baseExperiment.hashCode();
        return result;
    }
}
