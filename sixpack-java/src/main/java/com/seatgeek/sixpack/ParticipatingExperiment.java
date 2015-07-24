package com.seatgeek.sixpack;

/**
 * An {@link Experiment} that has been {@link Experiment#participate(OnParticipationSuccess, OnParticipationFailure)}'ed
 * in. Waiting to be {@link #convert(OnConvertSuccess, OnConvertFailure)}'ed.
 */
public class ParticipatingExperiment {

    /**
     * The {@link Sixpack} instance that this {@link ParticipatingExperiment is associated with}
     */
    public final Sixpack sixpack;

    /**
     * The {@link Experiment} that this {@link ParticipatingExperiment} was created from
     */
    public final Experiment baseExperiment;

    /**
     * The {@link Alternative} that the Sixpack server has selected for this client in this experiment
     */
    public final Alternative selectedAlternative;

    ParticipatingExperiment(Sixpack sixpack, Experiment baseExperiment, Alternative selectedAlternative) {
        this.sixpack = sixpack;
        this.baseExperiment = baseExperiment;
        this.selectedAlternative = selectedAlternative;
    }

    /**
     * Converts this experiment by notifying the Sixpack server that the user has completed the
     * expected action
     *
     * @param success success callback for when the server has acknowledged that the user converted
     * @param failure failure callback for when there was an issue reporting the user's conversion
     *                to the Sixpack server
     */
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
