package com.ninos;

import android.app.Application;

import com.facebook.FacebookSdk;

/**
 * Created by FAMILY on 08-01-2018.
 */

public class NinosApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
