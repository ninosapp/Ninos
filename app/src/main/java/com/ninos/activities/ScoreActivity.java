package com.ninos.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ninos.R;
import com.ninos.adapters.SummaryAdapter;
import com.ninos.firebase.Database;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.EvaluateResult;
import com.ninos.models.QuizEvaluateResultResponse;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.PreferenceUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ScoreActivity extends BaseActivity {
    public static final String QUIZ_ID = "QUIZ_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        String quizId = getIntent().getStringExtra(QUIZ_ID);
        final TextView tv_score_one = findViewById(R.id.tv_score_one);
        View bottomSheet = findViewById(R.id.bottom_sheet);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(170);

        TextView tv_summary = findViewById(R.id.tv_summary);
        tv_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        LinearLayoutManager scoreLayoutManager = new LinearLayoutManager(this);

        final RecyclerView question_list = findViewById(R.id.question_list);
        question_list.setNestedScrollingEnabled(true);
        question_list.setLayoutManager(scoreLayoutManager);

        final SummaryAdapter summaryAdapter = new SummaryAdapter();
        question_list.setAdapter(summaryAdapter);

        RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
        service.getQuizResult(quizId, Database.getUserId(), PreferenceUtil.getAccessToken(this)).enqueue(new Callback<QuizEvaluateResultResponse>() {
            @Override
            public void onResponse(Call<QuizEvaluateResultResponse> call, Response<QuizEvaluateResultResponse> response) {
                if (response.body() != null) {

                    EvaluateResult eInfo = response.body().getEvaluateResult();

                    if (eInfo != null) {
                        tv_score_one.setText(String.format("%02d", Integer.parseInt(eInfo.getAcquiredScore())));
                        summaryAdapter.addItems(eInfo.getQuestions());
                    }
                }
            }

            @Override
            public void onFailure(Call<QuizEvaluateResultResponse> call, Throwable t) {
                showToast(R.string.error_message);
            }
        });

        findViewById(R.id.fab_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
