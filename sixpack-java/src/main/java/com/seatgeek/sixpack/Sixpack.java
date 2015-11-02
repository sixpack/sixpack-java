package com.seatgeek.sixpack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seatgeek.sixpack.log.LogLevel;
import com.seatgeek.sixpack.log.Logger;
import com.seatgeek.sixpack.log.PlatformLogger;
import com.seatgeek.sixpack.response.ConvertResponse;
import com.seatgeek.sixpack.response.ParticipateResponse;

import retrofit.Callback;
import retrofit.Endpoint;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Primary class for interacting with Sixpack A/B testing. For full documentation about sixpack-java,
 * please check out the README at [github.com/seatgeek/sixpack-java](https://github.com/seatgeek/sixpack-java#sixpack-java)
 *
 * If you're looking for getting started information with an instance of a Sixpack server, please
 * read the main project's documentation on that subject at [github.com/seatgeek/sixpack](https://github.com/seatgeek/sixpack/#getting-started)
 */
public class Sixpack {

    /**
     * Default url for a {@link Sixpack} instance hosted with default configuration on the local machine
     */
    public static final String DEFAULT_URL = "http://localhost:5000";

    public static final String NAME_REGEX = "^[a-z0-9][a-z0-9\\-_ ]*$";

    public static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);

    /**
     * By default, Sixpack will log nothing
     */
    public static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.NONE;

    private static final String SIXPACK_LOG_TAG = "Sixpack";

    private final SixpackApi api;

    private final String clientId;

    private LogLevel logLevel = DEFAULT_LOG_LEVEL;

    private Logger logger = PlatformLogger.INSTANCE.getLogger();

    /** not exposed, use {@link SixpackBuilder} */
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

    /**
     * Creates a new {@link ExperimentBuilder} for starting a new experiment
     */
    public ExperimentBuilder experiment() {
        return new ExperimentBuilder(this);
    }

    /**
     * While you can query the current client id, you must create a new {@link Sixpack} instance to change it
     * @return the current client id being used by this sixpack instance
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets the log level used by various functions of the Sixpack client.
     *
     * Roughly:
     *
     * - `LogLevel.VERBOSE` is going to enable full logging for all events
     * - `LogLevel.DEBUG` is going to enable basic logging for most events
     * - `LogLevel.NONE` will disable logging entirely
     * @param logLevel the new log level
     */
    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * @return the current {@link LogLevel} being used by Sixpack for
     */
    public LogLevel getLogLevel() {
        return logLevel;
    }

    /**
     * @param logger the new {@link Logger} that this Sixpack instance will use for logging events
     */
    public void setLogger(final Logger logger) {
        this.logger = logger;
    }

    /**
     * @return a randomly generated, UUID formatted, client id for use with a Sixpack instance
     */
    public static String generateRandomClientId() {
        return UUID.randomUUID().toString();
    }

    /* end public methods */

    /**
     * Internal method used by {@link Experiment} to start a participation in a test
     */
    ParticipatingExperiment participate(final Experiment experiment) {
        logParticipate(experiment);

        try {
            ParticipateResponse response = api.participate(experiment,
                    new ArrayList<>(experiment.alternatives),
                    experiment.forcedChoice,
                    experiment.trafficFraction
            );

            return new ParticipatingExperiment(Sixpack.this, experiment, response.getSelectedAlternative());
        } catch (RuntimeException e) {
            logException(experiment, e);
            return new ParticipatingExperiment(Sixpack.this, experiment, experiment.getControlAlternative());
        }
    }

    /**
     * Internal method used by {@link ParticipatingExperiment} to indicate that a conversion has occurred
     */
    ConvertedExperiment convert(final ParticipatingExperiment experiment) {
        logConvert(experiment);

        try {
            ConvertResponse x = api.convert(experiment.baseExperiment);

            return new ConvertedExperiment(Sixpack.this, experiment.baseExperiment);
        } catch (RuntimeException e) {
            logException(experiment.baseExperiment, e);
            throw new ConversionError(e, experiment.baseExperiment);
        }
    }

    /**
     * Internal method for building the {@link SixpackApi} Rest Adapter and returning the Api instance
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

    /* logging */

    void logNewExperiment(final String name, final Set<Alternative> alternatives, final Alternative forcedChoice, final Double trafficFraction) {
        if (logLevel.isAtLeastVerbose()) {
            logger.log(
                    SIXPACK_LOG_TAG,
                    String.format(
                            "Created new Experiment: name=%s, alternatives=%s, forcedChoice=%s, trafficFraction=%s",
                            name, alternatives, forcedChoice, trafficFraction
                    )
            );
        } else if (logLevel.isAtLeastDebug()) {
            logger.log(SIXPACK_LOG_TAG, String.format("Created new Experiment: name=%s", name));
        }
    }

    void logUseOfDefaultUrl() {
        if (logLevel.isAtLeastDebug()) {
            logger.log(SIXPACK_LOG_TAG, "Warning! Using default Sixpack url of " + DEFAULT_URL +
                    ". If your server instance is not set up locally on your machine your requests will fail!");
        }
    }

    void logGeneratedClientId(final String clientId) {
        if (logLevel.isAtLeastDebug()) {
            logger.log(SIXPACK_LOG_TAG, String.format("Warning! Using auto-generated client id of %s" +
                    ". If your client id changes on each instance creation, you won't get the same test results", clientId));
        }
    }

    void logNewInstanceCreation(final String sixpackUrl, final String clientId) {
        if (logLevel.isAtLeastVerbose()) {
            logger.log(
                    SIXPACK_LOG_TAG,
                    String.format(
                            "Created new Sixpack client with sixpackUrl=%s, clientId=%s",
                            sixpackUrl, clientId
                    )
            );
        } else if (logLevel.isAtLeastDebug()) {
            logger.log(SIXPACK_LOG_TAG, "Created new Sixpack client");
        }
    }

    void logParticipate(final Experiment experiment) {
        if (logLevel.isAtLeastVerbose()) {
            logger.log(
                    SIXPACK_LOG_TAG,
                    String.format(
                            "Participating in Experiment: name=%s, alternatives=%s, forcedChoice=%s, trafficFraction=%s",
                            experiment.name, experiment.alternatives, experiment.forcedChoice, experiment.trafficFraction
                    )
            );
        } else if (logLevel.isAtLeastDebug()) {
            logger.log(SIXPACK_LOG_TAG, String.format("Participating in Experiment: name=%s", experiment.name));
        }
    }

    void logConvert(final ParticipatingExperiment experiment) {
        if (logLevel.isAtLeastVerbose()) {
            logger.log(
                    SIXPACK_LOG_TAG,
                    String.format(
                            "Converting Experiment: name=%s, alternatives=%s, forcedChoice=%s, trafficFraction=%s",
                            experiment.baseExperiment.name, experiment.baseExperiment.alternatives, experiment.baseExperiment.forcedChoice, experiment.baseExperiment.trafficFraction
                    )
            );
        } else if (logLevel.isAtLeastDebug()) {
            logger.log(SIXPACK_LOG_TAG, String.format("Converting Experiment: name=%s", experiment.baseExperiment.name));
        }
    }

    void logException(final Experiment experiment, final RuntimeException e) {
        if (logLevel.isAtLeastVerbose()) {
            logger.loge(
                    SIXPACK_LOG_TAG,
                    String.format(
                            "Exception with Experiment: name=%s, alternatives=%s, forcedChoice=%s, trafficFraction=%s",
                            experiment.name, experiment.alternatives, experiment.forcedChoice, experiment.trafficFraction
                    ),
                    e
            );
        } else if (logLevel.isAtLeastDebug()) {
            logger.loge(SIXPACK_LOG_TAG, String.format("Exception with Experiment: name=%s", experiment.name), e);
        }
    }
}
