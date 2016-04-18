package com.seatgeek.sixpack;

import com.seatgeek.sixpack.log.LogLevel;
import com.seatgeek.sixpack.log.Logger;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * Builder for creating a new {@link Sixpack} instance.
 *
 * ### Example usage
 *
 *
 * ```java
 *   Sixpack sixpack = new SixpackBuilder()
 *       .setSixpackUrl("http://api.mycompany.com/sixpack")
 *       .setClientId(user != null ? user.sixpackId : getCachedClientId())
 *       .build();
 * ```
 */
public class SixpackBuilder {
    private HttpUrl sixpackUrl;
    private String clientId;
    private LogLevel logLevel;
    private OkHttpClient client;
    private Logger logger;

    /**
     * Sets the Sixpack server url that will be used by this client
     *
     * @param sixpackUrl the server url for the Sixpack server
     * @return this {@link SixpackBuilder} for method chaining
     */
    public SixpackBuilder setSixpackUrl(HttpUrl sixpackUrl) {
        this.sixpackUrl = sixpackUrl;
        return this;
    }

    /**
     * Sets the client id that this {@link Sixpack} client will pass to the Sixpack server when
     * participating and converting tests. It's important that you generate this value once and then
     * cache it so that your client id doesn't change between sessions. It can be useful to store this
     * id on your server information for a given user so that the user can participate in cross-platform
     * tests.
     *
     * @param clientId the client id that will be used by the resulting {@link Sixpack} instance
     * @return this {@link SixpackBuilder} for method chaining
     */
    public SixpackBuilder setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    /**
     * Provide a custom {@link Logger} that will be used by the {@link Sixpack} instance. By default
     * Sixpack will log to stdout in Java apps and logcat in Android apps.
     *
     * @param logger the custom logger to use
     * @return this {@link SixpackBuilder} for method chaining
     */
    public SixpackBuilder setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

    /**
     * @param logLevel the level of logging that will be performed by the {@link Sixpack} instance
     * @return this {@link SixpackBuilder} for method chaining
     */
    public SixpackBuilder setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    /**
     * Sets the {@link okhttp3.OkHttpClient} that Retrofit will use
     *
     * @param client the {@link okhttp3.OkHttpClient} that will be used to make requests to the Sixpack server
     * @return this {@link SixpackBuilder} for method chaining
     */
    public SixpackBuilder setHttpClient(OkHttpClient client) {
        this.client = client;
        return this;
    }

    /**
     * @return the new {@link Sixpack} instance specified by this builder. If defaults values are used
     * for the server url and/or the client_id, it will be logged
     */
    public Sixpack build() {
        boolean usedDefaultUrl = false;
        if (sixpackUrl == null) {
            sixpackUrl = Sixpack.DEFAULT_URL;
            usedDefaultUrl = true;
        }

        boolean generatedDefaultClientId = false;
        if (clientId == null || clientId.length() == 0) {
            clientId = Sixpack.generateRandomClientId();
            generatedDefaultClientId = true;
        }

        Sixpack sixpack = new Sixpack(sixpackUrl, clientId, client);

        if (logLevel != null) {
            sixpack.setLogLevel(logLevel);
        }

        if (logger != null) {
            sixpack.setLogger(logger);
        }

        if (usedDefaultUrl) {
            sixpack.logUseOfDefaultUrl();
        }

        if (generatedDefaultClientId) {
            sixpack.logGeneratedClientId(clientId);
        }

        sixpack.logNewInstanceCreation(sixpackUrl, clientId);

        return sixpack;
    }

    // package method for testing
    HttpUrl getSixpackUrl() {
        return sixpackUrl;
    }
}
