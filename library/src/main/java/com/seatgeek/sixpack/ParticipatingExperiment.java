package com.seatgeek.sixpack;

public class ParticipatingExperiment {

    private final Sixpack sixpack;
    private final Experiment experiment;
    private final Alternative selectedAlternative;

    ParticipatingExperiment(Sixpack sixpack, Experiment experiment, Alternative selectedAlternative) {
        this.sixpack = sixpack;
        this.experiment = experiment;
        this.selectedAlternative = selectedAlternative;
    }

    public Sixpack getSixpack() {
        return sixpack;
    }

    public Experiment getBaseExperiment() {
        return experiment;
    }

    public Alternative getSelectedAlternative() {
        return selectedAlternative;
    }

    public void convert(OnConvertSuccess success, OnConvertFailure failure) {
        sixpack.convert(this, success, failure);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticipatingExperiment)) return false;

        ParticipatingExperiment that = (ParticipatingExperiment) o;

        if (!experiment.equals(that.experiment)) return false;
        if (!selectedAlternative.equals(that.selectedAlternative)) return false;
        if (!sixpack.equals(that.sixpack)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sixpack.hashCode();
        result = 31 * result + experiment.hashCode();
        result = 31 * result + selectedAlternative.hashCode();
        return result;
    }
}
