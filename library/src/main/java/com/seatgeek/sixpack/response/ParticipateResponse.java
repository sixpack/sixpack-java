package com.seatgeek.sixpack.response;

import com.google.gson.annotations.SerializedName;
import com.seatgeek.sixpack.Alternative;

public class ParticipateResponse {

    private AlternativeName alternative;

    private ExperimentName experiment;

    @SerializedName("client_id")
    private String clientId;

    public AlternativeName getAlternative() {
        return alternative;
    }

    public void setAlternative(AlternativeName alternative) {
        this.alternative = alternative;
    }

    public ExperimentName getExperiment() {
        return experiment;
    }

    public void setExperiment(ExperimentName experiment) {
        this.experiment = experiment;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Alternative getSelectedAlternative() {
        return alternative != null ? new Alternative(alternative.getName()) : null;
    }
}
