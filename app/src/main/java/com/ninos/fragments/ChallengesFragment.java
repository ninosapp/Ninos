package com.ninos.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninos.BaseActivity;
import com.ninos.R;
import com.ninos.adapters.ChallengeAdapter;
import com.ninos.listeners.OnLoadMoreListener;

/**
 * Created by FAMILY on 04-12-2017.
 */

public class ChallengesFragment extends BaseFragment implements OnLoadMoreListener {

    public static final String APP_NAME = "APP_NAME";
    private BaseActivity mBaseActivity;
    private View cl_home;

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

            final ChallengeAdapter challengeAdapter = new ChallengeAdapter(getActivity(), recyclerView, this);

            recyclerView.setAdapter(challengeAdapter);


        } catch (Exception e) {
            logError(e);
            showSnackBar(R.string.error_message, cl_home);
        }
    }

    @Override
    public void onLoadMore() {

    }
}