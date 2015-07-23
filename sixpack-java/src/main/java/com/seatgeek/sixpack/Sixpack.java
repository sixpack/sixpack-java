package com.seatgeek.sixpack;

import android.os.StatFs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seatgeek.sixpack.log.LogLevel;
import com.seatgeek.sixpack.log.Logger;
import com.seatgeek.sixpack.log.PlatformLogger;
import com.seatgeek.sixpack.response.ConvertResponse;
import com.seatgeek.sixpack.response.ParticipateResponse;

import retrofit.*;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.http.Query;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class Sixpack {

    public static final String DEFAULT_URL = "http://localhost:5000";

    public static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.NONE;

    public static final String SIXPACK_LOG_TAG = "Sixpack";

    private final SixpackApi api;

    private final String clientId;

    private LogLevel logLevel = DEFAULT_LOG_LEVEL;

    private Logger logger = PlatformLogger.INSTANCE.getLogger();

    Sixpack(final String sixpackUrl, final String clientId, final Client client) {
        this.clientId = clientId;
        this.api = getDefaultApi(sixpackUrl, clientId, logLevel, client);
    }

    Sixpack(final SixpackApi api) {
        this(api, generateRandomClientId());
    }

    Sixpack(final SixpackApi api, final String clientId) {
        this.clientId = clientId;
        this.api = api;
    }

    public ExperimentBuilder experiment() {
        return new ExperimentBuilder(this);
    }

    void logNewExperiment(final String name, final Set<Alternative> alternatives, final Alternative forcedChoice, final Double trafficFraction) {
        if (logLevel.isAtLeastVerbose()) {
            verboseLogNewExperiment(name, alternatives, forcedChoice, trafficFraction);
        } else if (logLevel.isAtLeastDebug()) {
            debugLogNewExperiment(name);
        }
    }

    private void debugLogNewExperiment(final String name) {
        logger.log(SIXPACK_LOG_TAG, String.format("Created new Experiment: name=%s", name));
    }

    private void verboseLogNewExperiment(final String name, final Set<Alternative> alternatives, final Alternative forcedChoice, final Double trafficFraction) {
        logger.log(
                SIXPACK_LOG_TAG,
                String.format(
                        "Created new Experiment: name=%s, alternatives=%s, forcedChoice=%s, trafficFraction=%s",
                        name, alternatives, forcedChoice, trafficFraction
                )
        );
    }

    public String getClientId() {
        return clientId;
    }

    /**
     * todo docs
     * @param logLevel
     */
    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * todo docs
     * @return
     */
    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogger(final Logger logger) {
        this.logger = logger;
    }

    /**
     * todo docs
     * @return
     */
    public static String generateRandomClientId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Internal method used by {@code Experiment} to start a participation in a test
     */
    void participateIn(final Experiment experiment, final OnParticipationSuccess success, final OnParticipationFailure failure) {
        logParticipate(experiment);

        api.participate(experiment,
                new ArrayList<>(experiment.alternatives),
                experiment.forcedChoice,
                experiment.trafficFraction,
                getParticipateCallback(experiment, success, failure)
        );
    }

    private void logParticipate(final Experiment experiment) {
        if (logLevel.isAtLeastVerbose()) {
            verboseLogParticipate(experiment);
        } else if (logLevel.isAtLeastDebug()) {
            debugLogParticipate(experiment);
        }
    }

    private void debugLogParticipate(final Experiment experiment) {
        logger.log(SIXPACK_LOG_TAG, String.format("Participating in Experiment: name=%s", experiment.name));
    }

    private void verboseLogParticipate(final Experiment experiment) {
        logger.log(
                SIXPACK_LOG_TAG,
                String.format(
                        "Participating in Experiment: name=%s, alternatives=%s, forcedChoice=%s, trafficFraction=%s",
                        experiment.name, experiment.alternatives, experiment.forcedChoice, experiment.trafficFraction
                )
        );
    }

    /**
     * Internal method used by {@code ParticipatingExperiment} to indicate that a conversion has occurred
     */
    void convert(final ParticipatingExperiment experiment, final OnConvertSuccess success, final OnConvertFailure failure) {
        logConvert(experiment);

        api.convert(experiment.baseExperiment,
                getConvertCallback(experiment, success, failure)
        );
    }

    private void logConvert(final ParticipatingExperiment experiment) {
        if (logLevel.isAtLeastVerbose()) {
            verboseLogConvert(experiment);
        } else if (logLevel.isAtLeastDebug()) {
            debugLogConvert(experiment);
        }
    }

    private void debugLogConvert(final ParticipatingExperiment experiment) {
        logger.log(SIXPACK_LOG_TAG, String.format("Converting Experiment: name=%s", experiment.baseExperiment.name));
    }

    private void verboseLogConvert(final ParticipatingExperiment experiment) {
        logger.log(
                SIXPACK_LOG_TAG,
                String.format(
                        "Converting Experiment: name=%s, alternatives=%s, forcedChoice=%s, trafficFraction=%s",
                        experiment.baseExperiment.name, experiment.baseExperiment.alternatives, experiment.baseExperiment.forcedChoice, experiment.baseExperiment.trafficFraction
                )
        );
    }

    /**
     * Internal method for getting the Retrofit {@code Callback} to use in the participation request
     */
    Callback<ParticipateResponse> getParticipateCallback(final Experiment experiment, final OnParticipationSuccess success, final OnParticipationFailure failure) {
        return new Callback<ParticipateResponse>() {

            public void success(final ParticipateResponse participateResponse, final Response response) {
                if (success != null) {
                    success.onParticipation(new ParticipatingExperiment(Sixpack.this, experiment, participateResponse.getSelectedAlternative()));
                }
            }

            public void failure(final RetrofitError error) {
                if (failure != null) {
                    failure.onParticipationFailed(experiment, error);
                }
            }
        };
    }

    /**
     * Internal method for getting the Retrofit {@code Callback} to use in the convert request
     */
    Callback<ConvertResponse> getConvertCallback(final ParticipatingExperiment experiment, final OnConvertSuccess success, final OnConvertFailure failure) {
        return new Callback<ConvertResponse>() {

            public void success(final ConvertResponse convertResponse, final  Response response) {
                if (success != null) {
                    success.onConverted(new ConvertedExperiment(Sixpack.this, experiment.baseExperiment));
                }
            }

            public void failure(final RetrofitError error) {
                if (failure != null) {
                    failure.onConvertFailure(experiment, error);
                }
            }
        };
    }

    /**
     * Internal method for building the SixpackApi RestAdapter and returning the Api instance
     */
    static SixpackApi getDefaultApi(final String sixpackUrl, final String clientId, final LogLevel logLevel, final Client client) {
        Endpoint sixpackEndpoint = getSixpackEndpoint(sixpackUrl);

        RequestInterceptor clientIdInterceptor = getClientIdInterceptor(clientId);

        RestAdapter.LogLevel retrofitLogLevel = getRetrofitLogLevel(logLevel);

        final Client finalClient;
        if (client == null) {
            finalClient = getDefaultOkHttpClient();
        } else {
            finalClient = client;
        }

        GsonConverter gsonConverter = getDefaultGsonConverter();

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(sixpackEndpoint)
                .setClient(finalClient)
                .setConverter(gsonConverter)
                .setRequestInterceptor(clientIdInterceptor)
                .setLogLevel(retrofitLogLevel)
                .build();

        return adapter.create(SixpackApi.class);
    }

    private static GsonConverter getDefaultGsonConverter() {
        Gson gson = new GsonBuilder().create();

        return new GsonConverter(gson);
    }

    static Client getDefaultOkHttpClient() {
        return new OkClient();
    }

    /**
     * Internal utility for converting between the retrofit log level and our internal log level
     */
    static RestAdapter.LogLevel getRetrofitLogLevel(final LogLevel logLevel) {
        if (logLevel == null) {
            return RestAdapter.LogLevel.NONE;
        } else if (logLevel == LogLevel.VERBOSE) {
            return RestAdapter.LogLevel.FULL;
        } else if (logLevel == LogLevel.DEBUG) {
            return RestAdapter.LogLevel.HEADERS_AND_ARGS;
        } else {
            return RestAdapter.LogLevel.NONE;
        }
    }

    static RequestInterceptor getClientIdInterceptor(final String clientId) {
        return new RequestInterceptor() {
            public void intercept(final RequestFacade request) {
                request.addQueryParam("client_id", clientId);
            }
        };
    }

    static Endpoint getSixpackEndpoint(final String sixpackUrl) {
        return new Endpoint() {
            public String getUrl() {
                return sixpackUrl != null ? sixpackUrl : DEFAULT_URL;
            }

            public String getName() {
                return "SixPack";
            }
        };
    }
}
