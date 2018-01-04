package com.ninos.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ninos.R;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.ProfileResponse;
import com.ninos.models.UserProfile;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.AWSUrls;
import com.ninos.utils.PreferenceUtil;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    public static final String PROFILE_PLACE_HOLDER = "PROFILE_PLACE_HOLDER";
    public static final String PROFILE_ID = "PROFILE_ID";
    private static final int RC_STORAGE_PERM = 4523;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final int placeHolderId = getIntent().getIntExtra(PROFILE_PLACE_HOLDER, 0);
        final String userId = getIntent().getStringExtra(PROFILE_ID);

        final TextView tv_name = findViewById(R.id.tv_name);
        final TextView tv_post_count = findViewById(R.id.tv_post_count);
        final TextView tv_follower_count = findViewById(R.id.tv_follower_count);
        final TextView tv_following = findViewById(R.id.tv_following);
        final ImageView iv_profile = findViewById(R.id.iv_profile);
        iv_profile.setImageDrawable(ContextCompat.getDrawable(this, placeHolderId));
        iv_profile.setOnClickListener(this);

        String token = PreferenceUtil.getAccessToken(this);

        RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
        service.getUserProfile(token, userId).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile userProfile = response.body().getUserProfile();

                    if (userProfile != null) {
                        tv_post_count.setText(userProfile.getPostCount());
                        tv_follower_count.setText(userProfile.getFollowersCount());
                        tv_following.setText(userProfile.getFollowingCount());
                        tv_name.setText(userProfile.getChildName());

                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(placeHolderId);
                        requestOptions.error(placeHolderId);
                        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
                        requestOptions.skipMemoryCache(true);

                        Glide.with(ProfileActivity.this)
                                .setDefaultRequestOptions(requestOptions)
                                .load(AWSUrls.GetPI512(ProfileActivity.this, userId)).into(iv_profile);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public void onClick(View view) {
        addFile();
    }

    @AfterPermissionGranted(RC_STORAGE_PERM)
    private void addFile() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(this, ProfileSelectActivity.class);
            startActivity(intent);
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
        }
    }
}
