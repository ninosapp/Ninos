package com.ninos;

import android.app.Application;

import com.github.tcking.giraffecompressor.GiraffeCompressor;

/**
 * Created by FAMILY on 20-01-2018.
 */

public class NinosApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        GiraffeCompressor.init(this);
    }
}
