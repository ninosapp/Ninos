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

import com.ninos.R;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.Response;
import com.ninos.models.UserInfo;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.PreferenceUtil;

import retrofit2.Call;
import retrofit2.Callback;

public class EditNameActivity extends BaseActivity implements View.OnClickListener {

    final public static String CHILD_NAME = "CHILD_NAME";
    final public static String USER_ID = "USER_ID";
    private EditText et_child_name;
    private String mChildName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);

        Toolbar toolbar_edit_name = findViewById(R.id.toolbar_edit_name);
        toolbar_edit_name.setTitle(R.string.edit_name);
        toolbar_edit_name.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar_edit_name);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back_white));
        }

        findViewById(R.id.tv_cancel).setOnClickListener(this);
        findViewById(R.id.tv_ok).setOnClickListener(this);

        mChildName = getIntent().getStringExtra(CHILD_NAME);
        userId = getIntent().getStringExtra(USER_ID);

        et_child_name = findViewById(R.id.et_child_name);
        et_child_name.setText(mChildName);
        et_child_name.setSelection(et_child_name.getText().length());
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                onBackPressed();
                break;
            case R.id.tv_ok:
                String childName = et_child_name.getText().toString().trim();

                if (mChildName.equals(childName)) {
                    finish();
                } else {
                    if (childName.isEmpty()) {
                        showToast(R.string.enter_child_name);
                    } else {
                        UserInfo userInfo = new UserInfo();
                        userInfo.setChildName(childName);

                        RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
                        service.updateProfile(userId, userInfo, PreferenceUtil.getAccessToken(this)).enqueue(new Callback<Response>() {
                            @Override
                            public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
                                if (response.body() != null && response.body().isSuccess()) {
                                    Intent intent = new Intent();
                                    setResult(ProfileActivity.NAME_UPDATED, intent);
                                    finish();
                                } else {
                                    showToast(R.string.error_message);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Response> call, @NonNull Throwable t) {
                                showToast(R.string.error_message);
                            }
                        });
                    }
                }

                break;
        }
    }
}
