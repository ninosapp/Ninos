package com.ninos.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ninos.R;
import com.ninos.fragments.InstructionFragment;
import com.ninos.fragments.QuizFragment;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.QuizStartResponse;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.PreferenceUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends BaseActivity {

    public static String QUIZ_ID = "QUIZ_ID";
    public static String QUIZ_DURATION = "QUIZ_DURATION";
    public static String QUIZ_TITLE = "QUIZ_TITLE";
    private String quizId, title;
    private int duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        quizId = getIntent().getStringExtra(QUIZ_ID);
        duration = getIntent().getIntExtra(QUIZ_DURATION, 6000);
        title = getIntent().getStringExtra(QUIZ_TITLE);

        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        fts.replace(R.id.frame_layout, new InstructionFragment());
        fts.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.frame_layout);

        if (fragment instanceof QuizFragment) {
            QuizFragment quizFragment = (QuizFragment) fragment;
            quizFragment.onBackPressed();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    public void startQuiz() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.starting_quiz));
        progressDialog.setCancelable(false);
        progressDialog.show();

        RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
        service.startQuiz(quizId, PreferenceUtil.getAccessToken(this)).enqueue(new Callback<QuizStartResponse>() {
            @Override
            public void onResponse(@NonNull Call<QuizStartResponse> call, @NonNull Response<QuizStartResponse> response) {
                progressDialog.dismiss();

                if (response.body() != null) {
                    FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
                    fts.replace(R.id.frame_layout, QuizFragment.newInstance(quizId, duration, title, response.body().getQuizStarted().get_id()));
                    fts.commit();
                } else {
                    showToast(R.string.error_message);
                }
            }

            @Override
            public void onFailure(@NonNull Call<QuizStartResponse> call, @NonNull Throwable t) {

            }
        });
    }
}
