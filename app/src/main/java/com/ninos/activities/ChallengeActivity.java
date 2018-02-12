package com.ninos.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ninos.R;
import com.ninos.adapters.AllChallengeAdapter;
import com.ninos.listeners.OnLoadMoreListener;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.ChallengeInfo;
import com.ninos.models.ChallengeResponse;
import com.ninos.models.PostInfo;
import com.ninos.models.PostsResponse;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.PreferenceUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChallengeActivity extends BaseActivity implements View.OnClickListener, OnLoadMoreListener {


    public static String CHALLENGE_ID = "CHALLENGE_ID";
    private RetrofitService service;
    private int placeHolderId;
    private ImageView iv_challenge;
    private AllChallengeAdapter allChallengeAdapter;
    private String accessToken, challengeId;
    private int from = 0, size = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        accessToken = PreferenceUtil.getAccessToken(this);
        placeHolderId = R.drawable.pattern_5;
        challengeId = getIntent().getStringExtra(CHALLENGE_ID);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), placeHolderId);
        setBitmapPalette(bitmap);

        final TextView tv_challenge = findViewById(R.id.tv_challenge);
        final TextView tv_description = findViewById(R.id.tv_description);
        iv_challenge = findViewById(R.id.iv_challenge);
        iv_challenge.setImageBitmap(bitmap);

        findViewById(R.id.iv_back).setOnClickListener(this);

        LinearLayoutManager challengeLayoutManager = new LinearLayoutManager(this);

        RecyclerView challenge_list = findViewById(R.id.challenge_list);
        challenge_list.setNestedScrollingEnabled(false);
        challenge_list.setLayoutManager(challengeLayoutManager);

        allChallengeAdapter = new AllChallengeAdapter(this, this, challenge_list, this);

        challenge_list.setAdapter(allChallengeAdapter);

        service = RetrofitInstance.createService(RetrofitService.class);
        service.getChallenge(challengeId, PreferenceUtil.getAccessToken(this)).enqueue(new Callback<ChallengeResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChallengeResponse> call, @NonNull Response<ChallengeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChallengeInfo challengeInfo = response.body().getChallengeInfo();

                    tv_challenge.setText(challengeInfo.getTitle());
                    tv_description.setText(challengeInfo.getDescription());

                    updateImage(challengeInfo.getImageUrl());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChallengeResponse> call, @NonNull Throwable t) {

            }
        });

        getPosts();
    }

    private void updateImage(String url) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(placeHolderId)
                .error(placeHolderId)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        Glide.with(getApplicationContext())
                .setDefaultRequestOptions(requestOptions)
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        iv_challenge.setImageBitmap(resource);
                        setBitmapPalette(resource);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                super.onBackPressed();
                break;
        }
    }

    @Override
    public void onLoadMore() {
        allChallengeAdapter.addItem(null);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getPosts();
            }
        }, 5000);
    }


    private void getPosts() {
        service.getChallenges(from, size, "challenge", challengeId, accessToken).enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(@NonNull Call<PostsResponse> call, @NonNull Response<PostsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allChallengeAdapter.removeItem(null);

                    for (final PostInfo postInfo : response.body().getPostInfo()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                allChallengeAdapter.addItem(postInfo);
                            }
                        });
                    }

                    from = from + size;
                }
            }

            @Override
            public void onFailure(Call<PostsResponse> call, Throwable t) {
                logError(t.getMessage());
            }
        });
    }
}
