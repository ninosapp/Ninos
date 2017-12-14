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

/**
 * Created by FAMILY on 04-12-2017.
 */

public class ChallengesFragment extends BaseFragment {

    public static final String APP_NAME = "APP_NAME";
    private BaseActivity mBaseActivity;
    private View cl_home;
    private String mAppName;

    public static AllChallengesFragment newInstance(String appName) {
        AllChallengesFragment allChallengesFragment = new AllChallengesFragment();

        Bundle args = new Bundle();
        args.putString(APP_NAME, appName);
        allChallengesFragment.setArguments(args);
        return allChallengesFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mAppName = getArguments().getString(APP_NAME);
        }
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

            LinearLayoutManager layoutManager = new LinearLayoutManager(mBaseActivity);

            final RecyclerView recyclerView = view.findViewById(R.id.challenge_list);
            recyclerView.setLayoutManager(layoutManager);

            final ChallengeAdapter challengeAdapter = new ChallengeAdapter(getActivity());

            recyclerView.setAdapter(challengeAdapter);


            for (int i = 0; i < 10; i++) {
                challengeAdapter.addItem("Sumanth " + i);
            }
        } catch (Exception e) {
            logError(e);
            showSnackBar(R.string.error_message, cl_home);
        }
    }
}