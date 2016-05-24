package com.seatgeek.sixpack;

import com.seatgeek.sixpack.log.HttpLoggingInterceptorLoggerAdapter;
import com.seatgeek.sixpack.log.LogLevel;
import com.seatgeek.sixpack.log.Logger;
import com.seatgeek.sixpack.log.PlatformLogger;
import com.seatgeek.sixpack.response.ConvertResponse;
import com.seatgeek.sixpack.response.ParticipateResponse;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
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
    public static final HttpUrl DEFAULT_URL = HttpUrl.parse("http://localhost:5000/");

    public static final String NAME_REGEX = "^[a-z0-9][a-z0-9\\-_ ]*$";

    public static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);

    /**
     * By default, Sixpack will log nothing
     */
    public static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.NONE;

    public static final String SIXPACK_LOG_TAG = "Sixpack";

    private final SixpackApi api;

    private final String clientId;

    private LogLevel logLevel = DEFAULT_LOG_LEVEL;

    private Logger logger = PlatformLogger.INSTANCE.getLogger();

    /** not exposed, use {@link SixpackBuilder} */
    Sixpack(final HttpUrl sixpackUrl, final String clientId, final OkHttpClient client) {
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
            Response<ParticipateResponse> response = api.participate(experiment,
                    new ArrayList<>(experiment.alternatives),
                    experiment.forcedChoice,
                    experiment.trafficFraction,
                    null
            ).execute();

            if (response.isSuccessful()) {
                return new ParticipatingExperiment(Sixpack.this, experiment, response.body().getSelectedAlternative());
            } else {
                return getControlParticipation(experiment);
            }
        } catch (RuntimeException | IOException e) {
            logException(experiment, e);
            return getControlParticipation(experiment);
        }
    }

    private ParticipatingExperiment getControlParticipation(Experiment experiment) {
        return new ParticipatingExperiment(Sixpack.this, experiment, experiment.getControlAlternative());
    }

    /**
     * Internal method used by {@link Experiment} to prefetch a selected alternative
     */
    PrefetchedExperiment prefetch(final Experiment experiment) {
        logPrefetch(experiment);

        try {
            Response<ParticipateResponse> response = api.participate(experiment,
                    new ArrayList<>(experiment.alternatives),
                    experiment.forcedChoice,
                    experiment.trafficFraction,
                    true
            ).execute();

            if (response.isSuccessful()) {
                return new PrefetchedExperiment(Sixpack.this, experiment, response.body().getSelectedAlternative());
            } else {
                return getControlPrefetch(experiment);
            }
        } catch (RuntimeException | IOException e) {
            logException(experiment, e);
            return getControlPrefetch(experiment);
        }
    }

    private PrefetchedExperiment getControlPrefetch(Experiment experiment) {
        return new PrefetchedExperiment(Sixpack.this, experiment, experiment.getControlAlternative());
    }

    /**
     * Internal method used by {@link ParticipatingExperiment} to indicate that a conversion has occurred
     */
    ConvertedExperiment convert(final ParticipatingExperiment experiment, String kpi) {
        logConvert(experiment, kpi);

        try {
            Response<ConvertResponse> response = api.convert(experiment.baseExperiment, kpi).execute();

            if (response.isSuccessful()) {
                return new ConvertedExperiment(Sixpack.this, experiment.baseExperiment);
            } else {
                logFailedConversion(experiment.baseExperiment, response.code());
                throw new ConversionError(null, experiment.baseExperiment);
            }
        } catch (RuntimeException | IOException e) {
            logException(experiment.baseExperiment, e);
            throw new ConversionError(e, experiment.baseExperiment);
        }
    }

    /**
     * Internal method for building the {@link SixpackApi} Rest Adapter and returning the Api instance
     */
    static SixpackApi getDefaultApi(final HttpUrl sixpackUrl, final String clientId, final LogLevel logLevel, final OkHttpClient client) {
        HttpUrl sixpackEndpoint = getSixpackEndpoint(sixpackUrl);

        final OkHttpClient finalClient;
        if (client == null) {
            finalClient = getDefaultOkHttpClient();
        } else {
            finalClient = client;
        }

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptorLoggerAdapter(PlatformLogger.INSTANCE.getLogger()))
                .setLevel(getRetrofitLogLevel(logLevel));

        Interceptor clientIdInterceptor = getClientIdInterceptor(clientId);

        OkHttpClient interceptedClient = finalClient.newBuilder()
                .addInterceptor(clientIdInterceptor)
                .addInterceptor(loggingInterceptor)
                .build();

        GsonConverterFactory gsonConverter = getDefaultGsonConverter();

        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(sixpackEndpoint)
                .client(interceptedClient)
                .addConverterFactory(gsonConverter)
                .build();

        return adapter.create(SixpackApi.class);
    }

    private static GsonConverterFactory getDefaultGsonConverter() {
        return GsonConverterFactory.create();
    }

    static OkHttpClient getDefaultOkHttpClient() {
        return new OkHttpClient();
    }

    /**
     * Internal utility for converting between the retrofit log level and our internal log level
     */
    static HttpLoggingInterceptor.Level getRetrofitLogLevel(final LogLevel logLevel) {
        if (logLevel == null) {
            return HttpLoggingInterceptor.Level.NONE;
        } else if (logLevel == LogLevel.VERBOSE) {
            return HttpLoggingInterceptor.Level.BODY;
        } else if (logLevel == LogLevel.DEBUG) {
            return HttpLoggingInterceptor.Level.HEADERS;
        } else {
            return HttpLoggingInterceptor.Level.NONE;
        }
    }

    static Interceptor getClientIdInterceptor(final String clientId) {
        return new Interceptor() {

            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                final Request request = chain.request();
                final Request.Builder builder = request.newBuilder()
                        .url(request.url().newBuilder().addQueryParameter("client_id", clientId).build());

                return chain.proceed(builder.build());
            }
        };
    }

    static HttpUrl getSixpackEndpoint(final HttpUrl sixpackUrl) {
        return sixpackUrl != null ? sixpackUrl : DEFAULT_URL;
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

    void logNewInstanceCreation(final HttpUrl sixpackUrl, final String clientId) {
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

    void logPrefetch(final Experiment experiment) {
        if (logLevel.isAtLeastVerbose()) {
            logger.log(
                    SIXPACK_LOG_TAG,
                    String.format(
                            "Prefetching Experiment: name=%s, alternatives=%s, forcedChoice=%s, trafficFraction=%s",
                            experiment.name, experiment.alternatives, experiment.forcedChoice, experiment.trafficFraction
                    )
            );
        } else if (logLevel.isAtLeastDebug()) {
            logger.log(SIXPACK_LOG_TAG, String.format("Prefetching Experiment: name=%s", experiment.name));
        }
    }

    void logConvert(final ParticipatingExperiment experiment, String kpi) {
        if (logLevel.isAtLeastVerbose()) {
            logger.log(
                    SIXPACK_LOG_TAG,
                    String.format(
                            "Converting Experiment: name=%s, alternatives=%s, forcedChoice=%s, trafficFraction=%s, kpi=%s",
                            experiment.baseExperiment.name, experiment.baseExperiment.alternatives, experiment.baseExperiment.forcedChoice, experiment.baseExperiment.trafficFraction, kpi
                    )
            );
        } else if (logLevel.isAtLeastDebug()) {
            logger.log(SIXPACK_LOG_TAG, String.format("Converting Experiment: name=%s", experiment.baseExperiment.name));
        }
    }

    void logException(final Experiment experiment, final Exception e) {
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

    private void logFailedConversion(Experiment experiment, int httpResponseCode) {
        if (logLevel.isAtLeastVerbose()) {
            logger.log(
                    SIXPACK_LOG_TAG,
                    String.format(
                            "Exception converting Experiment: httpResponseCode=%d, name=%s, alternatives=%s, forcedChoice=%s, trafficFraction=%s",
                            httpResponseCode, experiment.name, experiment.alternatives, experiment.forcedChoice, experiment.trafficFraction
                    )
            );
        } else if (logLevel.isAtLeastDebug()) {
            logger.log(SIXPACK_LOG_TAG, String.format("Exception converting Experiment: httpResponseCode=%d, name=%s", httpResponseCode, experiment.name));
        }
    }
}
