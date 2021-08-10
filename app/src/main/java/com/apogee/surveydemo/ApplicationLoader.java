package com.apogee.surveydemo;

import android.content.Context;
import android.os.Handler;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

public class ApplicationLoader extends MultiDexApplication {

    public static String REQUEST_TAG = "global";
    public static volatile Context applicationContext;
    public static volatile Handler applicationHandler;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();

        applicationHandler = new Handler(applicationContext.getMainLooper());
    }
}