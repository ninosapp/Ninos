package com.ninos.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.ninos.R;
import com.ninos.firebase.Database;
import com.ninos.fragments.AllChallengesFragment;
import com.ninos.fragments.ChallengesFragment;
import com.ninos.fragments.QuizFragment;
import com.ninos.utils.AWSUrls;
import com.ninos.utils.PreferenceUtil;
import com.ninos.views.CircleImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZVideoPlayer;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks, NavigationView.OnNavigationItemSelectedListener {

    public static final int POST_ADDED = 8525;
    public static final int COMMENT_ADDED = 8526;
    public static final int PROFILE_UPDATED = 8527;
    public static final int QUIZ_COMPLETE = 8528;
    public static final int POST_UPDATE = 8529;
    private final int RC_STORAGE_PERM = 4531;
    private Fragment challengeFragment;
    private ImageView iv_home, iv_challenges;
    private AllChallengesFragment allChallengeFragment;
    private boolean doubleBackToExit;
    private CircleImageView iv_profile;
    private DrawerLayout drawer_layout;
    private RelativeLayout rl_no_network;
    private TextView tv_try_again;
    private TextView tv_user_name;
    private TextView tv_user_email;
    private CircleImageView iv_nav_image;

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
        rl_no_network = findViewById(R.id.rl_no_network);
        rl_no_network.setVisibility(View.GONE);
        tv_try_again = findViewById(R.id.tv_try_again);
        tv_try_again.setOnClickListener(this);
        findViewById(R.id.iv_notification).setOnClickListener(this);

        ImageView iv_search = findViewById(R.id.iv_search);
        iv_search.setOnClickListener(this);

        drawer_layout = findViewById(R.id.drawer_layout);
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        NavigationView nav_notification = findViewById(R.id.nav_notification);

        tv_user_name = navigationView.getHeaderView(0).findViewById(R.id.tv_user_name);
        tv_user_email = navigationView.getHeaderView(0).findViewById(R.id.tv_user_email);
        iv_nav_image = navigationView.getHeaderView(0).findViewById(R.id.iv_nav_image);
        iv_nav_image.setOnClickListener(this);

        allChallengeFragment = new AllChallengesFragment();
        challengeFragment = new ChallengesFragment();

        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        fts.replace(R.id.frame_layout, allChallengeFragment);
        fts.commit();

        updateProfile();
    }

    private void updateProfile() {
        tv_user_name.setText(PreferenceUtil.getUserName(this));
        tv_user_email.setText(PreferenceUtil.getUserEmail(this));
        iv_nav_image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_account));
        iv_profile.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_account));

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_account)
                .centerCrop()
                .error(R.drawable.ic_account)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .skipMemoryCache(true);

        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(AWSUrls.GetPI128(this, Database.getUserId()))
                .into(iv_nav_image);

        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(AWSUrls.GetPI64(this, Database.getUserId()))
                .into(iv_profile);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.fl_home:
                displayAllChallengeFragment();
                break;
            case R.id.fl_add:
                if (PreferenceUtil.isUserWarned(this)) {
                    addFile();
                } else {
                    final Dialog dialog = new Dialog(this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_warn);

                    dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    dialog.findViewById(R.id.btn_report).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PreferenceUtil.setUserWarn(MainActivity.this);
                            addFile();
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
                break;
            case R.id.fl_challenges:
                displayChallengeFragment();
                break;
            case R.id.iv_profile:
                drawer_layout.openDrawer(Gravity.START);
                break;
            case R.id.iv_nav_image:
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra(ProfileActivity.PROFILE_ID, Database.getUserId());
                startActivityForResult(intent, PROFILE_UPDATED);

                closeDrawer();
                break;
            case R.id.iv_notification:
                drawer_layout.openDrawer(Gravity.END);
                break;
            case R.id.iv_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.tv_try_again:
                if (isNetworkAvailable()) {
                    rl_no_network.setVisibility(View.GONE);

                    if (allChallengeFragment.isAdded()) {
                        allChallengeFragment.refresh();
                    }

                    if (challengeFragment.isAdded()) {

                    }
                }
                break;
        }
    }

    private void closeDrawer() {
        if (drawer_layout.isDrawerOpen(Gravity.START)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawer_layout.closeDrawer(Gravity.START);
                }
            }, 1000);
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
            case POST_UPDATE:
                if (data != null) {
                    String postId = data.getStringExtra(FilePickerActivity.POST_ID);
                    String desc = data.getStringExtra(EditPostActivity.DESCRIPTION);
                    ArrayList<String> links = data.getStringArrayListExtra(EditPostActivity.LINKS);

                    if (allChallengeFragment != null && postId != null) {
                        allChallengeFragment.postUpdated(postId, desc, links);
                    }
                }

                break;
            case PROFILE_UPDATED:
                if (data != null) {
                    boolean isProfileUpdated = data.getBooleanExtra(ProfileActivity.IS_PROFILE_UPDATED, false);

                    if (isProfileUpdated) {
                        updateProfile();
                    }
                }
                break;
            case QUIZ_COMPLETE:
                if (data != null) {
                    String quizId = data.getStringExtra(QuizFragment.QUIZ_ID);

                    if (allChallengeFragment != null && quizId != null) {
                        allChallengeFragment.quizUpdated(quizId);
                    }
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        JZVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        } else if (challengeFragment.isVisible()) {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.logout);
                builder.setMessage(R.string.are_you_sure);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return true;
            case R.id.nav_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), PROFILE_UPDATED);

                closeDrawer();

                return true;

            case R.id.nav_facebook:
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/ninosapp/"));
                startActivity(facebookIntent);

                closeDrawer();

                return true;
            case R.id.nav_instagram:
                Intent instagramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/ninos.app"));
                startActivity(instagramIntent);

                closeDrawer();

                return true;

            case R.id.nav_twitter:
                Intent twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/ninosapp"));
                startActivity(twitterIntent);

                closeDrawer();

                return true;
            case R.id.nav_rate_us:
                Intent playstoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_store_link)));
                startActivity(playstoreIntent);

                closeDrawer();

                return true;
            case R.id.nav_invite:
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo);
                try {
                    File file = new File(this.getExternalCacheDir(), "logicchip.png");
                    FileOutputStream fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    file.setReadable(true, false);

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.invite_text));
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    shareIntent.setType("image/*");
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.invite_friends)));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                closeDrawer();
                break;
            case R.id.nav_feed_back:

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "info@ninosapp.in", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, " Feed back from " + PreferenceUtil.getUserName(this));
                startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email_using)));
                closeDrawer();
                break;
            case R.id.nav_aboutus:
                startActivity(new Intent(this, AboutActivity.class));
        }

        return false;
    }

    public void showNoNetwork() {
        rl_no_network.setVisibility(View.VISIBLE);
    }
}
