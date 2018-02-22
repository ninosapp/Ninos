package com.ninos.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends BaseActivity implements DateSetListener, View.OnClickListener, EasyPermissions.PermissionCallbacks {

    public static final String EMAIL = "EMAIL";
    public static final String P_NAME = "P_NAME";
    public static final String USER_ID = "USER_ID";
    private static final int RC_STORAGE_PERM = 4523;
    private TextView tv_dob;
    private EditText et_child_name;
    private View cl_edit_profile;
    private Profile profile;
    private DateUtil dateUtil;
    private ImageView iv_upload_image, iv_placeholder;
    private AppCompatCheckBox cb_agree;

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

        cb_agree = findViewById(R.id.cb_agree);
        et_child_name = findViewById(R.id.et_child_name);
        tv_dob = findViewById(R.id.tv_dob);
        cl_edit_profile = findViewById(R.id.cl_edit_profile);
        iv_placeholder = findViewById(R.id.iv_placeholder);
        iv_upload_image = findViewById(R.id.iv_upload_image);
        iv_upload_image.setOnClickListener(this);

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
                boolean isAgreed = cb_agree.isChecked();

                if (isAgreed) {
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
                                public void onResponse(@NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {
                                    if (response.isSuccessful()) {
                                        RegisterResponse rR = response.body();

                                        if (rR != null) {
                                            PreferenceUtil.setUserInfo(EditProfileActivity.this, rR.getUserInfo());
                                            PreferenceUtil.setUserName(EditProfileActivity.this, rR.getUserInfo().getChildName());
                                            PreferenceUtil.setUserEmail(EditProfileActivity.this, rR.getUserInfo().getEmail());
                                            PreferenceUtil.setAccessToken(EditProfileActivity.this, rR.getToken());
                                            startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    } else {
                                        showSnackBar(R.string.error_message, cl_edit_profile);
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<RegisterResponse> call, @NonNull Throwable t) {
                                    CrashUtil.report(t.getMessage());
                                }
                            });
                        }
                    }
                } else {
                    showToast(R.string.accept_terms_conditons);
                }
                break;
            case R.id.iv_upload_image:
                addFile();
                break;
        }
    }

    @AfterPermissionGranted(RC_STORAGE_PERM)
    private void addFile() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(this, ProfileSelectActivity.class);
            startActivityForResult(intent, ProfileActivity.IMAGE_UPDATED);
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
            case ProfileActivity.IMAGE_UPDATED:
                if (data != null) {
                    iv_placeholder.setVisibility(View.GONE);
                    String path = data.getStringExtra(ProfileActivity.PROFILE_PATH);

                    RequestOptions requestOptions = new RequestOptions()
                            .placeholder(R.drawable.ic_circle)
                            .error(R.drawable.ic_circle)
                            .circleCrop()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true);

                    Glide.with(this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(path)
                            .into(iv_upload_image);
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
