package com.apogee.surveydemo.utility;

import android.app.Application;

public class MyInternetConnection extends Application {
    private static MyInternetConnection mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized MyInternetConnection getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReciever.ConnectivityReceiverListener listener) {
        ConnectivityReciever.connectivityReceiverListener = listener;
    }
}
