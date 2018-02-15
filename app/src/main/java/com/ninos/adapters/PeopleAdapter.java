package com.ninos.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ninos.R;
import com.ninos.activities.MainActivity;
import com.ninos.activities.ProfileActivity;
import com.ninos.listeners.OnLoadMoreListener;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.Response;
import com.ninos.models.UserInfo;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.AWSUrls;
import com.ninos.utils.PreferenceUtil;
import com.ninos.views.CircleImageView;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by FAMILY on 22-01-2018.
 */

public class PeopleAdapter extends CommonRecyclerAdapter<UserInfo> {

    private Context context;
    private Activity activity;
    private TypedArray typedArray;
    private RetrofitService service;
    private String accessToken;

    public PeopleAdapter(Context context, Activity activity, RecyclerView recyclerView, OnLoadMoreListener onLoadMoreListener) {
        super(recyclerView, onLoadMoreListener);

        this.activity = activity;
        this.context = context;
        typedArray = context.getResources().obtainTypedArray(R.array.patterns);
        accessToken = PreferenceUtil.getAccessToken(context);

        service = RetrofitInstance.createService(RetrofitService.class);
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_people, parent, false);
        return new PeopleViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        PeopleViewHolder sampleViewHolder = (PeopleViewHolder) genericHolder;

        sampleViewHolder.bindData(position);
    }

    private class PeopleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_user;
        CircleImageView iv_user;
        AppCompatButton btn_follow;

        PeopleViewHolder(View itemView) {
            super(itemView);
            tv_user = itemView.findViewById(R.id.tv_user);
            iv_user = itemView.findViewById(R.id.iv_user);
            btn_follow = itemView.findViewById(R.id.btn_follow);
            btn_follow.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        private void bindData(int position) {
            UserInfo userInfo = getItem(position);
            tv_user.setText(userInfo.getChildName());

            int index = position % 10;
            int resId = typedArray.getResourceId(index, 0);

            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(resId)
                    .error(R.drawable.ic_account)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);

            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(AWSUrls.GetPI64(context, userInfo.getUserId()))
                    .into(iv_user);

            if (userInfo.isFollowing()) {
                btn_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_remove_user, 0, 0, 0);
                btn_follow.setText(R.string.unfollow);
            } else {
                btn_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_user, 0, 0, 0);
                btn_follow.setText(R.string.follow);
            }
        }

        @Override
        public void onClick(View view) {
            final int position = getAdapterPosition();
            final UserInfo userInfo = getItem(position);

            switch (view.getId()) {
                case R.id.btn_follow:
                    if (userInfo.isFollowing()) {
                        service.unFollow(userInfo.getUserId(), accessToken).enqueue(new Callback<Response>() {
                            @Override
                            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                if (response.body() != null && response.isSuccessful()) {
                                    userInfo.setFollowing(false);
                                    updateItem(position, userInfo);
                                }
                            }

                            @Override
                            public void onFailure(Call<com.ninos.models.Response> call, Throwable t) {

                            }
                        });
                    } else {
                        service.follow(userInfo.getUserId(), accessToken).enqueue(new Callback<com.ninos.models.Response>() {
                            @Override
                            public void onResponse(Call<com.ninos.models.Response> call, retrofit2.Response<Response> response) {
                                if (response.body() != null && response.isSuccessful()) {
                                    userInfo.setFollowing(true);
                                    updateItem(position, userInfo);
                                }
                            }

                            @Override
                            public void onFailure(Call<com.ninos.models.Response> call, Throwable t) {

                            }
                        });
                    }

                    break;
                default:
                    Intent intent = new Intent(context, ProfileActivity.class);
                    int resId = typedArray.getResourceId(position, 0);
                    intent.putExtra(ProfileActivity.PROFILE_PLACE_HOLDER, resId);
                    intent.putExtra(ProfileActivity.PROFILE_ID, userInfo.getUserId());
                    activity.startActivityForResult(intent, MainActivity.PROFILE_UPDATED);
                    break;
            }
        }
    }
}
