package com.seatgeek.sixpack.examplejava;

import com.seatgeek.sixpack.*;
import com.seatgeek.sixpack.log.LogLevel;

import java.util.Scanner;

public class SixpackTestJava {

    public static void main(String[] args) {

        // Create a new Sixpack client
        // NOTE: While we've passed in the default url and a random client id,
        // they are optional, if they aren't specified the defaults will be used
        Sixpack sixpack = new SixpackBuilder()
                .setSixpackUrl(Sixpack.DEFAULT_URL)
                .setClientId(Sixpack.generateRandomClientId())
                .build();

        sixpack.setLogLevel(LogLevel.NONE);

        // build a new Experiment
        Experiment pillColor = sixpack.experiment()
                .withName("pill-color")
                .withAlternatives(
                        new Alternative("red"),
                        new Alternative("blue")
                ).build();

        // participate in the new experiment
        ParticipatingExperiment participatingExperiment = pillColor.participate();

        // We successfully participated, now we can use the alternative specified by Sixpack
        System.out.println("Will you take a " + participatingExperiment.selectedAlternative.name + " pill? [y/n]");
        String answer = new Scanner(System.in).nextLine();

        if ("y".equalsIgnoreCase(answer)) {
            // the user selected the "converting" answer! convert them using the ParticipatingExperiment
            try {
                participatingExperiment.convert();

                // And that's it! you should now be able to view your results from sixpack-web
                System.out.println("Success!");
                System.exit(0);
            } catch (ConversionError error) {
                // Failing to covert is likely due to network issues... at this point you can try again
                // or backoff and retry, whatever you think makes the most sense for your application
                System.out.println("Failed to convert in " + participatingExperiment.baseExperiment + ". Error: " + error.getMessage());
                System.exit(2);
            }
        }
    }
}
