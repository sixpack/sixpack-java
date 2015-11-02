package com.seatgeek.sixpack;

/**
 * An {@link Experiment} that has been {@link Experiment#participate()}'ed
 * in. Waiting to be {@link #convert()}'ed.
 */
public class ParticipatingExperiment {

    /**
     * The {@link Sixpack} instance that this {@link ParticipatingExperiment is associated with}
     */
    private final Sixpack sixpack;

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
     * This call makes blocking network requests.
     *
     * @throws ConversionError if the client fails to convert
     */
    public ConvertedExperiment convert() {
        return sixpack.convert(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticipatingExperiment)) return false;

        ParticipatingExperiment that = (ParticipatingExperiment) o;

        if (!baseExperiment.equals(that.baseExperiment)) return false;
        if (!selectedAlternative.equals(that.selectedAlternative)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = baseExperiment.hashCode();
        result = 31 * result + selectedAlternative.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ParticipatingExperiment{" +
                "sixpack=" + sixpack +
                ", baseExperiment=" + baseExperiment +
                ", selectedAlternative=" + selectedAlternative +
                '}';
    }
}
