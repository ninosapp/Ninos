package com.ninos.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ninos.R;
import com.ninos.adapters.ChallengeImageAdapter;
import com.ninos.firebase.Database;
import com.ninos.listeners.OnLoadMoreListener;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.PostInfo;
import com.ninos.models.PostsResponse;
import com.ninos.models.UserProfile;
import com.ninos.models.UserProfileResponse;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.AWSClient;
import com.ninos.utils.AWSUrls;
import com.ninos.utils.PreferenceUtil;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks, OnLoadMoreListener {

    public static final String PROFILE_PLACE_HOLDER = "PROFILE_PLACE_HOLDER";
    public static final String PROFILE_ID = "PROFILE_ID";
    public static final String PROFILE_PATH = "PROFILE_PATH";
    public static final String IS_PROFILE_UPDATED = "IS_PROFILE_UPDATED";
    public static final int IMAGE_UPDATED = 5468;
    private static final int RC_STORAGE_PERM = 4523;
    private int placeHolderId;
    private String userId;
    private ImageView iv_profile;
    private RelativeLayout rl_progress;
    private FloatingActionButton fab_update_Image;
    private TextView tv_name;
    private boolean isProfileUpdated;
    private ChallengeImageAdapter allChallengeAdapter;
    private RetrofitService service;
    private String accessToken;
    private int from = 0, size = 10;
    private UserProfile userProfile;
    private Button btn_follow;
    private AWSClient awsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        awsClient = new AWSClient(this);
        awsClient.awsInit();

        placeHolderId = getIntent().getIntExtra(PROFILE_PLACE_HOLDER, R.drawable.pattern_2);
        userId = getIntent().getStringExtra(PROFILE_ID);

        tv_name = findViewById(R.id.tv_name);
        final TextView tv_post_count = findViewById(R.id.tv_post_count);
        final TextView tv_follower_count = findViewById(R.id.tv_follower_count);
        final TextView tv_following = findViewById(R.id.tv_following);
        final TextView tv_points = findViewById(R.id.tv_points);
        btn_follow = findViewById(R.id.btn_follow);
        btn_follow.setOnClickListener(this);
        fab_update_Image = findViewById(R.id.fab_update_Image);
        iv_profile = findViewById(R.id.iv_profile);
        rl_progress = findViewById(R.id.rl_progress);
        rl_progress.setVisibility(View.VISIBLE);
        findViewById(R.id.fab_back).setOnClickListener(this);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), placeHolderId);
        iv_profile.setImageBitmap(bm);

        if (Database.getUserId().equals(userId)) {
            fab_update_Image.setOnClickListener(this);
            fab_update_Image.setVisibility(View.VISIBLE);
            btn_follow.setVisibility(View.GONE);
        } else {
            fab_update_Image.setVisibility(View.GONE);
            fab_update_Image.setOnClickListener(null);
            btn_follow.setVisibility(View.VISIBLE);
        }

        GridLayoutManager challengeLayoutManager = new GridLayoutManager(this, 3);

        RecyclerView challenge_list = findViewById(R.id.challenge_list);
        challenge_list.setNestedScrollingEnabled(false);
        challenge_list.setLayoutManager(challengeLayoutManager);

        allChallengeAdapter = new ChallengeImageAdapter(this, challenge_list, this);

        challenge_list.setAdapter(allChallengeAdapter);

        setBitmapPalette(bm);

        accessToken = PreferenceUtil.getAccessToken(this);

        service = RetrofitInstance.createService(RetrofitService.class);
        service.getUserProfile(userId, accessToken).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileResponse> call, @NonNull Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userProfile = response.body().getUserProfile();

                    if (userProfile != null) {
                        tv_points.setText(userProfile.getPointsCount());
                        tv_post_count.setText(userProfile.getPostCount());
                        tv_follower_count.setText(userProfile.getFollowersCount());
                        tv_following.setText(userProfile.getFollowingCount());
                        tv_name.setText(userProfile.getChildName());

                        updateImage();

                        setFollow(userProfile.isFollowing());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {

            }
        });

        getPosts();

    }

    private void updateImage() {
        RequestOptions requestOptions;

        if (Database.getUserId().equals(userId)) {
            requestOptions = new RequestOptions()
                    .placeholder(placeHolderId)
                    .error(placeHolderId)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);
        } else {
            requestOptions = new RequestOptions()
                    .placeholder(placeHolderId)
                    .error(placeHolderId)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        }

        Glide.with(getApplicationContext())
                .setDefaultRequestOptions(requestOptions)
                .asBitmap()
                .load(AWSUrls.GetPI256(ProfileActivity.this, userId))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        iv_profile.setImageBitmap(resource);
                        rl_progress.setVisibility(View.GONE);
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
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(IS_PROFILE_UPDATED, isProfileUpdated);
        setResult(MainActivity.PROFILE_UPDATED, intent);
        finish();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_back:
                onBackPressed();
                break;
            case R.id.btn_follow:

                if (userProfile.isFollowing()) {
                    service.unFollow(userProfile.getUserId(), accessToken).enqueue(new Callback<com.ninos.models.Response>() {
                        @Override
                        public void onResponse(Call<com.ninos.models.Response> call, Response<com.ninos.models.Response> response) {
                            if (response.body() != null && response.isSuccessful()) {
                                setFollow(false);
                                isProfileUpdated = true;
                            }
                        }

                        @Override
                        public void onFailure(Call<com.ninos.models.Response> call, Throwable t) {

                        }
                    });
                } else {
                    service.follow(userProfile.getUserId(), accessToken).enqueue(new Callback<com.ninos.models.Response>() {
                        @Override
                        public void onResponse(Call<com.ninos.models.Response> call, Response<com.ninos.models.Response> response) {
                            if (response.body() != null && response.isSuccessful()) {
                                setFollow(true);
                                isProfileUpdated = true;
                            }
                        }

                        @Override
                        public void onFailure(Call<com.ninos.models.Response> call, Throwable t) {

                        }
                    });
                }

                break;
            default:
                addFile();
                break;
        }
    }

    public void setFollow(boolean isFollowing) {

        if (isFollowing) {
            btn_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_remove_user, 0, 0, 0);
            btn_follow.setText(R.string.unfollow);
        } else {
            btn_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_user, 0, 0, 0);
            btn_follow.setText(R.string.follow);
        }

        userProfile.setFollowing(isFollowing);
    }

    @AfterPermissionGranted(RC_STORAGE_PERM)
    private void addFile() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(this, ProfileSelectActivity.class);
            startActivityForResult(intent, IMAGE_UPDATED);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_storage), RC_STORAGE_PERM, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_STORAGE_PERM:
                addFile();
                break;
            case IMAGE_UPDATED:
                if (data != null) {
                    isProfileUpdated = true;
                    rl_progress.setVisibility(View.GONE);

                    String path = data.getStringExtra(ProfileActivity.PROFILE_PATH);
                    Glide.with(this).load(path).into(iv_profile);

                    File file = new File(path);

                    if (file.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        iv_profile.setImageBitmap(bitmap);
                        setBitmapPalette(bitmap);
                    }
                }
                break;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
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
        service.getUserPosts(from, size, userId, accessToken).enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(@NonNull Call<PostsResponse> call, @NonNull Response<PostsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allChallengeAdapter.removeItem(null);

                    for (final PostInfo postInfo : response.body().getPostInfo()) {
                        String path = String.format("%s/%s", postInfo.getUserId(), postInfo.get_id());
                        new LoadImage().execute(path);
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

    public class LoadImage extends AsyncTask<String, Void, List<String>> {


        @Override
        protected List<String> doInBackground(String... strings) {
            String path = strings[0];

            List<String> links = awsClient.getBucket(path);

            return links;
        }

        @Override
        protected void onPostExecute(List<String> links) {

            for (String link : links) {
                allChallengeAdapter.addItem(link);
            }
        }
    }
}
