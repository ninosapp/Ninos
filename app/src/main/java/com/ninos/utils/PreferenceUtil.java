package com.ninos.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.ninos.models.UserInfo;

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

    public static boolean isUserWarned(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("user_warn", false);
    }

    public static void setUserWarn(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("user_warn", true).apply();
    }

    public static boolean isTourShown(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("user_tour", false);
    }

    public static void setTourShown(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("user_tour", true).apply();
    }

    public static UserInfo getUserInfo(Context context) {
        String userInfoValue = PreferenceManager.getDefaultSharedPreferences(context).getString("user_info", null);
        UserInfo userInfo = new Gson().fromJson(userInfoValue, UserInfo.class);
        return userInfo;
    }

    public static void setUserInfo(Context context, UserInfo userInfo) {
        String userInfoValue = new Gson().toJson(userInfo);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("user_info", userInfoValue).apply();
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

    static int getAppRaterLaunchCount(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("launch_count", 0);
    }

    static boolean isDontShowEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dont_show_again", false);
    }

    static void setAppRaterLaunchCount(Context context, int count) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("launch_count", count).apply();
    }

    static long getAppRaterDateFirstLaunch(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong("date_first_launch", 0);
    }

    static void setAppRaterDateFirstLaunch(Context context, long count) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong("date_first_launch", count).apply();
    }

    static void setDontShowEnabled(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("dont_show_again", true).apply();
    }
}
