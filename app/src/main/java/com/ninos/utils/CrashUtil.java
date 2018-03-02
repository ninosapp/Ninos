package com.ninos.utils;

import android.support.v4.BuildConfig;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

/**
 * Created by smeesala on 6/30/2017.
 */

public class CrashUtil {
    private static final String TAG = CrashUtil.class.getSimpleName();

    public static void report(Exception ex) {

        if (ex != null) {

            Log.e(TAG, "report: " + ex.getMessage(), ex);

            if (!BuildConfig.DEBUG) {
                FirebaseCrash.report(ex);
            }
        }
    }

    public static void report(String ex) {

        if (ex != null) {

            Log.e(TAG, "report: " + ex);

            if (!BuildConfig.DEBUG) {
                FirebaseCrash.log(ex);
            }
        }
    }
}