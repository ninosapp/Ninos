package in.ninos.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import in.ninos.R;
import in.ninos.adapters.SearchAdapter;
import in.ninos.fragments.AllChallengeSearchFragment;
import in.ninos.fragments.ChallengesSearchFragment;
import in.ninos.fragments.PeopleSearchFragment;

public class SearchActivity extends BaseActivity implements View.OnClickListener, EditText.OnEditorActionListener, ViewPager.OnPageChangeListener {

    public static final String USER_ID = "USER_ID";
    private EditText et_search;
    private SearchAdapter searchAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_search);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.accent_dark));
            }

            et_search = findViewById(R.id.et_search);
            et_search.setOnEditorActionListener(this);
            findViewById(R.id.iv_back).setOnClickListener(this);

            viewPager = findViewById(R.id.view_pager);
            searchAdapter = new SearchAdapter(this, getSupportFragmentManager());
            viewPager.setAdapter(searchAdapter);
            viewPager.setOffscreenPageLimit(3);
            viewPager.addOnPageChangeListener(this);

            TabLayout tabLayout = findViewById(R.id.tab_layout);
            tabLayout.setupWithViewPager(viewPager);
        } catch (Exception e) {
            logError(e);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        String text = et_search.getText().toString().trim();

        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (text.isEmpty()) {
                showToast(R.string.search_keyword_is_empty);
            } else {
                search(text);
            }
        }

        return false;
    }

    private void search(String text) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
            }

            int position = viewPager.getCurrentItem();
            Fragment fragment = searchAdapter.getItem(position);

            switch (position) {
                case 0:
                    if (fragment instanceof PeopleSearchFragment) {
                        PeopleSearchFragment peopleSearchFragment = (PeopleSearchFragment) fragment;
                        String userName = peopleSearchFragment.getUserName();

                        if (userName == null || !userName.equals(text)) {
                            peopleSearchFragment.userSearch(text);
                        }
                    }
                    break;
                case 1:
                    if (fragment instanceof AllChallengeSearchFragment) {
                        AllChallengeSearchFragment challengeSearchFragment = (AllChallengeSearchFragment) fragment;
                        String postKeyword = challengeSearchFragment.getPostKeyword();

                        if (postKeyword == null || !postKeyword.equals(text)) {
                            challengeSearchFragment.userSearch(text);
                        }
                    }
                    break;
                case 2:
                    if (fragment instanceof ChallengesSearchFragment) {
                        ChallengesSearchFragment challengeSearchFragment = (ChallengesSearchFragment) fragment;

                        String challengeKeyword = challengeSearchFragment.getChallengeKeyword();

                        if (challengeKeyword == null || !challengeKeyword.equals(text)) {
                            challengeSearchFragment.userSearch(text);
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            logError(e);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        String text = et_search.getText().toString().trim();

        if (!text.isEmpty()) {
            search(text);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            switch (requestCode) {
                case MainActivity.PROFILE_UPDATED:
                    if (data != null) {
                        boolean isProfileUpdated = data.getBooleanExtra(ProfileActivity.IS_PROFILE_UPDATED, false);

                        if (isProfileUpdated) {
                            Fragment fragment = searchAdapter.getItem(0);

                            if (fragment instanceof PeopleSearchFragment) {
                                PeopleSearchFragment peopleSearchFragment = (PeopleSearchFragment) fragment;
                                String userName = peopleSearchFragment.getUserName();

                                if (userName != null) {
                                    peopleSearchFragment.userSearch(userName);
                                }
                            }
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            logError(e);
        }
    }
}
