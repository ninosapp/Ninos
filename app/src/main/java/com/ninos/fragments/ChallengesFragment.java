package com.ninos.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
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
    private ChallengeAdapter challengeAdapter;
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
            LinearLayoutManager challengeLayoutManager = new LinearLayoutManager(getContext());

            RecyclerView challenge_list = view.findViewById(R.id.challenge_list);
            challenge_list.setNestedScrollingEnabled(false);
            challenge_list.setLayoutManager(challengeLayoutManager);

            challengeAdapter = new ChallengeAdapter(getActivity(), challenge_list, this);

            challenge_list.setAdapter(challengeAdapter);

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
                    challengeAdapter.removeItem(null);

                    for (final PostInfo postInfo : response.body().getChallenges()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                challengeAdapter.addItem(postInfo);
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
        challengeAdapter.addItem(null);
        getPosts();
    }

    public void newPostAdded(String postId) {
        service.getPost(postId, accessToken).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(@NonNull Call<PostResponse> call, @NonNull Response<PostResponse> response) {
                if (response.body() != null && response.isSuccessful() && challengeAdapter != null) {
                    PostInfo postInfo = response.body().getPostInfo();
                    challengeAdapter.addItem(postInfo, 0);
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
                if (response.body() != null && response.isSuccessful() && challengeAdapter != null) {
                    PostInfo postInfo = response.body().getPostInfo();

                    for (int i = 0; i < challengeAdapter.getItemCount(); i++) {
                        PostInfo pf = challengeAdapter.getItem(i);

                        if (pf.get_id().equals(postInfo.get_id())) {
                            challengeAdapter.updateItem(i, postInfo);
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