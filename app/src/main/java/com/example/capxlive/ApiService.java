package com.example.capxlive;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiService {
    // Endpoint for the api
    //PROVIDE THE Params for the api
    @GET("symbols/get-chart")
    Call<StockResponse> getChartData(
            @Query("symbol") String symbol,
            @Query("period") String period,
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost
    );
}
