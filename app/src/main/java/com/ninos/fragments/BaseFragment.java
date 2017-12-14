package com.ninos.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.ninos.R;
import com.ninos.utils.CrashUtil;

/**
 * Created by sumanth on 6/30/2017.
 */

public class BaseFragment extends Fragment {
    private static final String TAG = BaseFragment.class.getSimpleName();
    protected Snackbar mSnackbar;

    protected void showSnackBar(int msg, View view) {
        final Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
        snackbar.setAction(R.string.dismiss, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.show();
    }

    /**
     * method to check whether internet is available  or not
     *
     * @return true if network available
     */
    protected boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception e) {
            Log.e(TAG, "isNetworkAvailable() - " + e.toString(), e);
        }
        return false;
    }

    /**
     * Shows SnackBar if internet connection is not available
     *
     * @param view current view in which you want to show sanckbar
     */
    protected void showNetworkDownSnackBar(View view) {
        try {
            Log.w(TAG, "Internet connection not available.");
            mSnackbar = Snackbar
                    .make(view, getResources().getString(R.string.network_down), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.dismiss), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSnackbar.dismiss();
                        }
                    });
            mSnackbar.setActionTextColor(Color.WHITE);
            mSnackbar.show();
        } catch (Exception e) {
            Log.e(TAG, "showNetworkDownSnackBar() - " + e.toString(), e);
        }
    }

    protected void logError(Exception exception) {
        CrashUtil.report(exception);
    }

    protected void logError(String exception) {
        CrashUtil.report(exception);
    }
}