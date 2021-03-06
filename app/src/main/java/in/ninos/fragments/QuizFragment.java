package in.ninos.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.ninos.R;
import in.ninos.activities.MainActivity;
import in.ninos.activities.QuizActivity;
import in.ninos.activities.ScoreActivity;
import in.ninos.adapters.QuestionsAdapter;
import in.ninos.listeners.RetrofitService;
import in.ninos.models.EvaluateResponse;
import in.ninos.models.MCQSolution;
import in.ninos.models.QuestionResponse;
import in.ninos.models.QuizEvaluateBody;
import in.ninos.reterofit.RetrofitInstance;
import in.ninos.utils.PreferenceUtil;
import in.ninos.utils.TimerCountDown;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by FAMILY on 03-02-2018.
 */

public class QuizFragment extends BaseFragment implements View.OnClickListener {

    public static final String QUIZ_ID = "QUIZ_ID";
    private static final String QUIZ_DURATION = "QUIZ_DURATION";
    private static final String QUIZ_TITLE = "QUIZ_TITLE";
    private static final String QUIZ_EVALUATION_ID = "QUIZ_EVALUATION_ID";

    private String quizId, title, evaluationId;
    private TimeCounter mTimeCounter;
    private TextView tv_time;
    private int duration;
    private RetrofitService service;
    private QuestionsAdapter questionsAdapter;
    private ViewPager view_pager;
    private TabLayout tabLayout;
    private ImageView iv_previous, iv_next;
    private AppCompatButton btn_done;

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
            duration = value * 60000;
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

            iv_previous = view.findViewById(R.id.iv_previous);
            iv_previous.setVisibility(View.GONE);
            iv_previous.setOnClickListener(this);

            iv_next = view.findViewById(R.id.iv_next);
            iv_next.setOnClickListener(this);

            btn_done = view.findViewById(R.id.btn_done);
            btn_done.setVisibility(View.GONE);
            btn_done.setOnClickListener(this);

            view_pager = view.findViewById(R.id.view_pager);
            view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    int count = 0;

                    if (questionsAdapter != null) {
                        count = questionsAdapter.getCount();
                    }

                    if (position == 0) {
                        iv_previous.setVisibility(View.GONE);
                        btn_done.setVisibility(View.GONE);
                        iv_next.setVisibility(View.VISIBLE);
                    } else {
                        iv_previous.setVisibility(View.VISIBLE);
                        btn_done.setVisibility(View.GONE);
                        iv_next.setVisibility(View.VISIBLE);
                    }

                    if (position == count - 1) {
                        iv_next.setVisibility(View.GONE);
                        btn_done.setVisibility(View.VISIBLE);
                        iv_previous.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            tabLayout = view.findViewById(R.id.tab_layout);

            service = RetrofitInstance.createService(RetrofitService.class);
            service.getQuiz(quizId, PreferenceUtil.getAccessToken(getContext())).enqueue(new Callback<QuestionResponse>() {
                @Override
                public void onResponse(@NonNull Call<QuestionResponse> call, @NonNull Response<QuestionResponse> response) {
                    if (response.body() != null && response.isSuccessful() && response.body().getQuestions().size() > 0) {
                        try {
                            questionsAdapter = new QuestionsAdapter(getChildFragmentManager(), response.body().getQuestions(), quizId);
                            view_pager.setAdapter(questionsAdapter);
                            tabLayout.setupWithViewPager(view_pager, true);

                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    mTimeCounter.create();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast(R.string.error_message);

                        finishActivity();
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

    public void finishActivity() {
        try {
            if (getActivity() != null) {
                Intent intent = new Intent();
                intent.putExtra(QUIZ_ID, quizId);
                getActivity().setResult(MainActivity.QUIZ_COMPLETE, intent);
                getActivity().finish();
            }
        } catch (Exception e) {
            logError(e);
        }
    }

    @Override
    public void onDestroy() {
        try {
            if (mTimeCounter != null) {
                mTimeCounter.pause();
                mTimeCounter.cancel();
                mTimeCounter = null;
            }

            super.onDestroy();
        }catch (Exception e) {
            logError(e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                showAlertDialog();
                break;
            case R.id.iv_previous:
                view_pager.setCurrentItem(view_pager.getCurrentItem() - 1, true);
                break;
            case R.id.iv_next:
                view_pager.setCurrentItem(view_pager.getCurrentItem() + 1, true);
                break;
            case R.id.btn_done:
                submitQuiz();
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
                finishActivity();
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

    private void submitQuiz() {
        try {
            if (mTimeCounter != null && mTimeCounter.isRunning()) {
                mTimeCounter.cancel();
            }

            tv_time.setText(R.string._00);

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
                                if (getActivity() != null) {
                                    Intent intent = new Intent(getContext(), ScoreActivity.class);
                                    intent.putExtra(ScoreActivity.QUIZ_ID, quizId);
                                    getActivity().startActivityForResult(intent, QuizActivity.QUIZ_CLOSE);
                                }
                            }
                        } else {
                            finishActivity();
                        }
                    }

                    @Override
                    public void onFailure(Call<EvaluateResponse> call, Throwable t) {
                        showToast(R.string.error_message);
                    }
                });
            }
        } catch (Exception e) {
            logError(e);
        }
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
            submitQuiz();
        }
    }

}