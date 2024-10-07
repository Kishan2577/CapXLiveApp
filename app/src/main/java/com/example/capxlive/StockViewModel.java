package com.example.capxlive;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StockViewModel extends ViewModel {
    private MutableLiveData<StockResponse.StockData> stockData = new MutableLiveData<>();

    ApiKeys keys = new ApiKeys();
    public LiveData<StockResponse.StockData> getStockData() {
        return stockData;
    }

    public void fetchStockData(String symbol) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(keys.url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService api = retrofit.create(ApiService.class);

        Call<StockResponse> call = api.getChartData(
                symbol,
                keys.period,
                keys.apiKey,
                keys.apiHost
        );

        call.enqueue(new Callback<StockResponse>() {
            @Override
            public void onResponse(Call<StockResponse> call, Response<StockResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Get the most recent date
                    Map<String, StockResponse.StockData> stockDataMap = response.body().getAttributes();
                    // Get the most recent date from the keys
                    String latestDate = stockDataMap.keySet().stream().max(String::compareTo).get();
                    Log.e("TEST","SUCCESS");
                    // Set the most recent stock data
                    stockData.setValue(stockDataMap.get(latestDate));
                }
                else {
                    // When the subscription of the API is Over.
                    stockData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<StockResponse> call, Throwable t) {
                // When data is not fetched provide null.
                stockData.setValue(null);
                Log.e("TEST","FAILURE");
            }
        });
    }
}
