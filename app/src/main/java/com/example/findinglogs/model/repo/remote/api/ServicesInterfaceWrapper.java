package com.example.findinglogs.model.repo.remote.api;


import com.example.findinglogs.model.model.Weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public abstract class ServicesInterfaceWrapper {

    public interface WeatherService {
        @GET("weather")
        Call<Weather> getWeather(@Query("lat") String latitude,
                                     @Query("lon") String longitude,
                                     @Query("appid") String appid);
    }
}