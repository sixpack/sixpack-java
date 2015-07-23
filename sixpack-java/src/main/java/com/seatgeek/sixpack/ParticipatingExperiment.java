package com.seatgeek.sixpack;

public class ParticipatingExperiment {

    public final Sixpack sixpack;

    public final Experiment baseExperiment;

    public final Alternative selectedAlternative;

    ParticipatingExperiment(Sixpack sixpack, Experiment baseExperiment, Alternative selectedAlternative) {
        this.sixpack = sixpack;
        this.baseExperiment = baseExperiment;
        this.selectedAlternative = selectedAlternative;
    }

    public void convert(OnConvertSuccess success, OnConvertFailure failure) {
        sixpack.convert(this, success, failure);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticipatingExperiment)) return false;

        ParticipatingExperiment that = (ParticipatingExperiment) o;

        if (!baseExperiment.equals(that.baseExperiment)) return false;
        if (!selectedAlternative.equals(that.selectedAlternative)) return false;
        if (!sixpack.equals(that.sixpack)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sixpack.hashCode();
        result = 31 * result + baseExperiment.hashCode();
        result = 31 * result + selectedAlternative.hashCode();
        return result;
    }
}
