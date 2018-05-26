package in.ninos.utils;


import android.util.Log;


import com.crashlytics.android.Crashlytics;

import in.ninos.BuildConfig;

/**
 * Created by smeesala on 6/30/2017.
 */

public class CrashUtil {
    private static final String TAG = CrashUtil.class.getSimpleName();

    public static void report(Exception ex) {

        if (ex != null) {

            Log.e(TAG, "report: " + ex.getMessage(), ex);

            if (!BuildConfig.DEBUG) {
                Crashlytics.log(ex.getMessage());
            }
        }
    }

    public static void report(String ex) {

        if (ex != null) {

            Log.e(TAG, "report: " + ex);

            if (!BuildConfig.DEBUG) {
                Crashlytics.log(ex);
            }
        }
    }
}