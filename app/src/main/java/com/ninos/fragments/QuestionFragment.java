package com.ninos.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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

            tv_option1.setOnClickListener(this);
            tv_option2.setOnClickListener(this);
            tv_option3.setOnClickListener(this);
            tv_option4.setOnClickListener(this);
        } catch (Exception e) {
            logError(e);
            showToast(R.string.error_message);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        int index;

        switch (id) {
            default:
            case R.id.tv_option1:
                index = 0;
                break;
            case R.id.tv_option2:
                index = 1;
                break;
            case R.id.tv_option3:
                index = 2;
                break;
            case R.id.tv_option4:
                index = 3;
                break;
        }

        tv_option1.setOnClickListener(null);
        tv_option2.setOnClickListener(null);
        tv_option3.setOnClickListener(null);
        tv_option4.setOnClickListener(null);

        String option = options[index];

        mcqSolution = new MCQSolution();

        if (getContext() != null) {
            TextView textView = (TextView) view;
            Drawable drawable;

            if (option.equals(answer)) {
                mcqSolution.setStatus(QuestionsAdapter.correct);
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_rectangle_green);
            } else {
                mcqSolution.setStatus(QuestionsAdapter.incorrect);
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_rectangle_red);
            }

            textView.setBackground(drawable);
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