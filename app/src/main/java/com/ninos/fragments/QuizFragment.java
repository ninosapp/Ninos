package com.ninos.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ninos.R;
import com.ninos.adapters.QuestionsAdapter;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.QuestionResponse;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.PreferenceUtil;
import com.ninos.utils.TimerCountDown;

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

    private String quizId, title;
    private TimeCounter mTimeCounter;
    private TextView tv_time;
    private int duration;

    public static QuizFragment newInstance(String quizId, int duration, String title) {
        QuizFragment quizFragment = new QuizFragment();
        Bundle bundle = new Bundle();
        bundle.putString(QUIZ_ID, quizId);
        bundle.putInt(QUIZ_DURATION, duration);
        bundle.putString(QUIZ_TITLE, title);
        quizFragment.setArguments(bundle);
        return quizFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            quizId = getArguments().getString(QUIZ_ID);
            int value = getArguments().getInt(QUIZ_DURATION);
            title = getArguments().getString(QUIZ_TITLE);
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

            RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
            service.getQuiz(quizId, PreferenceUtil.getAccessToken(getContext())).enqueue(new Callback<QuestionResponse>() {
                @Override
                public void onResponse(@NonNull Call<QuestionResponse> call, @NonNull Response<QuestionResponse> response) {
                    if (response.body() != null && response.isSuccessful()) {
                        view_pager.setAdapter(new QuestionsAdapter(getChildFragmentManager(), response.body().getQuestions()));
                        view_pager.setOffscreenPageLimit(10);
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
        }
    }
}