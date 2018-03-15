package in.ninos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import in.ninos.listeners.RetrofitService;
import in.ninos.models.RegisterResponse;
import in.ninos.reterofit.RetrofitInstance;
import in.ninos.utils.PreferenceUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaunchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String accessToken = PreferenceUtil.getAccessToken(this);

        if (accessToken == null) {
            startLogin();
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
                        startLogin();
                    }
                });
            } else {
                startLogin();
            }
        }
    }

    private void startLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void startHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
