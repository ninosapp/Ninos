package com.ninos.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.ninos.BaseActivity;
import com.ninos.R;
import com.ninos.fragments.AllChallengesFragment;
import com.ninos.fragments.ChallengesFragment;
import com.ninos.utils.FragmentUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_home, iv_challenges;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar_main = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar_main);

        findViewById(R.id.fl_home).setOnClickListener(this);
        findViewById(R.id.fl_add).setOnClickListener(this);
        findViewById(R.id.fl_challenges).setOnClickListener(this);

        iv_home = findViewById(R.id.iv_home);
        iv_challenges = findViewById(R.id.iv_challenges);

        FragmentUtil.replaceFragment(this, new AllChallengesFragment());
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.fl_home:
                iv_home.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
                iv_challenges.setColorFilter(ContextCompat.getColor(this, R.color.grey));

                Fragment fragment = FragmentUtil.getFragment(this, AllChallengesFragment.class.getSimpleName());

                if (!(fragment instanceof AllChallengesFragment)) {
                    fragment = new AllChallengesFragment();
                }

                FragmentUtil.replaceFragment(this, fragment);
                break;
            case R.id.fl_add:
                break;
            case R.id.fl_challenges:
                iv_challenges.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
                iv_home.setColorFilter(ContextCompat.getColor(this, R.color.grey));

                Fragment challengeFragment = FragmentUtil.getFragment(this, ChallengesFragment.class.getSimpleName());

                if (!(challengeFragment instanceof ChallengesFragment)) {
                    challengeFragment = new ChallengesFragment();
                }

                FragmentUtil.replaceFragment(this, challengeFragment);
                break;
        }
    }
}
