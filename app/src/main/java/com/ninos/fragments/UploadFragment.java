package com.ninos.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ninos.BaseActivity;
import com.ninos.R;
import com.ninos.adapters.UploadAdapter;
import com.ninos.firebase.Database;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.AddPostResponse;
import com.ninos.models.PostInfo;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.AWSClient;
import com.ninos.utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by FAMILY on 03-01-2018.
 */

public class UploadFragment extends BaseFragment implements View.OnClickListener {

    public final static String IMAGES = "IMAGES";
    public final static String VIDEOS = "VIDEOS";
    private final static String TYPE = "TYPE";
    private View cl_home;
    private ArrayList<String> paths;
    private TextView tv_description;
    private String path;
    private String type;

    public static UploadFragment newInstance(ArrayList<String> paths) {
        UploadFragment uploadFragment = new UploadFragment();

        Bundle bundle = new Bundle();
        bundle.putStringArrayList(IMAGES, paths);
        bundle.putString(TYPE, IMAGES);
        uploadFragment.setArguments(bundle);

        return uploadFragment;
    }

    public static UploadFragment newInstance(String path) {
        UploadFragment uploadFragment = new UploadFragment();

        Bundle bundle = new Bundle();
        bundle.putString(VIDEOS, path);
        bundle.putString(TYPE, VIDEOS);
        uploadFragment.setArguments(bundle);

        return uploadFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            type = getArguments().getString(TYPE);
            path = getArguments().getString(VIDEOS);
            paths = getArguments().getStringArrayList(IMAGES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            cl_home = baseActivity.findViewById(R.id.cl_home);

            Toolbar toolbar_image_pick = view.findViewById(R.id.toolbar_upload);
            toolbar_image_pick.setTitle(R.string.app_name);
            toolbar_image_pick.setTitleTextColor(ContextCompat.getColor(baseActivity, R.color.colorAccent));
            baseActivity.setSupportActionBar(toolbar_image_pick);

            ActionBar actionBar = baseActivity.getSupportActionBar();

            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(baseActivity, R.drawable.ic_back));
            }

            LinearLayoutManager layoutManager = new LinearLayoutManager(baseActivity, LinearLayoutManager.HORIZONTAL, false);

            final RecyclerView recyclerView = view.findViewById(R.id.upload_list);
            recyclerView.setLayoutManager(layoutManager);

            UploadAdapter uploadAdapter = new UploadAdapter(baseActivity);

            recyclerView.setAdapter(uploadAdapter);

            if (type.equals(IMAGES)) {
                for (String path : paths) {
                    uploadAdapter.addItem(path);
                }
            } else {
                uploadAdapter.addItem(path);
            }

            tv_description = view.findViewById(R.id.tv_description);
            view.findViewById(R.id.tv_cancel).setOnClickListener(this);
            view.findViewById(R.id.tv_select_count).setOnClickListener(this);
        } catch (Exception e) {
            logError(e);
            showSnackBar(R.string.error_message, cl_home);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_select_count:
                PostInfo postInfo = new PostInfo();
                final String token = PreferenceUtil.getAccessToken(getContext());
                final RetrofitService service = RetrofitInstance.createService(RetrofitService.class);


                service.addPost(postInfo, token).enqueue(new Callback<AddPostResponse>() {
                    @Override
                    public void onResponse(Call<AddPostResponse> call, Response<AddPostResponse> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            String title = tv_description.getText().toString();

                            PostInfo postInfo = response.body().getPostInfo();
                            postInfo.setTitle(title);
                            postInfo.setCreatedAt(new Date());
                            postInfo.setIsChallenge(false);
                            postInfo.setUserId(Database.getUserId());
                            postInfo.setType("post");

                            if (type.equals(IMAGES)) {
                                for (String path : paths) {
                                    AWSClient awsClient = new AWSClient(getContext(), postInfo.get_id(), path);
                                    awsClient.awsInit();
                                    awsClient.uploadImage(postInfo);
                                }
                            } else {
                                postInfo.setVideo(true);
                                AWSClient awsClient = new AWSClient(getContext(), postInfo.get_id(), path);
                                awsClient.awsInit();
                                awsClient.uploadVideo(postInfo);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AddPostResponse> call, Throwable t) {
                        Log.e(UploadFragment.class.getSimpleName(), t.getMessage());
                    }
                });
                break;
        }
    }
}
