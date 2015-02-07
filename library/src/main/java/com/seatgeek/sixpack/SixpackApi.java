package com.seatgeek.sixpack;

import com.seatgeek.sixpack.response.ConvertResponse;
import com.seatgeek.sixpack.response.ParticipateResponse;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

import java.util.List;

public interface SixpackApi {

    @GET("/participate")
    public void participate(
            @Query("experiment") Experiment experiment,
            @Query("alternatives") List<Alternative> alternatives,
            @Query("force") Alternative forcedAlternative,
            @Query("traffic_fraction") Double trafficFraction,
            Callback<ParticipateResponse> callback
    );

    @GET("/convert")
    public void convert(
            @Query("experiment") Experiment experiment,
            Callback<ConvertResponse> callback
    );
}
