package com.ninos.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninos.R;
import com.ninos.adapters.ChallengeAdapter;
import com.ninos.listeners.OnLoadMoreListener;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.ChallengeSearchResponse;
import com.ninos.models.PostInfo;
import com.ninos.models.PostResponse;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.PreferenceUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by FAMILY on 04-12-2017.
 */

public class ChallengesFragment extends BaseFragment implements OnLoadMoreListener {
    private ChallengeAdapter allChallengeAdapter;
    private RetrofitService service;
    private int from = 0, size = 10;
    private String accessToken;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_challenges, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            GridLayoutManager challengeLayoutManager = new GridLayoutManager(getContext(), 2);

            RecyclerView challenge_list = view.findViewById(R.id.challenge_list);
            challenge_list.setNestedScrollingEnabled(false);
            challenge_list.setLayoutManager(challengeLayoutManager);

            allChallengeAdapter = new ChallengeAdapter(getContext(), challenge_list, this);

            challenge_list.setAdapter(allChallengeAdapter);

            accessToken = PreferenceUtil.getAccessToken(getContext());
            service = RetrofitInstance.createService(RetrofitService.class);

            getPosts();
        } catch (Exception e) {
            logError(e);
            showToast(R.string.error_message);
        }
    }

    private void getPosts() {
        service.getChallenges(from, size, accessToken).enqueue(new Callback<ChallengeSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChallengeSearchResponse> call, @NonNull Response<ChallengeSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allChallengeAdapter.removeItem(null);

                    for (final PostInfo postInfo : response.body().getChallenges()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                allChallengeAdapter.addItem(postInfo);
                            }
                        });
                    }

                    from = from + size;
                }
            }

            @Override
            public void onFailure(Call<ChallengeSearchResponse> call, Throwable t) {
                logError(t.getMessage());
            }
        });
    }

    @Override
    public void onLoadMore() {
        allChallengeAdapter.addItem(null);
        getPosts();
    }

    public void newPostAdded(String postId) {
        service.getPost(postId, accessToken).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(@NonNull Call<PostResponse> call, @NonNull Response<PostResponse> response) {
                if (response.body() != null && response.isSuccessful() && allChallengeAdapter != null) {
                    PostInfo postInfo = response.body().getPostInfo();
                    allChallengeAdapter.addItem(postInfo, 0);
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {

            }
        });
    }

    public void newCommentAdded(String postId) {
        service.getPost(postId, accessToken).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(@NonNull Call<PostResponse> call, @NonNull Response<PostResponse> response) {
                if (response.body() != null && response.isSuccessful() && allChallengeAdapter != null) {
                    PostInfo postInfo = response.body().getPostInfo();

                    for (int i = 0; i < allChallengeAdapter.getItemCount(); i++) {
                        PostInfo pf = allChallengeAdapter.getItem(i);

                        if (pf.get_id().equals(postInfo.get_id())) {
                            allChallengeAdapter.updateItem(i, postInfo);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {

            }
        });
    }
}