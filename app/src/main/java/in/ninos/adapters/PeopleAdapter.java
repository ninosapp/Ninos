package in.ninos.adapters;

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

import in.ninos.R;
import in.ninos.activities.MainActivity;
import in.ninos.activities.ProfileActivity;
import in.ninos.listeners.OnLoadMoreListener;
import in.ninos.listeners.RetrofitService;
import in.ninos.models.Response;
import in.ninos.models.UserInfo;
import in.ninos.reterofit.RetrofitInstance;
import in.ninos.utils.AWSUrls;
import in.ninos.utils.CrashUtil;
import in.ninos.utils.PreferenceUtil;
import in.ninos.views.CircleImageView;
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
            try {
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
            } catch (Exception e) {
                CrashUtil.report(e);
            }
        }

        @Override
        public void onClick(View view) {
            try {
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
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
                        } else {
                            service.follow(userInfo.getUserId(), accessToken).enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    if (response.body() != null && response.isSuccessful()) {
                                        userInfo.setFollowing(true);
                                        updateItem(position, userInfo);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

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
            } catch (Exception e) {
                CrashUtil.report(e);
            }
        }
    }
}
