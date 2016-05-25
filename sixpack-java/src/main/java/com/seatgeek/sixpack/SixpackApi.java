package com.seatgeek.sixpack;

import com.seatgeek.sixpack.response.ConvertResponse;
import com.seatgeek.sixpack.response.ParticipateResponse;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SixpackApi {

    @GET("/participate")
    Call<ParticipateResponse> participate(
            @Query("experiment") Experiment experiment,
            @Query("alternatives") List<Alternative> alternatives,
            @Query("force") Alternative forcedAlternative,
            @Query("traffic_fraction") Double trafficFraction,
            @Query("prefetch") Boolean prefetch
    );

    @GET("/convert")
    Call<ConvertResponse> convert(
            @Query("experiment") Experiment experiment,
            @Query("kpi") String kpi
    );
}
