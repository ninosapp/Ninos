package com.ninos.activities;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.ninos.R;
import com.ninos.adapters.IntroAdapter;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.UserCheckResponse;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ninos.utils.CrashUtil.report;


public class LoginActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, GoogleApiClient.OnConnectionFailedListener {
    public static final int RC_SIGN_IN = 13596;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private View progress_login;
    private LinearLayout ll_google, ll_facebook;
    private View cl_login;
    private List<Integer> mColors;
    private ViewPager view_pager;
    private ArgbEvaluator mARGBEvaluator;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_login);

            cl_login = findViewById(R.id.cl_login);
            progress_login = findViewById(R.id.progress_login);

            ll_google = findViewById(R.id.ll_google);
            ll_google.setOnClickListener(this);

            ll_facebook = findViewById(R.id.ll_facebook);
            ll_facebook.setOnClickListener(this);

            view_pager = findViewById(R.id.view_pager);
            view_pager.setAdapter(new IntroAdapter(getSupportFragmentManager()));
            view_pager.addOnPageChangeListener(this);

            TabLayout tabLayout = findViewById(R.id.tab_layout);
            tabLayout.setupWithViewPager(view_pager, true);

            mColors = new ArrayList<>();
            mColors.add(Color.parseColor("#FF4C36"));
            mColors.add(Color.parseColor("#FF4C36"));
            mColors.add(Color.parseColor("#007807"));
            mColors.add(Color.parseColor("#FF8E01"));

            setStatusColorByIndex(0);
            mARGBEvaluator = new ArgbEvaluator();

            mAuth = FirebaseAuth.getInstance();
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            mCallbackManager = CallbackManager.Factory.create();
        } catch (Exception e) {
            logError(e);
            showSnackBar(R.string.error_message, cl_login);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                showSnackBar(R.string.error_message, cl_login);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        disableButton();

        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            signInUser();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            showSnackBar(R.string.error_message, cl_login);
                            enableButton();
                        }
                    }
                });
    }

    private void signInUser() {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {

            RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
            service.userCheck(user.getUid()).enqueue(new Callback<UserCheckResponse>() {
                @Override
                public void onResponse(@NonNull Call<UserCheckResponse> call, @NonNull Response<UserCheckResponse> response) {
                    if (response.body() != null) {

                        UserCheckResponse uCR = response.body();

                        if (uCR != null && uCR.getSuccess()) {
                            PreferenceUtil.setUserName(LoginActivity.this, uCR.getUserInfo().getChildName());
                            PreferenceUtil.setUserEmail(LoginActivity.this, uCR.getUserInfo().getEmail());
                            PreferenceUtil.setAccessToken(LoginActivity.this, uCR.getToken());
                            PreferenceUtil.setUserInfo(LoginActivity.this, uCR.getUserInfo());
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Intent intent = new Intent(LoginActivity.this, EditProfileActivity.class);
                            intent.putExtra(EditProfileActivity.EMAIL, user.getEmail());
                            intent.putExtra(EditProfileActivity.P_NAME, user.getDisplayName());
                            intent.putExtra(EditProfileActivity.USER_ID, user.getUid());
                            startActivity(intent);
                            finish();
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserCheckResponse> call, Throwable t) {
                    showSnackBar(R.string.error_message, cl_login);
                    enableButton();
                }
            });
        }
    }

    public void enableButton() {
        ll_google.setClickable(true);
        ll_google.setVisibility(View.VISIBLE);
        ll_facebook.setClickable(true);
        ll_facebook.setVisibility(View.VISIBLE);
        progress_login.setVisibility(View.GONE);
    }

    private void disableButton() {
        ll_google.setClickable(false);
        ll_google.setVisibility(View.GONE);
        ll_facebook.setClickable(false);
        ll_facebook.setVisibility(View.GONE);
        progress_login.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        if (isNetworkAvailable()) {
            switch (view.getId()) {
                case R.id.ll_google:
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                    break;
                case R.id.ll_facebook:
                    doFacebookLogin();
                    break;
            }
        } else {
            showNetworkDownSnackBar(cl_login);
        }
    }

    private void doFacebookLogin() {

        try {
            ArrayList<String> permissions = new ArrayList<>();

            permissions.add("email");
            permissions.add("public_profile");

            LoginManager.getInstance().logInWithReadPermissions(this, permissions);

            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    firebaseAuthWithFacebook(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    enableButton();
                    showToast(R.string.error_message);
                }

                @Override
                public void onError(FacebookException e) {
                    enableButton();
                    showToast(R.string.error_message);
                }
            });
        } catch (Exception e) {
            report(e);
        }
    }

    private void firebaseAuthWithFacebook(final AccessToken accessToken) {

        try {
            Log.d(TAG, "firebaseAuthWithFacebook:" + accessToken.getToken());

            disableButton();

            AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());

            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(final @NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                            if (!task.isSuccessful()) {
                                enableButton();
                                Log.w(TAG, "signInWithCredential", task.getException());

                                if (task.getException() != null && task.getException().getMessage().contains("already")) {
                                    showToast(R.string.email_already_registered);
                                } else {
                                    showToast(R.string.error_message);
                                }
                            } else {
                                signInUser();
                            }
                        }
                    });
        } catch (Exception e) {
            enableButton();
            report(e);
            showToast(R.string.error_message);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position < (mColors.size() - 1)) {
            int color = (Integer) mARGBEvaluator.evaluate(positionOffset, mColors.get(position), mColors.get(position + 1));
            setStatusColor(color);
        } else {
            setStatusColorByIndex(position);
        }
    }

    @Override
    public void onPageSelected(int position) {
        setStatusColorByIndex(position);
    }

    private void setStatusColorByIndex(int index) {
        setStatusColor(mColors.get(index));
    }

    private void setStatusColor(int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(color);
        }

        view_pager.setBackgroundColor(color);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        showSnackBar(R.string.playservice_error, cl_login);
    }
}
