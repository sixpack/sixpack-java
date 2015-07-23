package com.seatgeek.sixpack.response;

import com.google.gson.annotations.SerializedName;
import com.seatgeek.sixpack.Alternative;

public class ParticipateResponse {

    private Alternative selectedAlternative;

    public AlternativeName alternative;

    public ExperimentName experiment;

    @SerializedName("client_id")
    public String clientId;

    public Alternative getSelectedAlternative() {
        if (selectedAlternative == null) {
            selectedAlternative = alternative != null ? new Alternative(alternative.name) : null;
        }

        return selectedAlternative;
    }
}
