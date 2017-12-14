package com.ninos.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninos.BaseActivity;
import com.ninos.R;
import com.ninos.adapters.ChallengeAdapter;
import com.ninos.adapters.QuizAdapter;

/**
 * Created by smeesala on 6/30/2017.
 */

public class AllChallengesFragment extends BaseFragment {

    private BaseActivity mBaseActivity;
    private View cl_home;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_challenge, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            mBaseActivity = (BaseActivity) getActivity();

            cl_home = mBaseActivity.findViewById(R.id.cl_home);

            LinearLayoutManager quizLayoutManager = new LinearLayoutManager(mBaseActivity, LinearLayoutManager.HORIZONTAL, false);

            final RecyclerView quiz_list = view.findViewById(R.id.quiz_list);
            quiz_list.setNestedScrollingEnabled(false);
            quiz_list.setLayoutManager(quizLayoutManager);

            final QuizAdapter quizAdapter = new QuizAdapter(getContext());

            quiz_list.setAdapter(quizAdapter);

            for (int i = 0; i < 10; i++) {
                quizAdapter.addItem("Sumanth " + i);
            }

            LinearLayoutManager challengeLayoutManager = new LinearLayoutManager(mBaseActivity);

            final RecyclerView challenge_list = view.findViewById(R.id.challenge_list);
            challenge_list.setNestedScrollingEnabled(false);
            challenge_list.setLayoutManager(challengeLayoutManager);

            final ChallengeAdapter challengeAdapter = new ChallengeAdapter(getActivity());

            challenge_list.setAdapter(challengeAdapter);

            for (int i = 0; i < 10; i++) {
                challengeAdapter.addItem("Sumanth " + i);
            }
        } catch (Exception e) {
            logError(e);
            showSnackBar(R.string.error_message, cl_home);
        }
    }
}