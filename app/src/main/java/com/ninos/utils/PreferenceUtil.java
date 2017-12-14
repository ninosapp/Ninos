package com.ninos.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by FAMILY on 14-12-2017.
 */

public class PreferenceUtil {
    public static String getAccessToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("user_token", null);
    }

    public static void setAccessToken(Context context, String accessToken) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("user_token", accessToken).apply();
    }
}
