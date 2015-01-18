package com.seatgeek.sixpack;

public class SixpackBuilder {
    private String sixpackUrl;
    private String clientId;

    public SixpackBuilder setSixpackUrl(String sixpackUrl) {
        this.sixpackUrl = sixpackUrl;
        return this;
    }

    public SixpackBuilder setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public Sixpack build() {
        if (sixpackUrl == null || sixpackUrl.length() == 0) {
            sixpackUrl = Sixpack.DEFAULT_URL;
        }
        
        if (clientId == null || clientId.length() == 0) {
            clientId = Sixpack.generateRandomClientId();
        }
        return new Sixpack(sixpackUrl, clientId);
    }
}