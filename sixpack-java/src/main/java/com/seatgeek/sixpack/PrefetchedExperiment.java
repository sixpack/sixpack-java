package com.seatgeek.sixpack;

public class PrefetchedExperiment {

    private final Sixpack sixpack;

    public final Experiment baseExperiment;

    public final Alternative selectedAlternative;

    public PrefetchedExperiment(final Sixpack sixpack, final Experiment experiment, final Alternative alternative) {
        this.sixpack = sixpack;
        baseExperiment = experiment;
        selectedAlternative = alternative;
    }

    public ParticipatingExperiment participate() {
        return sixpack.participate(baseExperiment);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof PrefetchedExperiment)) return false;

        final PrefetchedExperiment that = (PrefetchedExperiment) o;

        if (baseExperiment != null ? !baseExperiment.equals(that.baseExperiment) : that.baseExperiment != null)
            return false;
        return !(selectedAlternative != null ? !selectedAlternative.equals(that.selectedAlternative) : that.selectedAlternative != null);
    }

    @Override
    public int hashCode() {
        int result = baseExperiment != null ? baseExperiment.hashCode() : 0;
        result = 31 * result + (selectedAlternative != null ? selectedAlternative.hashCode() : 0);
        return result;
    }
}
