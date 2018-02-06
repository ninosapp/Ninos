package com.ninos.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.ninos.R;
import com.ninos.adapters.QuestionsAdapter;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.EvaluateInfo;
import com.ninos.models.EvaluateResponse;
import com.ninos.models.MCQSolution;
import com.ninos.models.QuestionResponse;
import com.ninos.models.QuizEvaluateBody;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.PreferenceUtil;
import com.ninos.utils.TimerCountDown;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by FAMILY on 03-02-2018.
 */

public class QuizFragment extends BaseFragment implements View.OnClickListener {

    private static final String QUIZ_ID = "QUIZ_ID";
    private static final String QUIZ_DURATION = "QUIZ_DURATION";
    private static final String QUIZ_TITLE = "QUIZ_TITLE";
    private static final String QUIZ_EVALUATION_ID = "QUIZ_EVALUATION_ID";

    private String quizId, title, evaluationId;
    private TimeCounter mTimeCounter;
    private TextView tv_time;
    private int duration;
    private RetrofitService service;
    private QuestionsAdapter questionsAdapter;

    public static QuizFragment newInstance(String quizId, int duration, String title, String evaluationId) {
        QuizFragment quizFragment = new QuizFragment();
        Bundle bundle = new Bundle();
        bundle.putString(QUIZ_ID, quizId);
        bundle.putInt(QUIZ_DURATION, duration);
        bundle.putString(QUIZ_TITLE, title);
        bundle.putString(QUIZ_EVALUATION_ID, evaluationId);
        quizFragment.setArguments(bundle);
        return quizFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            evaluationId = getArguments().getString(QUIZ_EVALUATION_ID);
            quizId = getArguments().getString(QUIZ_ID);
            title = getArguments().getString(QUIZ_TITLE);

            int value = getArguments().getInt(QUIZ_DURATION);
            duration = value * 100;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            view.findViewById(R.id.iv_back).setOnClickListener(this);
            TextView tv_title = view.findViewById(R.id.tv_title);
            tv_title.setText(title);
            tv_time = view.findViewById(R.id.tv_time);
            tv_time.setText(String.format("%02d", duration / 1000));
            mTimeCounter = new TimeCounter(duration);

            final ViewPager view_pager = view.findViewById(R.id.view_pager);
            final TabLayout tabLayout = view.findViewById(R.id.tab_layout);

            service = RetrofitInstance.createService(RetrofitService.class);
            service.getQuiz(quizId, PreferenceUtil.getAccessToken(getContext())).enqueue(new Callback<QuestionResponse>() {
                @Override
                public void onResponse(@NonNull Call<QuestionResponse> call, @NonNull Response<QuestionResponse> response) {
                    if (response.body() != null && response.isSuccessful()) {
                        questionsAdapter = new QuestionsAdapter(getChildFragmentManager(), response.body().getQuestions(), quizId);
                        view_pager.setAdapter(questionsAdapter);
                        tabLayout.setupWithViewPager(view_pager, true);

                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                mTimeCounter.create();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(@NonNull Call<QuestionResponse> call, @NonNull Throwable t) {

                }
            });

        } catch (Exception e) {
            logError(e);
            showToast(R.string.error_message);
        }
    }

    @Override
    public void onDestroy() {
        if (mTimeCounter != null) {
            mTimeCounter.pause();
            mTimeCounter.cancel();
            mTimeCounter = null;
        }

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                showAlertDialog();
                break;
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.exit_quiz);
        builder.setMessage(R.string.exit_quiz_message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void onBackPressed() {
        showAlertDialog();
    }

    private class TimeCounter extends TimerCountDown {

        TimeCounter(int sec) {
            super(sec);
        }

        @Override
        public void onTick(long sec) {
            tv_time.setText(String.format("%02d", sec / 1000));
        }

        @Override
        public void onFinish() {
            if (mTimeCounter != null && mTimeCounter.isRunning()) {
                mTimeCounter.cancel();
            }

            tv_time.setText("00");

            if (questionsAdapter != null) {
                List<MCQSolution> mcqSolutions = questionsAdapter.getAnswers();

                QuizEvaluateBody quizEvaluateBody = new QuizEvaluateBody();
                quizEvaluateBody.setEvalutionId(evaluationId);
                quizEvaluateBody.setMcqSolution(mcqSolutions);

                service.evaluateResult(quizId, quizEvaluateBody, PreferenceUtil.getAccessToken(getContext())).enqueue(new Callback<EvaluateResponse>() {
                    @Override
                    public void onResponse(Call<EvaluateResponse> call, Response<EvaluateResponse> response) {
                        if (response.isSuccessful()) {
                            if (getContext() != null) {
                                final Dialog dialog = new Dialog(getContext());
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                                if (dialog.getWindow() != null) {
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                }

                                dialog.setContentView(R.layout.dialog_score);
                                TextView tv_score_one = dialog.findViewById(R.id.tv_score_one);
                                TextView tv_score_two = dialog.findViewById(R.id.tv_score_two);

                                EvaluateInfo eInfo = response.body().getEvaluateInfo();

                                if (eInfo.getAcquiredScore().length() > 1) {
                                    String score = eInfo.getAcquiredScore();
                                    tv_score_two.setText(score.substring(0, 1));
                                    tv_score_one.setText(score.substring(1, 2));
                                } else {
                                    tv_score_two.setText("0");
                                    tv_score_one.setText(eInfo.getAcquiredScore());
                                }

                                dialog.findViewById(R.id.fab_close).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();

                                        if (getActivity() != null) {
                                            getActivity().finish();
                                        }
                                    }
                                });

                                dialog.setCancelable(false);
                                dialog.show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<EvaluateResponse> call, Throwable t) {
                        showToast(R.string.error_message);
                    }
                });
            }
        }
    }
}