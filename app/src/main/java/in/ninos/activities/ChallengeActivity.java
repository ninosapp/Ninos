package in.ninos.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import cn.jzvd.JZVideoPlayer;
import in.ninos.R;
import in.ninos.adapters.AllChallengeAdapter;
import in.ninos.listeners.OnLoadMoreListener;
import in.ninos.listeners.RetrofitService;
import in.ninos.models.ChallengeInfo;
import in.ninos.models.ChallengeResponse;
import in.ninos.models.PostInfo;
import in.ninos.models.PostResponse;
import in.ninos.models.PostsResponse;
import in.ninos.reterofit.RetrofitInstance;
import in.ninos.utils.PreferenceUtil;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChallengeActivity extends BaseActivity implements View.OnClickListener, OnLoadMoreListener, EasyPermissions.PermissionCallbacks {


    public static final String CHALLENGE_ID = "CHALLENGE_ID";
    public static final String CHALLENGE_TITLE = "CHALLENGE_TITLE";
    private final int RC_STORAGE_PERM = 4531;
    private RetrofitService service;
    private int placeHolderId;
    private ImageView iv_challenge;
    private AllChallengeAdapter allChallengeAdapter;
    private String accessToken, challengeId, challengeName;
    private int from = 0, size = 10;
    private SwipeRefreshLayout sr_layout;
    private ImageView iv_move_up;
    private NestedScrollView ns_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_challenge);

            accessToken = PreferenceUtil.getAccessToken(this);
            placeHolderId = R.drawable.pattern_5;
            challengeId = getIntent().getStringExtra(CHALLENGE_ID);
            challengeName = getIntent().getStringExtra(CHALLENGE_TITLE);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), placeHolderId);
            setBitmapPalette(bitmap);

            final TextView tv_challenge = findViewById(R.id.tv_challenge);
            final TextView tv_description = findViewById(R.id.tv_description);
            iv_challenge = findViewById(R.id.iv_challenge);
            iv_challenge.setImageBitmap(bitmap);

            findViewById(R.id.fab_back).setOnClickListener(this);
            findViewById(R.id.fab_challenge).setOnClickListener(this);

            LinearLayoutManager challengeLayoutManager = new LinearLayoutManager(this);

            RecyclerView challenge_list = findViewById(R.id.challenge_list);
            challenge_list.setNestedScrollingEnabled(false);
            challenge_list.setLayoutManager(challengeLayoutManager);

            allChallengeAdapter = new AllChallengeAdapter(this, this, challenge_list, this, AllChallengeAdapter.Type.CHALLENGE);

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

            sr_layout = findViewById(R.id.sr_layout);
            sr_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (isNetworkAvailable()) {
                        from = 0;
                        allChallengeAdapter.resetItems();

                        getPosts();

                        sr_layout.setRefreshing(false);
                    } else {
                        showNetworkDown();
                    }
                }
            });

            iv_move_up = findViewById(R.id.iv_move_up);
            iv_move_up.setOnClickListener(this);
            iv_move_up.setVisibility(View.GONE);

            ns_view = findViewById(R.id.ns_view);
            ns_view.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > oldScrollY) {
                        iv_move_up.setVisibility(View.VISIBLE);
                    } else {
                        iv_move_up.setVisibility(View.GONE);
                    }

                    JZVideoPlayer.releaseAllVideos();
                }
            });

            getPosts();
        }  catch (Exception e) {
            logError(e);
        }
    }

    private void updateImage(String url) {
        try {
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
        }  catch (Exception e) {
            logError(e);
        }
    }

    private void setBitmapPalette(Bitmap resource) {
        try {
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
        } catch (Exception e) {
            logError(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).setRationale(R.string.rationale_storage_ask_again).build().show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_back:
                super.onBackPressed();
                break;
            case R.id.fab_challenge:
                addFile();
                break;
            case R.id.iv_move_up:
                iv_move_up.setVisibility(View.GONE);
                ns_view.post(new Runnable() {
                    @Override
                    public void run() {
                        ns_view.scrollTo(0, 0);
                        ns_view.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_STORAGE_PERM:
                addFile();
                break;
            case MainActivity.POST_ADDED:
                if (data != null) {
                    String postId = data.getStringExtra(FilePickerActivity.POST_ID);

                    if (allChallengeAdapter != null && postId != null) {
                        service.getPost(postId, accessToken).enqueue(new Callback<PostResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<PostResponse> call, @NonNull Response<PostResponse> response) {
                                if (response.body() != null && response.isSuccessful() && allChallengeAdapter != null) {
                                    PostInfo postInfo = response.body().getPostInfo();
                                    allChallengeAdapter.addItem(postInfo, 0);
                                }
                            }

                            @Override
                            public void onFailure(Call<PostResponse> call, Throwable t) {

                            }
                        });
                    }
                }
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

    @AfterPermissionGranted(RC_STORAGE_PERM)
    private void addFile() {
        try {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Intent intent = new Intent(this, FilePickerActivity.class);
                intent.putExtra(CHALLENGE_ID, challengeId);
                intent.putExtra(CHALLENGE_TITLE, challengeName);
                startActivityForResult(intent, MainActivity.POST_ADDED);
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.rationale_storage), RC_STORAGE_PERM, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }  catch (Exception e) {
            logError(e);
        }
    }

    private void getPosts() {
        try {
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
        }  catch (Exception e) {
            logError(e);
        }
    }
}
