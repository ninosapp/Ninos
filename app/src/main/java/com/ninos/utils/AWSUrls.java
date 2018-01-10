package com.ninos.utils;

import android.content.Context;

import com.ninos.BuildConfig;
import com.ninos.R;

/**
 * Created by FAMILY on 04-01-2018.
 */

public class AWSUrls {

    private static final String TAG = AWSUrls.class.getSimpleName();

    /**
     * User 48X48 resolution image url
     *
     * @param context in which class the method is invoked
     * @param userId  id of user
     * @return
     */
    public static String GetPI64(Context context, String userId) {
        String imageUrl = "https://s3.amazonaws.com/" + BuildConfig.ams_profile_bucket + "/" + userId + context.getResources().getString(R.string.profile_aws_url_suffix_PI64);
        return imageUrl;
    }

    /**
     * User 128X128 resolution image url
     *
     * @param context in which class the method is invoked
     * @param userId  id of user
     * @return
     */
    public static String GetPI128(Context context, String userId) {
        String imageUrl = "https://s3.amazonaws.com/" + BuildConfig.ams_profile_bucket + "/" + userId + context.getResources().getString(R.string.profile_aws_url_suffix_PI128);
        return imageUrl;
    }

    /**
     * User 256X256 resolution image url
     *
     * @param context in which class the method is invoked
     * @param userId  id of user
     * @return
     */
    public static String GetPI256(Context context, String userId) {
        String imageUrl = "https://s3.amazonaws.com/" + BuildConfig.ams_profile_bucket + "/" + userId + context.getResources().getString(R.string.profile_aws_url_suffix_PI512);
        return imageUrl;
    }

    public static String GetPI512(Context context, String userId) {
        String imageUrl = "https://s3.amazonaws.com/" + BuildConfig.ams_profile_bucket + "/" + userId + context.getResources().getString(R.string.profile_aws_url_suffix_PI512);
        return imageUrl;
    }
}