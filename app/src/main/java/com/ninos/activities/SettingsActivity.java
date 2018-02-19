package com.ninos.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.ninos.R;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.Profile;
import com.ninos.models.ProfileResponse;
import com.ninos.models.UserInfo;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.DateUtil;
import com.ninos.utils.PreferenceUtil;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ninos.activities.ProfileActivity.IS_PROFILE_UPDATED;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_name, et_school, et_state, et_city, et_about;
    private TextView tv_dob, tv_email, tv_gender;
    private RetrofitService service;
    private Profile profile;
    private String childNameOld;
    private boolean isProfileUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar_settings = findViewById(R.id.toolbar_settings);
        toolbar_settings.setTitle(R.string.settings);
        toolbar_settings.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar_settings);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back_white));
        }

        et_name = findViewById(R.id.et_name);
        et_school = findViewById(R.id.et_school);
        et_state = findViewById(R.id.et_state);
        et_city = findViewById(R.id.et_city);
        et_about = findViewById(R.id.et_about);

        tv_gender = findViewById(R.id.tv_gender);
        tv_dob = findViewById(R.id.tv_dob);
        tv_email = findViewById(R.id.tv_email);
        findViewById(R.id.iv_info).setOnClickListener(this);

        tv_gender.setOnClickListener(this);
        findViewById(R.id.tv_save).setOnClickListener(this);

        service = RetrofitInstance.createService(RetrofitService.class);
        service.getProfile(PreferenceUtil.getAccessToken(this)).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    profile = response.body().getUserDetails();

                    if (profile != null) {
                        et_name.setText(profile.getChildName());
                        et_school.setText(profile.getSchool());
                        et_state.setText(profile.getState());
                        et_city.setText(profile.getCity());
                        et_about.setText(profile.getAboutus());

                        tv_gender.setText(profile.getGender());

                        DateUtil dateUtil = new DateUtil();
                        tv_dob.setText(dateUtil.formatDateToString(new Date(profile.getDOB()), DateUtil.FULL_DATE));
                        tv_email.setText(profile.getEmail());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                showToast(R.string.error_message);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_gender:
                PopupMenu popupMenu = new PopupMenu(this, view);
                popupMenu.getMenu().add(0, 1, 0, R.string.male);
                popupMenu.getMenu().add(0, 2, 0, R.string.female);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case 1:
                                tv_gender.setText(R.string.male);
                                profile.setGender(getString(R.string.male));
                                return true;
                            case 2:
                                tv_gender.setText(R.string.female);
                                profile.setGender(getString(R.string.female));
                                return true;
                        }

                        return false;
                    }
                });
                popupMenu.show();
                break;
            case R.id.tv_save:
                final String childName = et_name.getText().toString().trim();
                String gender = tv_gender.getText().toString();
                String school = et_school.getText().toString().trim();
                String state = et_state.getText().toString().trim();
                String city = et_city.getText().toString().trim();
                String about = et_about.getText().toString().trim();

                if (childName.isEmpty()) {
                    showToast(R.string.enter_child_name);
                } else {
                    childNameOld = profile.getChildName();

                    profile.setChildName(childName);
                    profile.setGender(gender);
                    profile.setSchool(school);
                    profile.setState(state);
                    profile.setCity(city);
                    profile.setAboutus(about);

                    service.updateProfile(profile, PreferenceUtil.getAccessToken(this)).enqueue(new Callback<com.ninos.models.Response>() {
                        @Override
                        public void onResponse(@NonNull Call<com.ninos.models.Response> call, @NonNull Response<com.ninos.models.Response> response) {
                            if (response.isSuccessful()) {
                                if (!childNameOld.equals(childName)) {
                                    UserInfo userInfo = PreferenceUtil.getUserInfo(SettingsActivity.this);
                                    userInfo.setChildName(childName);
                                    PreferenceUtil.setUserInfo(SettingsActivity.this, userInfo);
                                    PreferenceUtil.setUserName(SettingsActivity.this, childName);
                                    isProfileUpdated = true;
                                }

                                showToast(R.string.profile_updated_successfully);
                                onBackPressed();
                            } else {
                                showToast(R.string.error_update_profile);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<com.ninos.models.Response> call, @NonNull Throwable t) {
                            showToast(R.string.error_update_profile);
                        }
                    });
                }
                break;
            case R.id.iv_info:
                showToast(R.string.mail_us_dob_edit);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(IS_PROFILE_UPDATED, isProfileUpdated);
        setResult(MainActivity.PROFILE_UPDATED, intent);
        finish();
    }
}
