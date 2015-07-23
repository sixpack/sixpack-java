package com.seatgeek.sixpack;

import com.seatgeek.sixpack.log.LogLevel;
import com.seatgeek.sixpack.response.ConvertResponse;
import com.seatgeek.sixpack.response.ParticipateResponse;
import retrofit.*;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.UUID;

public class Sixpack {

    public static final String DEFAULT_URL = "http://localhost:5000";
    public static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.NONE;

    private final SixpackApi api;

    private final String clientId;

    private LogLevel logLevel = DEFAULT_LOG_LEVEL;

    Sixpack(final String sixpackUrl, final String clientId) {
        this.clientId = clientId;
        this.api = getDefaultApi(sixpackUrl, clientId, logLevel);
    }

    Sixpack(final SixpackApi api) {
        this(api, generateRandomClientId());
    }

    Sixpack(final SixpackApi api, final String clientId) {
        this.api = api;
        this.clientId = clientId;
    }

    public ExperimentBuilder experiment() {
        return new ExperimentBuilder(this);
    }

    public String getClientId() {
        return clientId;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public static String generateRandomClientId() {
        return UUID.randomUUID().toString();
    }

    void participateIn(final Experiment experiment, final OnParticipationSuccess success, final OnParticipationFailure failure) {
        api.participate(experiment,
                new ArrayList<>(experiment.alternatives),
                experiment.forcedChoice,
                experiment.trafficFraction,
                getParticipateCallback(experiment, success, failure)
        );
    }

    void convert(final ParticipatingExperiment experiment, final OnConvertSuccess success, final OnConvertFailure failure) {
        api.convert(experiment.baseExperiment,
                getConvertCallback(experiment, success, failure)
        );
    }

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

    static SixpackApi getDefaultApi(final String sixpackUrl, final String clientId, final LogLevel logLevel) {
        Endpoint sixpackEndpoint = getSixpackEndpoint(sixpackUrl);

        RequestInterceptor clientIdInterceptor = getClientIdInterceptor(clientId);

        RestAdapter.LogLevel retrofitLogLevel = getRetrofitLogLevel(logLevel);

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(sixpackEndpoint)
                .setRequestInterceptor(clientIdInterceptor)
                .setLogLevel(retrofitLogLevel)
                .build();

        return adapter.create(SixpackApi.class);
    }

    static RestAdapter.LogLevel getRetrofitLogLevel(final LogLevel logLevel) {
        if (logLevel == null) {
            return RestAdapter.LogLevel.NONE;
        } else if (logLevel == LogLevel.VERBOSE) {
            return RestAdapter.LogLevel.FULL;
        } else if (logLevel == LogLevel.DEBUG) {
            return RestAdapter.LogLevel.HEADERS_AND_ARGS;
        } else if (logLevel == LogLevel.BASIC) {
            return RestAdapter.LogLevel.BASIC;
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
