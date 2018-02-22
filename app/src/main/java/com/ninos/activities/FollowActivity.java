package com.ninos.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ninos.R;
import com.ninos.adapters.FollowAdapter;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.Follow;
import com.ninos.models.FollowingResponse;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.PreferenceUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ninos.activities.MainActivity.PROFILE_UPDATED;

public class FollowActivity extends BaseActivity {

    public static final String TYPE = "TYPE";
    public static final String FOLLOWING = "FOLLOWING";
    public static final String FOLLOWERS = "FOLLOWERS";
    private FollowAdapter followAdapter;
    private RetrofitService service;
    private String accessToken;
    private String type;
    private TextView tv_empty;
    private RecyclerView recyclerView;
    private boolean isFollowersUpdated;
    private boolean isFollowingUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        type = getIntent().getStringExtra(TYPE);

        Toolbar toolbar_about = findViewById(R.id.toolbar_follow);

        if (type.equals(FOLLOWERS)) {
            toolbar_about.setTitle(R.string.followers);
        } else {
            toolbar_about.setTitle(R.string.following);
        }

        toolbar_about.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar_about);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back_white));
        }

        accessToken = PreferenceUtil.getAccessToken(this);
        service = RetrofitInstance.createService(RetrofitService.class);

        tv_empty = findViewById(R.id.tv_empty);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        recyclerView = findViewById(R.id.people_list);
        recyclerView.setLayoutManager(layoutManager);

        followAdapter = new FollowAdapter(this, this, type);

        recyclerView.setAdapter(followAdapter);

        getUsers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getUsers() {
        if (type.equals(FOLLOWERS)) {
            tv_empty.setText(R.string.empty_following);

            service.getFollowers(accessToken).enqueue(new Callback<FollowingResponse>() {
                @Override
                public void onResponse(Call<FollowingResponse> call, Response<FollowingResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        followAdapter.removeItem(null);

                        if (response.body().getFollowingList().size() > 0) {

                            tv_empty.setVisibility(View.GONE);

                            for (final Follow userInfo : response.body().getFollowingList()) {
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        followAdapter.addItem(userInfo);
                                    }
                                });
                            }
                        } else {
                            tv_empty.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<FollowingResponse> call, Throwable t) {

                }
            });
        } else {
            tv_empty.setText(R.string.empty_follower);

            service.getFollowing(accessToken).enqueue(new Callback<FollowingResponse>() {
                @Override
                public void onResponse(Call<FollowingResponse> call, Response<FollowingResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        followAdapter.removeItem(null);

                        if (response.body().getFollowingList().size() > 0) {
                            tv_empty.setVisibility(View.GONE);

                            for (final Follow userInfo : response.body().getFollowingList()) {
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        followAdapter.addItem(userInfo);
                                    }
                                });
                            }
                        } else {
                            tv_empty.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<FollowingResponse> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(ProfileActivity.IS_FOLLOWERS_UPDATED, isFollowersUpdated);
        intent.putExtra(ProfileActivity.IS_FOLLOWING_UPDATED, isFollowingUpdated);
        intent.putExtra(ProfileActivity.IS_FOLLOW_COUNT, String.valueOf(followAdapter.getItemCount()));
        setResult(ProfileActivity.IS_FOLLOW_UPDATED, intent);
        finish();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PROFILE_UPDATED:
                if (data != null) {
                    boolean isProfileUpdated = data.getBooleanExtra(ProfileActivity.IS_PROFILE_UPDATED, false);

                    if (isProfileUpdated) {
                        followAdapter.resetItems();
                        followAdapter.addItem(null);
                        getUsers();

                        if (type.equals(FOLLOWERS)) {
                            isFollowersUpdated = true;
                        } else {
                            isFollowingUpdated = true;
                        }
                    }
                }
                break;
        }
    }
}
