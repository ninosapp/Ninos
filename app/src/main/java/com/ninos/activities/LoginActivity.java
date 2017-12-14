package com.ninos.activities;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ninos.BaseActivity;
import com.ninos.R;
import com.ninos.adapters.IntroAdapter;
import com.ninos.adapters.IntroPageTransformer;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    public static final int RC_SIGN_IN = 13596;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private View progress_login;
    private AppCompatButton btn_gamil_login, btn_fb_login;
    private View cl_login;
    private List<Integer> mColors;
    private ViewPager view_pager;
    private ArgbEvaluator mARGBEvaluator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_login);

            cl_login = findViewById(R.id.cl_login);
            progress_login = findViewById(R.id.progress_login);

            btn_gamil_login = findViewById(R.id.btn_gmail_login);
            btn_gamil_login.setOnClickListener(this);

            btn_fb_login = findViewById(R.id.btn_fb_login);
            btn_fb_login.setOnClickListener(this);

            view_pager = findViewById(R.id.view_pager);
            view_pager.setAdapter(new IntroAdapter(getSupportFragmentManager()));
            view_pager.setPageTransformer(false, new IntroPageTransformer());
            view_pager.addOnPageChangeListener(this);

            TabLayout tabLayout = findViewById(R.id.tab_layout);
            tabLayout.setupWithViewPager(view_pager, true);

            mColors = new ArrayList<>();
            mColors.add(ContextCompat.getColor(this, R.color.teal));
            mColors.add(ContextCompat.getColor(this, R.color.colorAccent));
            mColors.add(ContextCompat.getColor(this, R.color.grape));
            mColors.add(ContextCompat.getColor(this, R.color.green));

            setStatusColorByIndex(0);
            mARGBEvaluator = new ArgbEvaluator();

        } catch (Exception e) {
            logError(e);
            showSnackBar(R.string.error_message, cl_login);
        }
    }

    private void googleSignIn() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    public void enableButton() {
        btn_gamil_login.setClickable(true);
        btn_gamil_login.setVisibility(View.VISIBLE);
        btn_fb_login.setClickable(true);
        btn_fb_login.setVisibility(View.VISIBLE);
        progress_login.setVisibility(View.GONE);
    }

    private void disableButton() {
        btn_gamil_login.setClickable(false);
        btn_gamil_login.setVisibility(View.GONE);
        btn_fb_login.setClickable(false);
        btn_fb_login.setVisibility(View.GONE);
        progress_login.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        if (isNetworkAvailable()) {
            switch (view.getId()) {
                case R.id.btn_gmail_login:
                    launchHome();
                    break;
                case R.id.btn_fb_login:
                    launchHome();
                    break;
            }
        } else {
            showNetworkDownSnackBar(cl_login);
        }
    }

    private void launchHome() {
        startActivity(new Intent(this, EditProfileActivity.class));
        finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position < (mColors.size() - 1)) {
            int color = (Integer) mARGBEvaluator.evaluate(positionOffset, mColors.get(position), mColors.get(position + 1));
            setStatusColor(color);
        } else {
            setStatusColorByIndex(position);
        }
    }

    @Override
    public void onPageSelected(int position) {
        setStatusColorByIndex(position);
    }

    private void setStatusColorByIndex(int index) {
        setStatusColor(mColors.get(index));
    }

    private void setStatusColor(int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(color);
        }

        view_pager.setBackgroundColor(color);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
