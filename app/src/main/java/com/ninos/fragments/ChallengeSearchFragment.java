package com.ninos.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ninos.R;
import com.ninos.adapters.ChallengeAdapter;
import com.ninos.listeners.OnLoadMoreListener;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.PostInfo;
import com.ninos.models.PostSearchResponse;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.PreferenceUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by FAMILY on 23-01-2018.
 */

public class ChallengeSearchFragment extends BaseFragment implements OnLoadMoreListener {

    private RetrofitService service;
    private ChallengeAdapter challengeAdapter;
    private String accessToken;
    private int from = 0, size = 20;
    private String postKeyword;
    private TextView tv_empty;
    private RelativeLayout rl_empty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_people, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            Context context = getContext();

            accessToken = PreferenceUtil.getAccessToken(getContext());
            service = RetrofitInstance.createService(RetrofitService.class);

            tv_empty = view.findViewById(R.id.tv_empty);
            rl_empty = view.findViewById(R.id.rl_empty);
            rl_empty.setVisibility(View.GONE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(context);

            final RecyclerView recyclerView = view.findViewById(R.id.people_list);
            recyclerView.setLayoutManager(layoutManager);

            challengeAdapter = new ChallengeAdapter(getActivity(), recyclerView, this);

            recyclerView.setAdapter(challengeAdapter);


        } catch (Exception e) {
            logError(e);
        }
    }

    public void userSearch(String keyword) {
        postKeyword = keyword;
        from = 0;
        size = 20;
        challengeAdapter.clearItemsforSearch();
        getPosts();
    }

    private void getPosts() {
        service.getPosts(from, size, postKeyword, accessToken).enqueue(new Callback<PostSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<PostSearchResponse> call, @NonNull Response<PostSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    challengeAdapter.removeItem(null);

                    if (response.body().getPostsInfo().size() > 0) {
                        rl_empty.setVisibility(View.GONE);

                        for (final PostInfo postInfo : response.body().getPostsInfo()) {
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    challengeAdapter.addItem(postInfo);
                                }
                            });
                        }
                    } else if (from == 0) {
                        tv_empty.setText(String.format(getString(R.string.unable_to_find_the_post_for_s), postKeyword));
                        rl_empty.setVisibility(View.VISIBLE);
                    }

                    from = from + size;
                }
            }

            @Override
            public void onFailure(Call<PostSearchResponse> call, Throwable t) {
                logError(t.getMessage());
            }
        });
    }

    @Override
    public void onLoadMore() {
        challengeAdapter.addItem(null);
        getPosts();
    }

    public String getPostKeyword() {
        return postKeyword;
    }
}