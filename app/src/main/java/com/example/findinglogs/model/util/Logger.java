package com.example.findinglogs.model.util;


import android.util.Log;

import com.example.findinglogs.BuildConfig;

public class Logger {
    public static final boolean ISVERBOSE = true;
    private static final String TAG = "My Weather";
    public static final boolean ISLOGABLE = BuildConfig.DEBUG;

    public static void d(String subtag, String message) {
        Log.d(TAG, " [" + subtag + "]: " + message);
    }

    public static void e(String subtag, String message) {
        Log.e(TAG, " [" + subtag + "]: " + message);
    }

    public static void w(String subtag, String message) {
        Log.w(TAG, " [" + subtag + "]: " + message);
    }
}