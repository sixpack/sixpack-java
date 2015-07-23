package com.seatgeek.sixpack;

import com.seatgeek.sixpack.log.LogLevel;
import com.seatgeek.sixpack.log.Logger;
import com.seatgeek.sixpack.log.PlatformLogger;

import retrofit.client.Client;

public class SixpackBuilder {
    private String sixpackUrl;
    private String clientId;
    private LogLevel logLevel;
    private Client client;
    private Logger logger;

    public SixpackBuilder setSixpackUrl(String sixpackUrl) {
        this.sixpackUrl = sixpackUrl;
        return this;
    }

    public SixpackBuilder setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public SixpackBuilder setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

    public SixpackBuilder setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    public SixpackBuilder setHttpClient(Client client) {
        this.client = client;
        return this;
    }

    public String getSixpackUrl() {
        return sixpackUrl;
    }

    public Sixpack build() {
        if (sixpackUrl == null || sixpackUrl.length() == 0) {
            sixpackUrl = Sixpack.DEFAULT_URL;
        }
        
        if (clientId == null || clientId.length() == 0) {
            clientId = Sixpack.generateRandomClientId();
        }

        Sixpack sixpack = new Sixpack(sixpackUrl, clientId, client);

        if (logLevel != null) {
            sixpack.setLogLevel(logLevel);
        }

        if (logger != null) {
            sixpack.setLogger(logger);
        }

        return sixpack;
    }
}
