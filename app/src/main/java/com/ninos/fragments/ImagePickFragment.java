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
import com.ninos.adapters.BucketAdapter;

/**
 * Created by FAMILY on 29-12-2017.
 */

public class ImagePickFragment extends BaseFragment {

    private BaseActivity mBaseActivity;
    private View cl_home;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_pick, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            mBaseActivity = (BaseActivity) getActivity();

            cl_home = mBaseActivity.findViewById(R.id.cl_home);

            LinearLayoutManager layoutManager = new LinearLayoutManager(mBaseActivity);

            final RecyclerView recyclerView = view.findViewById(R.id.bucket_list);
            recyclerView.setLayoutManager(layoutManager);

            final BucketAdapter bucketAdapter = new BucketAdapter(mBaseActivity, BucketAdapter.Type.IMAGES);

            recyclerView.setAdapter(bucketAdapter);
        } catch (Exception e) {
            logError(e);
            showSnackBar(R.string.error_message, cl_home);
        }
    }
}