package in.ninos.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import in.ninos.R;
import in.ninos.utils.CrashUtil;
import in.ninos.utils.PreferenceUtil;

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
                    .setAction(getResources().getString(R.string.dismiss), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSnackbar.dismiss();
                        }
                    });
            mSnackbar.show();
        } catch (Exception e) {
            Log.e(TAG, "showNetworkDownSnackBar() - " + e.toString(), e);
        }
    }

    protected void showNetworkDown() {
        try {
            showToast(R.string.network_down);
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

    public void logout() {

        try {

            PreferenceUtil.clear(this);

            FirebaseAuth.getInstance().signOut();

            LoginManager.getInstance().logOut();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            final GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            mGoogleApiClient.connect();
            mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {

                    if (mGoogleApiClient.isConnected()) {

                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                Log.d(TAG, "User Logged out");
                                goToLogin();
                            }
                        });
                    }
                }

                @Override
                public void onConnectionSuspended(int i) {
                    Log.d(TAG, "Google API Client Connection Suspended");
                    goToLogin();
                }
            });
        } catch (Exception e) {
            CrashUtil.report(e);
            goToLogin();
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
