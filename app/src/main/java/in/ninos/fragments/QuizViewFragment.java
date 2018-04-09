package in.ninos.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.ninos.R;
import in.ninos.adapters.QuizAdapter;
import in.ninos.listeners.OnLoadMoreListener;
import in.ninos.listeners.RetrofitService;
import in.ninos.models.QuizResponse;
import in.ninos.models.Quizze;
import in.ninos.reterofit.RetrofitInstance;
import in.ninos.utils.PreferenceUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by FAMILY on 19-02-2018.
 */

public class QuizViewFragment extends BaseFragment implements OnLoadMoreListener {

    public static final String ACTIVE = "ACTIVE";
    public static final String COMPLETED = "COMPLETED";
    private static final String TYPE = "TYPE";
    final int visibleThreshold = 2;
    private String type;
    private QuizAdapter quizAdapter;
    private String accessToken;
    private RetrofitService service;
    private TextView tv_empty;
    private SwipeRefreshLayout sr_layout;
    private int from = 0, size = 21;

    public static QuizViewFragment newInstance(String type) {
        QuizViewFragment quizViewFragment = new QuizViewFragment();

        Bundle bundle = new Bundle();
        bundle.putString(TYPE, type);
        quizViewFragment.setArguments(bundle);

        return quizViewFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            type = getArguments().getString(TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz_view, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            accessToken = PreferenceUtil.getAccessToken(getContext());
            service = RetrofitInstance.createService(RetrofitService.class);

            tv_empty = view.findViewById(R.id.tv_empty);

            final GridLayoutManager quizLayoutManager = new GridLayoutManager(getContext(), 3);

            RecyclerView quiz_list = view.findViewById(R.id.quiz_list);
            quiz_list.setLayoutManager(quizLayoutManager);
            quiz_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) {
                        int lastItem = quizLayoutManager.findLastCompletelyVisibleItemPosition();
                        int currentTotalCount = quizLayoutManager.getItemCount();

                        if (currentTotalCount <= lastItem + visibleThreshold) {
                            quizAdapter.addItem(null);
                            from = from + size;
                            getQuizzes();
                        }
                    }
                }
            });

            quizAdapter = new QuizAdapter(getContext(), getActivity());
            quiz_list.setAdapter(quizAdapter);

            getQuizzes();

            sr_layout = view.findViewById(R.id.sr_layout);
            sr_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (isNetworkAvailable(getContext())) {
                        from = 0;
                        quizAdapter.resetItems();
                        getQuizzes();
                    } else {
                        showNetworkDownSnackBar(sr_layout);
                    }
                }
            });

        } catch (Exception e) {
            logError(e);
            showToast(R.string.error_message);
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

    private void getQuizzes() {
        try {
            if (type.equals(COMPLETED)) {
                service.getCompletedQuizzes(from, size, accessToken).enqueue(new Callback<QuizResponse>() {
                    @Override
                    public void onResponse(Call<QuizResponse> call, @NonNull Response<QuizResponse> response) {
                        quizAdapter.removeItem(null);

                        if (response.isSuccessful() && response.body() != null) {
                            List<Quizze> quizzes = response.body().getQuizeData();

                            for (final Quizze quizze : quizzes) {
                                quizAdapter.addItem(quizze);
                            }

                            if (quizAdapter.getItemCount() > 0) {
                                tv_empty.setVisibility(View.GONE);
                            } else {
                                tv_empty.setVisibility(View.VISIBLE);
                            }
                        }

                        sr_layout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(@NonNull Call<QuizResponse> call, @NonNull Throwable t) {
                        quizAdapter.resetItems();
                        sr_layout.setRefreshing(false);
                        tv_empty.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                service.getActiveQuizzes(from, size, accessToken).enqueue(new Callback<QuizResponse>() {
                    @Override
                    public void onResponse(Call<QuizResponse> call, @NonNull Response<QuizResponse> response) {
                        quizAdapter.removeItem(null);

                        if (response.isSuccessful() && response.body() != null) {
                            List<Quizze> quizzes = response.body().getQuizeData();

                            for (final Quizze quizze : quizzes) {
                                quizAdapter.addItem(quizze);
                            }

                            if (quizAdapter.getItemCount() > 0) {
                                tv_empty.setVisibility(View.GONE);
                            } else {
                                tv_empty.setVisibility(View.VISIBLE);
                            }
                        }

                        sr_layout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(@NonNull Call<QuizResponse> call, @NonNull Throwable t) {
                        quizAdapter.resetItems();
                        sr_layout.setRefreshing(false);
                        tv_empty.setVisibility(View.VISIBLE);
                    }
                });
            }
        } catch (Exception e) {
            logError(e);
        }
    }

    @Override
    public void onLoadMore() {

    }
}