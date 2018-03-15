package in.ninos.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import java.util.Date;

import in.ninos.R;

/**
 * Created by FAMILY on 03-03-2018.
 */

public class AppRaterUtils {

    private static final DateUtil dtu = new DateUtil();
    private final static int LAUNCHES_UNTIL_PROMPT = 3;

    public static void appLaunched(final Context context) {

        try {

            if (context != null) {

                boolean dontShowAgain = PreferenceUtil.isDontShowEnabled(context);

                if (!dontShowAgain) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            int launchCount = PreferenceUtil.getAppRaterLaunchCount(context) + 1;

                            long dateFirstLaunch = PreferenceUtil.getAppRaterDateFirstLaunch(context);

                            Date todayDate = dtu.getDateWithoutTimeStamp(new Date());
                            Date launchDate = dtu.getDateWithoutTimeStamp(new Date(dateFirstLaunch));

                            if (todayDate.getTime() > launchDate.getTime()) {

                                PreferenceUtil.setAppRaterLaunchCount(context, launchCount);

                                if (launchCount >= LAUNCHES_UNTIL_PROMPT) {
                                    PreferenceUtil.setAppRaterLaunchCount(context, 0);
                                    PreferenceUtil.setAppRaterDateFirstLaunch(context, todayDate.getTime());
                                    showRateDialog(context);
                                }
                            }
                        }
                    }, 5000);
                }
            }
        } catch (Exception e) {
            CrashUtil.report(e);
        }
    }

    private static void showRateDialog(final Context context) {

        try {

            if (context != null) {

                final Dialog dialog = new Dialog(context);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_rating);

                final ImageView iv_star1 = dialog.findViewById(R.id.iv_star1);
                final ImageView iv_star2 = dialog.findViewById(R.id.iv_star2);
                final ImageView iv_star3 = dialog.findViewById(R.id.iv_star3);
                final ImageView iv_star4 = dialog.findViewById(R.id.iv_star4);
                final ImageView iv_star5 = dialog.findViewById(R.id.iv_star5);

                final int yellowColor = ContextCompat.getColor(context, R.color.colorAccent);
                final int lightGray = ContextCompat.getColor(context, R.color.silver);

                iv_star1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_star1.setColorFilter(yellowColor);
                    }
                }, 500);

                iv_star2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_star2.setColorFilter(yellowColor);
                    }
                }, 800);

                iv_star3.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_star3.setColorFilter(yellowColor);
                    }
                }, 800);

                iv_star4.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_star4.setColorFilter(yellowColor);
                    }
                }, 1000);

                iv_star5.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_star5.setColorFilter(yellowColor);
                    }
                }, 1000);

                dialog.findViewById(R.id.tv_rate_us).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PreferenceUtil.setDontShowEnabled(context);
                        final String appPackageName = context.getPackageName();

                        try {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }

                        dialog.dismiss();
                    }
                });


                dialog.findViewById(R.id.tv_later).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.findViewById(R.id.tv_never).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PreferenceUtil.setDontShowEnabled(context);
                        dialog.dismiss();
                    }
                });

                iv_star1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iv_star1.setColorFilter(yellowColor);
                        iv_star2.setColorFilter(lightGray);
                        iv_star3.setColorFilter(lightGray);
                        iv_star4.setColorFilter(lightGray);
                        iv_star5.setColorFilter(lightGray);
                    }
                });

                iv_star2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iv_star1.setColorFilter(yellowColor);
                        iv_star2.setColorFilter(yellowColor);
                        iv_star3.setColorFilter(lightGray);
                        iv_star4.setColorFilter(lightGray);
                        iv_star5.setColorFilter(lightGray);
                    }
                });

                iv_star3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iv_star1.setColorFilter(yellowColor);
                        iv_star2.setColorFilter(yellowColor);
                        iv_star3.setColorFilter(yellowColor);
                        iv_star4.setColorFilter(lightGray);
                        iv_star5.setColorFilter(lightGray);
                    }
                });

                iv_star4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iv_star1.setColorFilter(yellowColor);
                        iv_star2.setColorFilter(yellowColor);
                        iv_star3.setColorFilter(yellowColor);
                        iv_star4.setColorFilter(yellowColor);
                        iv_star5.setColorFilter(lightGray);
                    }
                });

                iv_star5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iv_star1.setColorFilter(yellowColor);
                        iv_star2.setColorFilter(yellowColor);
                        iv_star3.setColorFilter(yellowColor);
                        iv_star4.setColorFilter(yellowColor);
                        iv_star5.setColorFilter(yellowColor);
                    }
                });

                if (!((Activity) context).isFinishing()) {
                    dialog.show();
                }
            }
        } catch (Exception e) {
            CrashUtil.report(e);
        }
    }
}
