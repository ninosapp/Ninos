package com.ninos.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ninos.R;
import com.ninos.adapters.QuestionsAdapter;
import com.ninos.models.MCQSolution;
import com.ninos.models.Question;

/**
 * Created by FAMILY on 04-02-2018.
 */

public class QuestionFragment extends BaseFragment implements View.OnClickListener {

    private static final String QUIZ_QUESTION = "QUIZ_QUESTION";
    private static final String QUIZ_OPTION = "QUIZ_OPTION";
    private static final String QUIZ_ANSWER = "QUIZ_ANSWER";
    private static final String QUIZ_ID = "QUIZ_ID";

    private String question, answer, quizId;
    private String[] options;
    private TextView tv_option1, tv_option2, tv_option3, tv_option4;
    private CardView cv_option1, cv_option2, cv_option3, cv_option4;
    private MCQSolution mcqSolution;

    public static QuestionFragment newInstance(Question question, String quizId) {
        QuestionFragment questionFragment = new QuestionFragment();

        Bundle bundle = new Bundle();
        bundle.putString(QUIZ_QUESTION, question.getQuestion());
        bundle.putStringArray(QUIZ_OPTION, question.getOptions());
        bundle.putString(QUIZ_ANSWER, question.getSolution());
        bundle.putString(QUIZ_ID, quizId);
        questionFragment.setArguments(bundle);

        return questionFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            quizId = getArguments().getString(QUIZ_ID);
            answer = getArguments().getString(QUIZ_ANSWER);
            question = getArguments().getString(QUIZ_QUESTION);
            options = getArguments().getStringArray(QUIZ_OPTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            TextView tv_question = view.findViewById(R.id.tv_question);
            tv_question.setText(question);

            tv_option1 = view.findViewById(R.id.tv_option1);
            tv_option2 = view.findViewById(R.id.tv_option2);
            tv_option3 = view.findViewById(R.id.tv_option3);
            tv_option4 = view.findViewById(R.id.tv_option4);

            tv_option1.setText(options[0]);
            tv_option2.setText(options[1]);
            tv_option3.setText(options[2]);
            tv_option4.setText(options[3]);

            cv_option1 = view.findViewById(R.id.cv_option1);
            cv_option2 = view.findViewById(R.id.cv_option2);
            cv_option3 = view.findViewById(R.id.cv_option3);
            cv_option4 = view.findViewById(R.id.cv_option4);

            cv_option1.setOnClickListener(this);
            cv_option2.setOnClickListener(this);
            cv_option3.setOnClickListener(this);
            cv_option4.setOnClickListener(this);

            mcqSolution = new MCQSolution();
            mcqSolution.setStatus(QuestionsAdapter.incorrect);
            mcqSolution.setQuestionId(quizId);
        } catch (Exception e) {
            logError(e);
            showToast(R.string.error_message);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        int index;

        tv_option1.setBackgroundColor(Color.WHITE);
        tv_option2.setBackgroundColor(Color.WHITE);
        tv_option3.setBackgroundColor(Color.WHITE);
        tv_option4.setBackgroundColor(Color.WHITE);

        switch (id) {
            default:
            case R.id.cv_option1:
                index = 0;

                if (getContext() != null) {
                    tv_option1.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.silver));
                }
                break;
            case R.id.cv_option2:
                index = 1;

                if (getContext() != null) {
                    tv_option2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.silver));
                }
                break;
            case R.id.cv_option3:
                index = 2;

                if (getContext() != null) {
                    tv_option3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.silver));
                }
                break;
            case R.id.cv_option4:
                index = 3;

                if (getContext() != null) {
                    tv_option4.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.silver));
                }
                break;
        }

        String option = options[index];

        mcqSolution = new MCQSolution();

        if (option.equals(answer)) {
            mcqSolution.setStatus(QuestionsAdapter.correct);
        } else {
            mcqSolution.setStatus(QuestionsAdapter.incorrect);
        }

        mcqSolution.setQuestionId(quizId);
        mcqSolution.setAnswer(option);
    }

    public MCQSolution getMCQSolution() {
        return mcqSolution;
    }

    public String getQuestion() {
        return question;
    }
}