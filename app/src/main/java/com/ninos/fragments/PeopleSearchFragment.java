package com.ninos.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ninos.R;
import com.ninos.adapters.PeopleAdapter;
import com.ninos.listeners.OnLoadMoreListener;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.PeopleResponse;
import com.ninos.models.UserInfo;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.PreferenceUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PeopleSearchFragment extends BaseFragment implements OnLoadMoreListener {

    private RetrofitService service;
    private PeopleAdapter peopleAdapter;
    private String accessToken;
    private int from = 0, size = 20;
    private String userName;
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

            GridLayoutManager layoutManager = new GridLayoutManager(context, 2);

            final RecyclerView recyclerView = view.findViewById(R.id.people_list);
            recyclerView.setLayoutManager(layoutManager);

            peopleAdapter = new PeopleAdapter(context, getActivity(), recyclerView, this);

            recyclerView.setAdapter(peopleAdapter);


        } catch (Exception e) {
            logError(e);
        }
    }

    public void userSearch(String keyword) {
        userName = keyword;
        from = 0;
        size = 20;
        peopleAdapter.clearItemsforSearch();
        getUsers();
    }

    private void getUsers() {
        service.searchUsers(from, size, userName, accessToken).enqueue(new Callback<PeopleResponse>() {
            @Override
            public void onResponse(@NonNull Call<PeopleResponse> call, @NonNull Response<PeopleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    peopleAdapter.removeItem(null);

                    if (response.body().getUsers().size() > 0) {
                        rl_empty.setVisibility(View.GONE);

                        for (final UserInfo userInfo : response.body().getUsers()) {
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    peopleAdapter.addItem(userInfo);
                                }
                            });
                        }
                    } else if (from == 0) {
                        tv_empty.setText(String.format(getString(R.string.unable_to_find_the_user_for_s), userName));
                        rl_empty.setVisibility(View.VISIBLE);
                    }

                    from = from + size;
                }
            }

            @Override
            public void onFailure(Call<PeopleResponse> call, Throwable t) {
                logError(t.getMessage());
            }
        });
    }

    @Override
    public void onLoadMore() {
        peopleAdapter.addItem(null);
        getUsers();
    }

    public String getUserName() {
        return userName;
    }
}
