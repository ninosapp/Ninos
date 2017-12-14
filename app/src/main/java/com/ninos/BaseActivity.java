package com.ninos;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ninos.utils.CrashUtil;

/**
 * Created by smeesala on 6/30/2017.
 */

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();
    protected Snackbar mSnackbar;

    public void showSnackBar(int msg, View view) {
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

    public void showToast(int msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception e) {
            Log.e(TAG, "isNetworkAvailable() - " + e.toString(), e);
        }
        return false;
    }

    protected void showNetworkDownSnackBar(View view) {
        try {
            Log.w(TAG, "Internet connection not available.");
            mSnackbar = Snackbar
                    .make(view, getResources().getString(R.string.network_down), Snackbar.LENGTH_INDEFINITE)
                    .setActionTextColor(Color.WHITE)
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

    public void logError(Exception exception) {
        CrashUtil.report(exception);
    }

    public void logError(String exception) {
        CrashUtil.report(exception);
    }
}
