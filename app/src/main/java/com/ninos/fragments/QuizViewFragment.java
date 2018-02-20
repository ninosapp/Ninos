package com.ninos.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ninos.R;
import com.ninos.adapters.QuizAdapter;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.QuizResponse;
import com.ninos.models.Quizze;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.PreferenceUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by FAMILY on 19-02-2018.
 */

public class QuizViewFragment extends BaseFragment {

    public static final String ACTIVE = "ACTIVE";
    public static final String COMPLETED = "COMPLETED";
    private static final String TYPE = "TYPE";
    private String type;
    private QuizAdapter quizAdapter;
    private String accessToken;
    private RetrofitService service;
    private TextView tv_empty;

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

            GridLayoutManager quizLayoutManager = new GridLayoutManager(getContext(), 3);

            RecyclerView quiz_list = view.findViewById(R.id.quiz_list);
            quiz_list.setNestedScrollingEnabled(false);
            quiz_list.setLayoutManager(quizLayoutManager);

            quizAdapter = new QuizAdapter(getContext(), getActivity());
            quiz_list.setAdapter(quizAdapter);

            getQuizzes();
        } catch (Exception e) {
            logError(e);
            showToast(R.string.error_message);
        }
    }

    private void getQuizzes() {
        if (type.equals(COMPLETED)) {
            service.getCompletedQuizzes(accessToken).enqueue(new Callback<QuizResponse>() {
                @Override
                public void onResponse(Call<QuizResponse> call, @NonNull Response<QuizResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Quizze> quizzes = response.body().getQuizeData();

                        for (Quizze quizze : quizzes) {
                            quizAdapter.addItem(quizze);
                        }

                        if (quizzes.size() > 0) {
                            tv_empty.setVisibility(View.GONE);
                        }
                    }

                }

                @Override
                public void onFailure(@NonNull Call<QuizResponse> call, @NonNull Throwable t) {

                }
            });
        } else {
            service.getActiveQuizzes(accessToken).enqueue(new Callback<QuizResponse>() {
                @Override
                public void onResponse(Call<QuizResponse> call, @NonNull Response<QuizResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Quizze> quizzes = response.body().getQuizeData();

                        for (Quizze quizze : quizzes) {
                            quizAdapter.addItem(quizze);
                        }

                        if (quizzes.size() > 0) {
                            tv_empty.setVisibility(View.GONE);
                        }
                    }

                }

                @Override
                public void onFailure(@NonNull Call<QuizResponse> call, @NonNull Throwable t) {

                }
            });
        }
    }
}