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

    public static void setUserName(Context context, String userInfo) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("user_name", userInfo).apply();
    }

    public static String getUserName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("user_name", null);
    }

    public static void setUserEmail(Context context, String userInfo) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("user_email", userInfo).apply();
    }

    public static String getUserEmail(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("user_email", null);
    }

    public static void clear(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();
    }
}
