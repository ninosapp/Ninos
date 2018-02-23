package com.ninos.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.ninos.adapters.NotificationAdapter;
import com.ninos.firebase.Database;
import com.ninos.fragments.AllChallengesFragment;
import com.ninos.fragments.ChallengesFragment;
import com.ninos.fragments.QuizFragment;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.Notification;
import com.ninos.models.NotificationCount;
import com.ninos.models.NotificationResponse;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.AWSUrls;
import com.ninos.utils.PreferenceUtil;
import com.ninos.utils.TourUtil;
import com.ninos.views.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZVideoPlayer;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks, NavigationView.OnNavigationItemSelectedListener {

    public static final int POST_ADDED = 8525;
    public static final int COMMENT_ADDED = 8526;
    public static final int PROFILE_UPDATED = 8527;
    public static final int QUIZ_COMPLETE = 8528;
    public static final int POST_EDIT = 8529;
    public static final int POST_UPDATE = 3651;
    private final int RC_STORAGE_PERM = 4531;
    private Fragment challengeFragment;
    private AllChallengesFragment allChallengeFragment;
    private boolean doubleBackToExit;
    private CircleImageView iv_profile;
    private DrawerLayout drawer_layout;
    private RelativeLayout rl_no_network;
    private TextView tv_user_name;
    private TextView tv_user_email;
    private CircleImageView iv_nav_image;
    private ImageView iv_challenge_dot, iv_home_dot;
    private NavigationView nav_notification;
    private ImageView iv_notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.fl_home).setOnClickListener(this);
        findViewById(R.id.fl_add).setOnClickListener(this);
        findViewById(R.id.fl_challenges).setOnClickListener(this);

        iv_challenge_dot = findViewById(R.id.iv_challenge_dot);
        iv_home_dot = findViewById(R.id.iv_home_dot);
        iv_profile = findViewById(R.id.iv_profile);
        iv_home_dot.setVisibility(View.VISIBLE);
        iv_challenge_dot.setVisibility(View.GONE);
        iv_profile.setOnClickListener(this);
        rl_no_network = findViewById(R.id.rl_no_network);
        rl_no_network.setVisibility(View.GONE);
        TextView tv_try_again = findViewById(R.id.tv_try_again);
        tv_try_again.setOnClickListener(this);
        findViewById(R.id.iv_notification).setOnClickListener(this);

        ImageView iv_search = findViewById(R.id.iv_search);
        iv_search.setOnClickListener(this);

        new TourUtil(this).showHomePrompt();

        drawer_layout = findViewById(R.id.drawer_layout);
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

        nav_notification = findViewById(R.id.nav_notification);

        iv_notifications = findViewById(R.id.iv_notifications);
        iv_notifications.setVisibility(View.GONE);

        getNotifications();
    }

    public void getNotifications() {
        RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
        service.getNotificationsCount(PreferenceUtil.getAccessToken(this)).enqueue(new Callback<NotificationCount>() {
            @Override
            public void onResponse(Call<NotificationCount> call, Response<NotificationCount> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getNotificationsCount() > 0) {
                        iv_notifications.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationCount> call, Throwable t) {

            }
        });
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
                .load(AWSUrls.GetPI192(this, Database.getUserId()))
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
                iv_notifications.setVisibility(View.GONE);
                drawer_layout.openDrawer(Gravity.END);

                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                View navView = nav_notification.getHeaderView(0);

                final RecyclerView notification_list = navView.findViewById(R.id.notification_list);
                notification_list.setNestedScrollingEnabled(false);
                notification_list.setLayoutManager(layoutManager);

                final NotificationAdapter notificationAdapter = new NotificationAdapter(this, this);

                final RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
                service.getNotifications(PreferenceUtil.getAccessToken(this)).enqueue(new Callback<NotificationResponse>() {
                    @Override
                    public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            notificationAdapter.addItems(response.body().getNotifications());
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationResponse> call, Throwable t) {
                        showToast(R.string.error_message);

                        closeDrawer();
                    }
                });

                notification_list.setAdapter(notificationAdapter);

                navView.findViewById(R.id.iv_clear).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        service.markAllNotificationsAsRead(PreferenceUtil.getAccessToken(MainActivity.this)).enqueue(new Callback<com.ninos.models.Response>() {
                            @Override
                            public void onResponse(Call<com.ninos.models.Response> call, Response<com.ninos.models.Response> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    for (int i = 0; i < notificationAdapter.getItemCount(); i++) {
                                        Notification notification = notificationAdapter.getItem(i);
                                        notification.setRead(true);
                                        notificationAdapter.updateItem(i, notification);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<com.ninos.models.Response> call, Throwable t) {

                            }
                        });
                    }
                });

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
        } else if (drawer_layout.isDrawerOpen(Gravity.END)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawer_layout.closeDrawer(Gravity.END);
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

        iv_home_dot.setVisibility(View.VISIBLE);
        iv_challenge_dot.setVisibility(View.GONE);
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

        iv_home_dot.setVisibility(View.GONE);
        iv_challenge_dot.setVisibility(View.VISIBLE);
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
            case POST_UPDATE:
                if (data != null) {
                    String postId = data.getStringExtra(FilePickerActivity.POST_ID);

                    if (allChallengeFragment != null && postId != null) {
                        allChallengeFragment.newClapAdded(postId);
                        allChallengeFragment.newCommentAdded(postId);
                    }
                }
                break;
            case COMMENT_ADDED:
                if (data != null) {
                    String postId = data.getStringExtra(FilePickerActivity.POST_ID);

                    if (allChallengeFragment != null && postId != null) {
                        allChallengeFragment.newCommentAdded(postId);
                    }
                }

                break;
            case POST_EDIT:
                if (data != null) {
                    String postId = data.getStringExtra(FilePickerActivity.POST_ID);
                    String desc = data.getStringExtra(EditPostActivity.DESCRIPTION);
                    ArrayList<String> links = data.getStringArrayListExtra(EditPostActivity.LINKS);

                    if (links == null) {
                        if (postId != null) {
                            allChallengeFragment.postDeleted(postId);
                        }
                    } else if (allChallengeFragment != null && postId != null) {
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
                Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_store_link)));
                startActivity(playStoreIntent);

                closeDrawer();

                return true;
            case R.id.nav_invite:
                try {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.invite_text));
                    shareIntent.setType("text/*");
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.invite_friends)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                closeDrawer();

                return true;

            case R.id.nav_feed_back:

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "info@ninosapp.in", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, " Feed back from " + PreferenceUtil.getUserName(this));
                startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email_using)));
                closeDrawer();

                return true;

            case R.id.nav_aboutus:
                startActivity(new Intent(this, AboutActivity.class));

                closeDrawer();
                return true;
        }

        return false;
    }

    public void showNoNetwork() {
        rl_no_network.setVisibility(View.VISIBLE);
    }
}
