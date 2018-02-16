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
import com.ninos.listeners.RetrofitService;
import com.ninos.models.Follow;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.AWSUrls;
import com.ninos.utils.PreferenceUtil;
import com.ninos.views.CircleImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by FAMILY on 16-02-2018.
 */

public class FollowAdapter extends CommonRecyclerAdapter<Follow> {

    private Context context;
    private Activity activity;
    private TypedArray typedArray;
    private RetrofitService service;
    private String accessToken;

    public FollowAdapter(Context context, Activity activity) {
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
        return new FollowViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        FollowViewHolder sampleViewHolder = (FollowViewHolder) genericHolder;

        sampleViewHolder.bindData(position);
    }

    private class FollowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_user;
        CircleImageView iv_user;
        AppCompatButton btn_follow;

        FollowViewHolder(View itemView) {
            super(itemView);
            tv_user = itemView.findViewById(R.id.tv_user);
            iv_user = itemView.findViewById(R.id.iv_user);
            btn_follow = itemView.findViewById(R.id.btn_follow);
            btn_follow.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        private void bindData(int position) {
            Follow follow = getItem(position);
            tv_user.setText(follow.getUserName());

            int index = position % 10;
            int resId = typedArray.getResourceId(index, 0);

            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(resId)
                    .error(R.drawable.ic_account)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);

            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(AWSUrls.GetPI64(context, follow.getUserId()))
                    .into(iv_user);

            if (follow.isFollowing()) {
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
            final Follow follow = getItem(position);

            switch (view.getId()) {
                case R.id.btn_follow:
                    if (follow.isFollowing()) {
                        service.unFollow(follow.getUserId(), accessToken).enqueue(new Callback<com.ninos.models.Response>() {
                            @Override
                            public void onResponse(Call<com.ninos.models.Response> call, Response<com.ninos.models.Response> response) {
                                if (response.body() != null && response.isSuccessful()) {
                                    follow.setFollowing(false);
                                    removeItem(position);
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
                    intent.putExtra(ProfileActivity.PROFILE_ID, follow.getUserId());
                    activity.startActivityForResult(intent, MainActivity.PROFILE_UPDATED);
                    break;
            }
        }
    }
}
