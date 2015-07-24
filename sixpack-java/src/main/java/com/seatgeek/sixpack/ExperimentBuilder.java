package com.seatgeek.sixpack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Primary class for starting a new {@link Experiment}.
 *
 * ### Example usage
 *
 * ```java
 *   Experiment colorsExperiment = Sixpack.experiment()
 *       .withName("Colors")
 *       .withAlternative(new Alternative("Red"))
 *       .withAlternative(new Alternative("Green"))
 *       .withAlternative(new Alternative("Blue"))
 *       .withAlternative(new Alternative("Control"))
 *       .build();
 * ```
 */
public class ExperimentBuilder {
    private Sixpack sixpack;
    private String name;
    private Set<Alternative> alternatives;
    private Alternative forcedChoice;
    private Double trafficFraction;

    /**
     * Creates a new {@link ExperimentBuilder} for creating a new {@link Experiment}
     *
     * @param sixpack
     */
    public ExperimentBuilder(Sixpack sixpack) {
        this.sixpack = sixpack;
    }

    /**
     * Names the resulting {@link Experiment}. Experiment names should be unique as they are the
     * primary identifier.
     *
     * @param name the name of this {@link Experiment}, this is the name that the experiment will
     *             have in the dashboard.
     * @return this {@link ExperimentBuilder} to allow method chaining
     */
    public ExperimentBuilder withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the Set of {@link Alternative}s that this {@link Experiment} will have. e.g. ["Red",
     * "Green", "Blue", "Control"]
     *
     * @param alternatives the {@link Alternative}s to be used in this {@link Experiment}
     * @return this {@link ExperimentBuilder} to allow method chaining
     */
    public ExperimentBuilder withAlternatives(Set<Alternative> alternatives) {
        if (this.alternatives == null) {
            this.alternatives = new HashSet<>(alternatives.size());
        }
        this.alternatives.addAll(alternatives);
        return this;
    }

    /**
     * Sets the Set of {@link Alternative}s that this {@link Experiment} will have. e.g. ["Red",
     * "Green", "Blue", "Control"]
     *
     * @param alternatives the {@link Alternative}s that will be used in this {@link Experiment}
     * @return this {@link ExperimentBuilder} to allow method chaining
     */
    public ExperimentBuilder withAlternatives(Alternative... alternatives) {
        if (this.alternatives == null) {
            this.alternatives = new HashSet<>(alternatives.length);
        }
        Collections.addAll(this.alternatives, alternatives);
        return this;
    }

    /**
     * Adds an {@link Alternative} to the Set of {@link Alternative}s that this {@link Experiment}
     * will have. e.g. ["Red", "Green", "Blue", "Control"]
     *
     * @param alternative one {@link Alternative} to be added to this {@link Experiment}
     * @return this {@link ExperimentBuilder} to allow method chaining
     */
    public ExperimentBuilder withAlternative(Alternative alternative) {
        if (this.alternatives == null) {
            this.alternatives = new HashSet<>();
        }
        this.alternatives.add(alternative);
        return this;
    }

    /**
     * Forces a particular {@link Alternative} to be the one selected by Sixpack. Useful for testing
     * or development where you always want the same `Alternative` returned
     *
     * @param forcedChoice the {@link Alternative} to be force-used in this {@link Experiment}
     * @return this {@link ExperimentBuilder} to allow method chaining
     */
    public ExperimentBuilder withForcedChoice(Alternative forcedChoice) {
        this.forcedChoice = forcedChoice;
        return this;
    }

    /**
     * traffic_fraction is an optional parameter; Sixpack allows for limiting experiments to a
     * subset of traffic. You can pass the percentage of traffic you'd like to expose the test to
     * as a decimal number here.
     *
     * @param fraction the fraction of traffic the test will be exposed to, [0.0, 1.0). e.g. 0.1
     *                 results in 10% of the traffic participating in the test
     * @return this {@link ExperimentBuilder} to allow method chaining
     */
    public ExperimentBuilder withTrafficFraction(Double fraction) {
        if (fraction < 0 || fraction > 1) {
            throw new BadTrafficFractionException();
        }

        this.trafficFraction = fraction;
        return this;
    }

    /**
     * @return the new {@link Experiment} that that was just built
     */
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
