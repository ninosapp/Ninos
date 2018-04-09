package in.ninos.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.util.List;

import in.ninos.R;
import in.ninos.adapters.QuizViewAdapter;
import in.ninos.fragments.QuizFragment;
import in.ninos.fragments.QuizViewFragment;

public class QuizViewActivity extends BaseActivity {

    private QuizViewAdapter quizViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_quiz_view);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.accent_dark));
            }

            Toolbar toolbar_quiz_view = findViewById(R.id.toolbar_quiz_view);
            toolbar_quiz_view.setTitle(R.string.your_quizzes);
            toolbar_quiz_view.setTitleTextColor(Color.WHITE);
            setSupportActionBar(toolbar_quiz_view);

            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back_white));
            }

            ViewPager viewPager = findViewById(R.id.view_pager);
            quizViewAdapter = new QuizViewAdapter(this, getSupportFragmentManager());
            viewPager.setAdapter(quizViewAdapter);

            TabLayout tabLayout = findViewById(R.id.tab_layout);
            tabLayout.setupWithViewPager(viewPager);
        }  catch (Exception e) {
            logError(e);
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            switch (requestCode) {
                case MainActivity.QUIZ_COMPLETE:
                    if (data != null) {
                        String quizId = data.getStringExtra(QuizFragment.QUIZ_ID);

                        List<QuizViewFragment> fragments = quizViewAdapter.getFragments();

                        if (fragments != null) {
                            for (QuizViewFragment quizViewFragment : fragments) {
                                quizViewFragment.quizUpdated(quizId);
                            }
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            logError(e);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
