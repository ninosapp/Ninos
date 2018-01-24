package com.ninos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ninos.listeners.RetrofitService;
import com.ninos.models.RegisterResponse;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.PreferenceUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaunchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String accessToken = PreferenceUtil.getAccessToken(this);

        if (accessToken == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        } else {

            if (isNetworkAvailable()) {
                RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
                service.refreshToken(PreferenceUtil.getAccessToken(this)).enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            PreferenceUtil.setAccessToken(LaunchActivity.this, response.body().getToken());
                        }

                        startHome();
                    }

                    @Override
                    public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        startHome();
                    }
                });
            } else {
                startHome();
            }
        }

        finish();
    }

    private void startHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
