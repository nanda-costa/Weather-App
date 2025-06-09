package com.example.findinglogs.model.repo.remote.api;


import com.example.findinglogs.model.model.Weather;

public interface WeatherCallback {
    void onSuccess(Weather weather);
    void onFailure(String msg);
}