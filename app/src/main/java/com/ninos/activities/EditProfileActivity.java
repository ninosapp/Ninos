package com.ninos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.ninos.BaseActivity;
import com.ninos.R;
import com.ninos.listeners.DateSetListener;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.Profile;
import com.ninos.models.RegisterResponse;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.CrashUtil;
import com.ninos.utils.DateUtil;
import com.ninos.utils.PreferenceUtil;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends BaseActivity implements DateSetListener, View.OnClickListener {

    public static final String EMAIL = "EMAIL";
    public static final String P_NAME = "P_NAME";
    public static final String USER_ID = "USER_ID";
    private TextView tv_dob;
    private EditText et_child_name;
    private View cl_edit_profile;
    private Profile profile;
    private DateUtil dateUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar_edit_profile);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        }

        et_child_name = findViewById(R.id.et_child_name);
        tv_dob = findViewById(R.id.tv_dob);
        cl_edit_profile = findViewById(R.id.cl_edit_profile);

        tv_dob.setOnClickListener(this);
        findViewById(R.id.fab_update).setOnClickListener(this);

        Intent intent = getIntent();
        String email = intent.getStringExtra(EMAIL);
        String pName = intent.getStringExtra(P_NAME);
        String userId = intent.getStringExtra(USER_ID);

        profile = new Profile();
        profile.setParentName(pName);
        profile.setEmail(email);
        profile.setUserId(userId);

        dateUtil = new DateUtil();
    }

    @Override
    public void onDateSet(String date) {
        tv_dob.setText(date);
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
            case R.id.tv_dob:
                dateUtil.datePicker(this, this, null);
                break;
            case R.id.fab_update:
                String childName = et_child_name.getText().toString().trim();
                String dob = tv_dob.getText().toString().trim();

                if (childName.isEmpty()) {
                    showSnackBar(R.string.enter_child_name, cl_edit_profile);
                } else if (dob.isEmpty()) {
                    showSnackBar(R.string.select_dob, cl_edit_profile);
                } else {
                    if (!isNetworkAvailable()) {
                        showSnackBar(R.string.network_down, cl_edit_profile);
                    } else {
                        Date date = dateUtil.formatStringToDate(dob, DateUtil.FULL_DATE);

                        profile.setDOB(date.getTime());
                        profile.setChildName(childName);
                        profile.setFirstLogin(true);

                        RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
                        service.registerChild(profile).enqueue(new Callback<RegisterResponse>() {
                            @Override
                            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                                if (response.isSuccessful()) {
                                    RegisterResponse rR = response.body();

                                    if (rR != null) {
                                        PreferenceUtil.setAccessToken(EditProfileActivity.this, rR.getToken());
                                        startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
                                        finish();
                                    }
                                } else {
                                    showSnackBar(R.string.error_message, cl_edit_profile);
                                }
                            }

                            @Override
                            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                                CrashUtil.report(t.getMessage());
                            }
                        });
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        super.onBackPressed();
    }
}
