package in.ninos.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import in.ninos.R;
import in.ninos.activities.MainActivity;
import in.ninos.adapters.AllChallengeAdapter;
import in.ninos.adapters.QuizAdapter;
import in.ninos.listeners.OnLoadMoreListener;
import in.ninos.listeners.RetrofitService;
import in.ninos.models.PostInfo;
import in.ninos.models.PostResponse;
import in.ninos.models.PostsResponse;
import in.ninos.models.QuizResponse;
import in.ninos.models.Quizze;
import in.ninos.reterofit.RetrofitInstance;
import in.ninos.utils.PreferenceUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by smeesala on 6/30/2017.
 */

public class AllChallengesFragment extends BaseFragment implements OnLoadMoreListener, View.OnClickListener {

    private MainActivity mBaseActivity;
    private View cl_home;
    private QuizAdapter quizAdapter;
    private AllChallengeAdapter allChallengeAdapter;
    private RetrofitService service;
    private int from = 0, size = 5;
    private String accessToken;
    private RecyclerView challenge_list;
    private NestedScrollView ns_view;
    private ImageView iv_move_up;
    private SwipeRefreshLayout sr_layout;

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
            iv_move_up = view.findViewById(R.id.iv_move_up);
            iv_move_up.setOnClickListener(this);
            iv_move_up.setVisibility(View.GONE);

