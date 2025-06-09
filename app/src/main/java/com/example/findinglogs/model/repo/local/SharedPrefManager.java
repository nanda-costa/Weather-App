package com.example.findinglogs.model.repo.local;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.findinglogs.model.util.Logger;

public class SharedPrefManager {
    private static final String TAG = SharedPrefManager.class.getSimpleName();
    private static final String PREF_FILE_NAME = "MyWeather-Preference";
    private final SharedPreferences mSP;
    private final SharedPreferences.Editor mEditor;
    private static SharedPrefManager instance;

    public static SharedPrefManager getInstance(Application context) {
        if (instance == null) {
            if (Logger.ISLOGABLE) Logger.d(TAG, "SharedPrefManager()");
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    private SharedPrefManager(Application context) {
        mSP = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        mEditor = mSP.edit();
    }

    public void writeString(String key, String value) {
        if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
            if (Logger.ISLOGABLE) Logger.e(TAG, "incorrect data: key:" + key + " value:" + value);
            return;
        }
        mEditor.putString(key, value);
        mEditor.apply();
    }

    public String readString(String key) {
        if (key == null || key.isEmpty()) {
            if (Logger.ISLOGABLE) Logger.e(TAG, "incorrect data: key:" + key);
            return null;
        }
        return mSP.getString(key, null);
    }
}