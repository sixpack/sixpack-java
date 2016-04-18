package com.seatgeek.sixpack.android;

import com.seatgeek.sixpack.Alternative;
import com.seatgeek.sixpack.Experiment;
import com.seatgeek.sixpack.Sixpack;
import com.seatgeek.sixpack.SixpackBuilder;
import com.seatgeek.sixpack.log.LogLevel;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;

@Module(
        injects = {
                SixpackActivity.class
        }
)
public class SixpackModule {
    public static final String BUTTON_COLOR = "button-color";
    public static final String BUTTON_COLOR_RED = "red";
    public static final String BUTTON_COLOR_BLUE = "blue";

    @Singleton @Provides Sixpack provideSixpack() {
        Sixpack sixpack = new SixpackBuilder()
                .setClientId(Sixpack.generateRandomClientId())
                .setSixpackUrl(HttpUrl.parse("http://10.0.3.2:5000/")) // genymotion host
                .build();

        sixpack.setLogLevel(LogLevel.VERBOSE);

        return sixpack;
    }

    @Singleton @Provides @Named(BUTTON_COLOR) Experiment provideButtonColorExperiment(Sixpack sixpack) {
        return sixpack.experiment()
                .withName(BUTTON_COLOR)
                .withAlternatives(
                        new Alternative(BUTTON_COLOR_RED),
                        new Alternative(BUTTON_COLOR_BLUE)
                )
                .build();
    }
}
