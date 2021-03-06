package in.ninos.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import in.ninos.R;
import in.ninos.activities.FollowActivity;
import in.ninos.activities.MainActivity;
import in.ninos.activities.ProfileActivity;
import in.ninos.listeners.RetrofitService;
import in.ninos.models.Follow;
import in.ninos.reterofit.RetrofitInstance;
import in.ninos.utils.AWSUrls;
import in.ninos.utils.CrashUtil;
import in.ninos.utils.PreferenceUtil;
import in.ninos.views.CircleImageView;
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
    private String type;

    public FollowAdapter(Context context, Activity activity, String type) {
        this.activity = activity;
        this.context = context;
        this.type = type;
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
            try {
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
            } catch (Exception e) {
                CrashUtil.report(e);
            }
        }

        @Override
        public void onClick(View view) {
            try {
                final int position = getAdapterPosition();
                final Follow follow = getItem(position);

                switch (view.getId()) {
                    case R.id.btn_follow:
                        if (type.equals(FollowActivity.FOLLOWERS)) {
                            if (follow.isFollowing()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                        .setMessage(R.string.are_you_sure)
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                service.unFollow(follow.getUserId(), accessToken).enqueue(new Callback<in.ninos.models.Response>() {
                                                    @Override
                                                    public void onResponse(Call<in.ninos.models.Response> call, Response<in.ninos.models.Response> response) {
                                                        if (response.body() != null && response.isSuccessful()) {

                                                            if (response.body().isSuccess()) {
                                                                follow.setFollowing(false);
                                                                updateItem(position, follow);
                                                            } else {
                                                                Toast.makeText(context, R.string.failed_to_un_follow_user, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<in.ninos.models.Response> call, Throwable t) {

                                                    }
                                                });
                                            }
                                        })
                                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                builder.create().show();
                            } else {
                                service.follow(follow.getUserId(), accessToken).enqueue(new Callback<in.ninos.models.Response>() {
                                    @Override
                                    public void onResponse(Call<in.ninos.models.Response> call, Response<in.ninos.models.Response> response) {
                                        if (response.body() != null && response.isSuccessful()) {
                                            if (response.body().isSuccess()) {
                                                follow.setFollowing(true);
                                                updateItem(position, follow);
                                            } else {
                                                Toast.makeText(context, R.string.failed_to_follow_user, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<in.ninos.models.Response> call, Throwable t) {

                                    }
                                });
                            }
                        } else {
                            if (follow.isFollowing()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                        .setMessage(R.string.are_you_sure)
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                service.unFollow(follow.getUserId(), accessToken).enqueue(new Callback<in.ninos.models.Response>() {
                                                    @Override
                                                    public void onResponse(Call<in.ninos.models.Response> call, Response<in.ninos.models.Response> response) {
                                                        if (response.body() != null && response.isSuccessful()) {
                                                            follow.setFollowing(false);
                                                            removeItem(position);
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<in.ninos.models.Response> call, Throwable t) {

                                                    }
                                                });
                                            }
                                        })
                                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                builder.create().show();
                            }
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
            } catch (Exception e) {
                CrashUtil.report(e);
            }
        }
    }
}
