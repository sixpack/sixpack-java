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

    Sixpack(String sixpackUrl, String clientId) {
        if (clientId == null || clientId.length() == 0) {
            clientId = generateRandomClientId();
        }
        this.clientId = clientId;
        this.api = getDefaultApi(sixpackUrl, clientId, logLevel);
    }

    Sixpack(SixpackApi api) {
        this(api, generateRandomClientId());
    }

    Sixpack(SixpackApi api, String clientId) {
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
                new ArrayList<Alternative>(experiment.getAlternatives()),
                experiment.getForcedChoice(),
                experiment.getTrafficFraction(),
                new Callback<ParticipateResponse>() {

                    public void success(ParticipateResponse participateResponse, Response response) {
                        if (success != null) {
                            success.onParticipation(new ParticipatingExperiment(Sixpack.this, experiment), participateResponse.getSelectedAlternative());
                        }
                    }

                    public void failure(RetrofitError error) {
                        if (failure != null) {
                            failure.onParticipationFailed(experiment, error);
                        }
                    }
                }
        );
    }

    void convert(final ParticipatingExperiment experiment, final OnConvertSuccess success, final OnConvertFailure failure) {
        api.convert(experiment.getBaseExperiment(),
                new Callback<ConvertResponse>() {
                    public void success(ConvertResponse convertResponse, Response response) {
                        if (success != null) {
                            success.onConverted(new ConvertedExperiment(Sixpack.this, experiment.getBaseExperiment()));
                        }
                    }

                    public void failure(RetrofitError error) {
                        if (failure != null) {
                            failure.onConvertFailure(experiment, error);
                        }
                    }
                });
    }

    static SixpackApi getDefaultApi(final String sixpackUrl, final String clientId, final LogLevel logLevel) {
        Endpoint sixpackEndpoint = new Endpoint() {
            public String getUrl() {
                return sixpackUrl != null ? sixpackUrl : DEFAULT_URL;
            }

            public String getName() {
                return "SixPack";
            }
        };

        RequestInterceptor clientIdInterceptor = new RequestInterceptor() {
            public void intercept(RequestFacade request) {
                request.addQueryParam("client_id", clientId);
            }
        };

        RestAdapter.LogLevel retrofitLogLevel = RestAdapter.LogLevel.NONE;
        switch (logLevel) {
            case VERBOSE:
                retrofitLogLevel = RestAdapter.LogLevel.FULL;
                break;
            case DEBUG:
                retrofitLogLevel = RestAdapter.LogLevel.HEADERS_AND_ARGS;
                break;
            case BASIC:
                retrofitLogLevel = RestAdapter.LogLevel.BASIC;
                break;
        }

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(sixpackEndpoint)
                .setRequestInterceptor(clientIdInterceptor)
                .setLogLevel(retrofitLogLevel)
                .build();

        return adapter.create(SixpackApi.class);
    }
}
