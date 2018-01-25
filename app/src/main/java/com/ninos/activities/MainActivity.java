package com.ninos.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.graphics.Palette;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ninos.R;
import com.ninos.firebase.Database;
import com.ninos.fragments.AllChallengesFragment;
import com.ninos.fragments.ChallengesFragment;
import com.ninos.utils.AWSUrls;
import com.ninos.utils.PreferenceUtil;
import com.ninos.views.CircleImageView;

import java.util.List;

import cn.jzvd.JZVideoPlayer;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks, NavigationView.OnNavigationItemSelectedListener {

    public static final int POST_ADDED = 8525;
    public static final int COMMENT_ADDED = 8526;
    private final int RC_STORAGE_PERM = 4531;
    Fragment challengeFragment;
    private ImageView iv_home, iv_challenges;
    private AllChallengesFragment allChallengeFragment;
    private boolean doubleBackToExit;
    private CircleImageView iv_profile;
    private DrawerLayout drawer_layout;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.fl_home).setOnClickListener(this);
        findViewById(R.id.fl_add).setOnClickListener(this);
        findViewById(R.id.fl_challenges).setOnClickListener(this);

        iv_home = findViewById(R.id.iv_home);
        iv_challenges = findViewById(R.id.iv_challenges);
        iv_profile = findViewById(R.id.iv_profile);
        iv_profile.setOnClickListener(this);
        ImageView iv_search = findViewById(R.id.iv_search);
        iv_search.setOnClickListener(this);

        drawer_layout = findViewById(R.id.drawer_layout);
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView tv_user_name = navigationView.getHeaderView(0).findViewById(R.id.tv_user_name);
        TextView tv_user_email = navigationView.getHeaderView(0).findViewById(R.id.tv_user_email);
        final CircleImageView iv_nav_image = navigationView.getHeaderView(0).findViewById(R.id.iv_nav_image);
        iv_nav_image.setOnClickListener(this);

        tv_user_name.setText(PreferenceUtil.getUserName(this));
        tv_user_email.setText(PreferenceUtil.getUserEmail(this));

        allChallengeFragment = new AllChallengesFragment();
        challengeFragment = new ChallengesFragment();

        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        fts.replace(R.id.frame_layout, allChallengeFragment);
        fts.commit();

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_account)
                .error(R.drawable.ic_account)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);

        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(AWSUrls.GetPI128(this, Database.getUserId()))
                .into(iv_nav_image);

        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .asBitmap()
                .load(AWSUrls.GetPI64(this, Database.getUserId()))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        iv_profile.setImageBitmap(resource);

                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                if (palette != null) {
                                    iv_profile.setBorderColor(palette.getDominantColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent)));
                                    iv_nav_image.setBorderColor(palette.getDominantColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent)));
                                }
                            }
                        });
                    }
                });
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.fl_home:
                iv_home.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_home));
//                iv_challenges.setColorFilter(ContextCompat.getColor(this, R.color.grey));

                displayAllChallengeFragment();
                break;
            case R.id.fl_add:
                addFile();
                break;
            case R.id.fl_challenges:
                iv_challenges.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_flash));
//                iv_home.setColorFilter(ContextCompat.getColor(this, R.color.grey));

                displayChallengeFragment();
                break;
            case R.id.iv_profile:
                drawer_layout.openDrawer(Gravity.START);
                break;
            case R.id.iv_nav_image:
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra(ProfileActivity.PROFILE_ID, Database.getUserId());
                startActivity(intent);

                if (drawer_layout.isDrawerOpen(Gravity.START)) {
                    drawer_layout.closeDrawer(Gravity.START);
                }

                break;
            case R.id.iv_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
        }
    }

    @AfterPermissionGranted(RC_STORAGE_PERM)
    private void addFile() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(this, FilePickerActivity.class);
            startActivityForResult(intent, POST_ADDED);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_storage), RC_STORAGE_PERM, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void displayAllChallengeFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (allChallengeFragment.isAdded()) { // if the fragment is already in container
            ft.show(allChallengeFragment);
        } else {
            ft.add(R.id.frame_layout, allChallengeFragment);
        }

        if (challengeFragment.isAdded()) {
            ft.hide(challengeFragment);
        }
        ft.commit();
    }

    private void displayChallengeFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (challengeFragment.isAdded()) {
            ft.show(challengeFragment);
        } else {
            ft.add(R.id.frame_layout, challengeFragment);
        }

        if (allChallengeFragment.isAdded()) {
            ft.hide(allChallengeFragment);
        }
        ft.commit();
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
            case POST_ADDED:
                if (data != null) {
                    String postId = data.getStringExtra(FilePickerActivity.POST_ID);

                    if (allChallengeFragment != null && postId != null) {
                        allChallengeFragment.newPostAdded(postId);
                    }
                }
            case COMMENT_ADDED:
                if (data != null) {
                    String postId = data.getStringExtra(FilePickerActivity.POST_ID);

                    if (allChallengeFragment != null && postId != null) {
                        allChallengeFragment.newCommentAdded(postId);
                    }
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        } else if (challengeFragment.isVisible()) {
            iv_home.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
            iv_challenges.setColorFilter(ContextCompat.getColor(this, R.color.grey));

            displayAllChallengeFragment();
        } else if (!doubleBackToExit) {
            doubleBackToExit = true;
            showToast(R.string.app_exit_msg);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExit = false;
                }
            }, 2000);
        } else {
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_logout:
                logout();
                return true;
            case R.id.nav_settings:
                return true;
        }

        return false;
    }
}
