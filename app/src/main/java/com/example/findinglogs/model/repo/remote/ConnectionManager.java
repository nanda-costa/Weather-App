package com.example.findinglogs.model.repo.remote;


import com.example.findinglogs.model.util.Logger;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

class ConnectionManager {
    private static final String TAG = ConnectionManager.class.getSimpleName();

    private static final String OPEN_WEATHER_DOMAIN = "https://api.openweathermap.org/data/2.5/";

    private static ConnectionManager instance;
    private static Retrofit sOpenWeatherApiConnection;

    private ConnectionManager() {
        if (Logger.ISLOGABLE) Logger.d(TAG, "ConnectionManager()");
        sOpenWeatherApiConnection = getOpenWeatherConnection();
    }

    static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    public Retrofit getOpenWeatherConnection() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(getLoggerClient())
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(OPEN_WEATHER_DOMAIN)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
    }

    public HttpLoggingInterceptor getLoggerClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
            if (Logger.ISVERBOSE) {
                Logger.d("OkHttp", message);
            }
        });

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return loggingInterceptor;
    }

    public Retrofit getWeatherConnection() {
        return sOpenWeatherApiConnection;
    }
}