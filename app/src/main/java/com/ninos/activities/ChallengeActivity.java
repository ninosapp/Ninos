package com.ninos.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ninos.R;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.ChallengeInfo;
import com.ninos.models.ChallengeResponse;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.PreferenceUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChallengeActivity extends BaseActivity {


    public static String CHALLENGE_ID = "CHALLENGE_ID";
    private RetrofitService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pattern_4);
        setBitmapPalette(bitmap);

        String challengeId = getIntent().getStringExtra(CHALLENGE_ID);

        final TextView tv_title = findViewById(R.id.tv_title);
        final TextView tv_description = findViewById(R.id.tv_description);

        service = RetrofitInstance.createService(RetrofitService.class);
        service.getChallenge(challengeId, PreferenceUtil.getAccessToken(this)).enqueue(new Callback<ChallengeResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChallengeResponse> call, @NonNull Response<ChallengeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChallengeInfo challengeInfo = response.body().getChallengeInfo();

                    tv_title.setText(challengeInfo.getTitle());
                    tv_description.setText(challengeInfo.getDescription());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChallengeResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void setBitmapPalette(Bitmap resource) {
        if (resource != null) {
            Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(palette.getDominantColor(Color.BLACK));
                    }
                }
            });
        }
    }
}
