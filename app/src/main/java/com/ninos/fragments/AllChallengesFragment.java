package com.ninos.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninos.R;
import com.ninos.activities.MainActivity;
import com.ninos.adapters.ChallengeAdapter;
import com.ninos.adapters.QuizAdapter;
import com.ninos.listeners.OnLoadMoreListener;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.PostInfo;
import com.ninos.models.PostResponse;
import com.ninos.models.PostsResponse;
import com.ninos.models.QuizResponse;
import com.ninos.models.Quizze;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.PreferenceUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by smeesala on 6/30/2017.
 */

public class AllChallengesFragment extends BaseFragment implements OnLoadMoreListener {

    private MainActivity mBaseActivity;
    private View cl_home;
    private QuizAdapter quizAdapter;
    private ChallengeAdapter challengeAdapter;
    private RetrofitService service;
    private int from = 0, size = 10;
    private String accessToken;
    private RecyclerView challenge_list;
    private NestedScrollView ns_view;

    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_challenges, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            mBaseActivity = (MainActivity) getActivity();

            cl_home = mBaseActivity.findViewById(R.id.cl_home);
            ns_view = view.findViewById(R.id.ns_view);

            LinearLayoutManager quizLayoutManager = new LinearLayoutManager(mBaseActivity, LinearLayoutManager.HORIZONTAL, false);

            final RecyclerView quiz_list = view.findViewById(R.id.quiz_list);
            quiz_list.setNestedScrollingEnabled(false);
            quiz_list.setLayoutManager(quizLayoutManager);

            quizAdapter = new QuizAdapter(getContext());
            quiz_list.setAdapter(quizAdapter);

            LinearLayoutManager challengeLayoutManager = new LinearLayoutManager(mBaseActivity);

            challenge_list = view.findViewById(R.id.challenge_list);
            challenge_list.setNestedScrollingEnabled(false);
            challenge_list.setLayoutManager(challengeLayoutManager);

            challengeAdapter = new ChallengeAdapter(getActivity(), challenge_list, this);

            challenge_list.setAdapter(challengeAdapter);

            accessToken = PreferenceUtil.getAccessToken(getContext());
            service = RetrofitInstance.createService(RetrofitService.class);

            if (isNetworkAvailable(getContext())) {
                getQuizzes();

                getPosts();
            } else {
                mBaseActivity.showNoNetwork();
            }

        } catch (Exception e) {
            logError(e);
            showSnackBar(R.string.error_message, cl_home);
        }
    }

    public void refresh() {
        from = 0;
        challengeAdapter.clearItemsforSearch();
        getPosts();
    }

    private void getQuizzes() {
        service.getQuizzes(accessToken).enqueue(new Callback<QuizResponse>() {
            @Override
            public void onResponse(Call<QuizResponse> call, @NonNull Response<QuizResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Quizze> quizzes = response.body().getQuizeData();

                    for (Quizze quizze : quizzes) {
                        quizAdapter.addItem(quizze);
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<QuizResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void getPosts() {
        service.getPosts(from, size, accessToken).enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(@NonNull Call<PostsResponse> call, @NonNull Response<PostsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    challengeAdapter.removeItem(null);

                    for (final PostInfo postInfo : response.body().getPostInfo()) {
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
            public void onFailure(Call<PostsResponse> call, Throwable t) {
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

                    new Handler().postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            ns_view.scrollTo(0, 0);
                        }
                    }, 1000);
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