            sr_layout = view.findViewById(R.id.sr_layout);
            sr_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (isNetworkAvailable(getContext())) {
                        from = 0;
                        quizAdapter.resetItems();
                        allChallengeAdapter.resetItems();
                        getQuizzes();

                        getPosts();

                        mBaseActivity.getNotifications();

                        sr_layout.setRefreshing(false);
                    } else {
                        mBaseActivity.showNoNetwork();
                    }
                }
            });

            ns_view = view.findViewById(R.id.ns_view);
            ns_view.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > oldScrollY) {
                        iv_move_up.setVisibility(View.VISIBLE);
                    } else {
                        iv_move_up.setVisibility(View.GONE);
                    }
                }
            });

            LinearLayoutManager quizLayoutManager = new LinearLayoutManager(mBaseActivity, LinearLayoutManager.HORIZONTAL, false);

            final RecyclerView quiz_list = view.findViewById(R.id.quiz_list);
            quiz_list.setNestedScrollingEnabled(false);
            quiz_list.setLayoutManager(quizLayoutManager);

            quizAdapter = new QuizAdapter(getContext(), getActivity());
            quiz_list.setAdapter(quizAdapter);

            LinearLayoutManager challengeLayoutManager = new LinearLayoutManager(mBaseActivity);

            challenge_list = view.findViewById(R.id.challenge_list);
            challenge_list.setNestedScrollingEnabled(false);

            challenge_list.setLayoutManager(challengeLayoutManager);

            allChallengeAdapter = new AllChallengeAdapter(getContext(), getActivity(), challenge_list, this, AllChallengeAdapter.Type.POST);

            challenge_list.setAdapter(allChallengeAdapter);

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
        allChallengeAdapter.clearItemsforSearch();
        getPosts();
    }

    private void getQuizzes() {
        try {
            service.getActiveQuizzes(0, 10, accessToken).enqueue(new Callback<QuizResponse>() {
                @Override
                public void onResponse(Call<QuizResponse> call, @NonNull Response<QuizResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        final List<Quizze> quizzes = response.body().getQuizeData();

                        if (quizzes.size() > 10) {
                            for (int i = 0; i < 10; i++) {
                                Quizze quizze = quizzes.get(i);
                                quizAdapter.addItem(quizze);
                            }

                            Quizze quizze = new Quizze();
                            quizze.setQuizTaken(false);
                            quizze.setTitle("More");
                            quizze.set_id("More");
                            quizAdapter.addItem(quizze);
                        } else {
                            for (Quizze quizze : quizzes) {
                                quizAdapter.addItem(quizze);
                            }

                            quizAdapter.addItem(null);

                            service.getCompletedQuizzes(0, 10, accessToken).enqueue(new Callback<QuizResponse>() {
                                @Override
                                public void onResponse(Call<QuizResponse> call, @NonNull Response<QuizResponse> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        quizAdapter.removeItem(null);

                                        List<Quizze> quizzes = response.body().getQuizeData();

                                        for (Quizze quizze : quizzes) {
                                            if (quizAdapter.getItemCount() >= 10) {
                                                break;
                                            }

                                            quizAdapter.addItem(quizze);
                                        }

                                        if (quizAdapter.getItemCount() >= 10) {
                                            Quizze quizze = new Quizze();
                                            quizze.setQuizTaken(false);
                                            quizze.setTitle("More");
                                            quizze.set_id("More");
                                            quizAdapter.addItem(quizze);
                                        }
                                    }

                                }

                                @Override
                                public void onFailure(@NonNull Call<QuizResponse> call, @NonNull Throwable t) {
                                    quizAdapter.removeItem(null);

                                    if (quizAdapter.getItemCount() >= 10) {
                                        Quizze quizze = new Quizze();
                                        quizze.setQuizTaken(false);
                                        quizze.setTitle("More");
                                        quizze.set_id("More");
                                        quizAdapter.addItem(quizze);
                                    }
                                }
                            });
                        }
                    }

                }

                @Override
                public void onFailure(@NonNull Call<QuizResponse> call, @NonNull Throwable t) {

                }
            });
        } catch (Exception e) {
            logError(e);
        }
    }

    private void getPosts() {
        try {
            service.searchPosts(from, size, accessToken).enqueue(new Callback<PostsResponse>() {
                @Override
                public void onResponse(@NonNull Call<PostsResponse> call, @NonNull Response<PostsResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        allChallengeAdapter.removeItem(null);

                        for (final PostInfo postInfo : response.body().getPostInfo()) {
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
                public void onFailure(Call<PostsResponse> call, Throwable t) {
                    logError(t.getMessage());
                }
            });
        } catch (Exception e) {
            logError(e);
        }
    }

    @Override
    public void onLoadMore() {
        allChallengeAdapter.addItem(null);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getPosts();
            }
        }, 2000);
    }

    public void newPostAdded(final String postId) {
        try {
            service.getPost(postId, accessToken).enqueue(new Callback<PostResponse>() {
                @Override
                public void onResponse(@NonNull Call<PostResponse> call, @NonNull Response<PostResponse> response) {
                    if (response.body() != null && response.isSuccessful() && allChallengeAdapter != null) {
                        boolean postAdded = false;

                        for (int i = 0; i < allChallengeAdapter.getItemCount(); i++) {
                            PostInfo postInfo = allChallengeAdapter.getItem(i);

                            if (postInfo != null && postInfo.get_id().equals(postId)) {
                                postAdded = true;
                            }
                        }

                        if (!postAdded) {
                            PostInfo postInfo = response.body().getPostInfo();
                            allChallengeAdapter.addItem(postInfo, 0);

                            new Handler().postAtTime(new Runnable() {
                                @Override
                                public void run() {
                                    ns_view.scrollTo(0, 0);
                                }
                            }, 1000);
                        }
                    }
                }

                @Override
                public void onFailure(Call<PostResponse> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            logError(e);
        }
    }

    public void newCommentAdded(String postId) {
        try {
            service.getPost(postId, accessToken).enqueue(new Callback<PostResponse>() {
                @Override
                public void onResponse(@NonNull Call<PostResponse> call, @NonNull Response<PostResponse> response) {
                    if (response.body() != null && response.isSuccessful() && allChallengeAdapter != null) {
                        PostInfo postInfo = response.body().getPostInfo();

                        for (int i = 0; i < allChallengeAdapter.getItemCount(); i++) {
                            PostInfo pf = allChallengeAdapter.getItem(i);

                            if (pf.get_id().equals(postInfo.get_id())) {
                                RecyclerView.ViewHolder viewHolder = challenge_list.findViewHolderForAdapterPosition(i);
                                allChallengeAdapter.updateComment(viewHolder, postInfo.getTotalCommentCount());
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<PostResponse> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            logError(e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_move_up:
                iv_move_up.setVisibility(View.GONE);
                ns_view.post(new Runnable() {
                    @Override
                    public void run() {
                        ns_view.scrollTo(0, 0);
                        ns_view.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
                break;
        }
    }

    public void quizUpdated(String quizId) {
        try {
            for (int i = 0; i < quizAdapter.getItemCount(); i++) {
                Quizze quizze = quizAdapter.getItem(i);

                if (quizze.get_id().equals(quizId)) {
                    quizze.setQuizTaken(true);
                    quizAdapter.updateItem(i, quizze);
                    break;
                }
            }
        } catch (Exception e) {
            logError(e);
        }
    }

    public void postUpdated(String postId, String desc, ArrayList<String> links) {
        try {
            for (int i = 0; i < allChallengeAdapter.getItemCount(); i++) {
                PostInfo postInfo = allChallengeAdapter.getItem(i);

                if (postInfo.get_id().equals(postId)) {
                    postInfo.setTitle(desc);
                    RecyclerView.ViewHolder viewHolder = challenge_list.findViewHolderForAdapterPosition(i);
                    allChallengeAdapter.updateTitle(viewHolder, desc);

                    if (links.size() > 0) {
                        postInfo.setLinks(links);
                        allChallengeAdapter.updateItem(i, postInfo);
                    }

                    break;
                }
            }
        } catch (Exception e) {
            logError(e);
        }
    }

    public void postDeleted(String postId) {
        try {
            for (int i = 0; i < allChallengeAdapter.getItemCount(); i++) {
                PostInfo postInfo = allChallengeAdapter.getItem(i);

                if (postInfo.get_id().equals(postId)) {
                    allChallengeAdapter.removeItem(i);
                    break;
                }
            }
        } catch (Exception e) {
            logError(e);
        }
    }


    public void newClapAdded(String postId) {
        try {
            service.getPost(postId, accessToken).enqueue(new Callback<PostResponse>() {
                @Override
                public void onResponse(@NonNull Call<PostResponse> call, @NonNull Response<PostResponse> response) {
                    if (response.body() != null && response.isSuccessful() && allChallengeAdapter != null) {
                        PostInfo postInfo = response.body().getPostInfo();

                        if (postInfo != null) {
                            for (int i = 0; i < allChallengeAdapter.getItemCount(); i++) {
                                PostInfo pf = allChallengeAdapter.getItem(i);

                                if (pf != null && pf.get_id().equals(postInfo.get_id())) {
                                    RecyclerView.ViewHolder viewHolder = challenge_list.findViewHolderForAdapterPosition(i);
                                    allChallengeAdapter.updateClap(viewHolder, postInfo);
                                    break;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<PostResponse> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            logError(e);
        }
    }
}