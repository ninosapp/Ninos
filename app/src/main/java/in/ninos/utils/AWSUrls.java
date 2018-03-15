package in.ninos.utils;

import android.content.Context;

import in.ninos.BuildConfig;
import in.ninos.R;

/**
 * Created by FAMILY on 04-01-2018.
 */

public class AWSUrls {

    private static final String TAG = AWSUrls.class.getSimpleName();

    public static String GetPI64(Context context, String userId) {
        String imageUrl = BuildConfig.AWS_URL + BuildConfig.ams_profile_bucket + "/" + userId + context.getResources().getString(R.string.profile_aws_url_suffix_PI64);
        return imageUrl;
    }


    public static String GetPI192(Context context, String userId) {
        String imageUrl = BuildConfig.AWS_URL + BuildConfig.ams_profile_bucket + "/" + userId + context.getResources().getString(R.string.profile_aws_url_suffix_PI192);
        return imageUrl;
    }
}