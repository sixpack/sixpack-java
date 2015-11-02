package com.seatgeek.sixpack;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Primary class for starting a new {@link Experiment}.
 *
 * ### Example usage
 *
 * ```java
 *   Experiment colorsExperiment = Sixpack.experiment()
 *       .withName("colors")
 *       .withAlternatives(
 *              new Alternative("control"),
 *              new Alternative("red"),
 *              new Alternative("green"),
 *              new Alternative("blue")
 *        )
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
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Experiment name cannot be empty or null!");
        }

        if (!Sixpack.NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Experiment name must match regex: " + Sixpack.NAME_REGEX);
        }

        this.name = name;
        return this;
    }

    /**
     * Sets the Set of {@link Alternative}s that this {@link Experiment} will have. e.g. ["control", "red",
     * "green", "blue"]
     *
     * @param control the control {@link Alternative} for this experiment
     * @param alternatives the other {@link Alternative}s that will be used in this {@link Experiment}
     * @return this {@link ExperimentBuilder} to allow method chaining
     */
    public ExperimentBuilder withAlternatives(Alternative control, Alternative... alternatives) {
        if (control == null) {
            throw new IllegalArgumentException("Cannot create experiment with null control");
        }

        if (this.alternatives == null) {
            this.alternatives = new LinkedHashSet<>((alternatives != null ? alternatives.length : 0) + 1);
        }

        this.alternatives.add(control);

        if (alternatives != null) {
            Collections.addAll(this.alternatives, alternatives);
        }

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
