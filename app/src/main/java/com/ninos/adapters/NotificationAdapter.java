package com.ninos.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ninos.R;
import com.ninos.activities.MainActivity;
import com.ninos.activities.ProfileActivity;
import com.ninos.activities.ShowPostActivity;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.Notification;
import com.ninos.models.Response;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.AWSUrls;
import com.ninos.utils.DateUtil;
import com.ninos.utils.PreferenceUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by FAMILY on 23-02-2018.
 */

public class NotificationAdapter extends CommonRecyclerAdapter<Notification> {

    private Activity activity;
    private Context context;
    private DateUtil dateUtil;
    private RequestOptions requestOptions;

    public NotificationAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        dateUtil = new DateUtil();

        requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_account)
                .error(R.drawable.ic_account)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        NotificationViewHolder sampleViewHolder = (NotificationViewHolder) genericHolder;

        sampleViewHolder.bindData(position);
    }

    private class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_profile;
        TextView tv_notification, tv_time;

        NotificationViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_notification = itemView.findViewById(R.id.tv_notification);
            tv_time = itemView.findViewById(R.id.tv_time);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            iv_profile.setOnClickListener(this);
        }

        private void bindData(int position) {
            Notification notification = getItem(position);

            String msg;

            switch (notification.getNotificationType()) {
                case "POST_COMMENTS":
                    msg = String.format("<b>%s</b> has commented on <b>%s</b>", notification.getFromUserName(), notification.getData().getPostTitle());
                    break;
                default:
                case "POST_CLAPS":
                    msg = String.format("<b>%s</b> has clapped for <b>%s</b>", notification.getFromUserName(), notification.getData().getPostTitle());
                    break;
                case "USER_FOLLOWING":
                    msg = String.format("<b>%s</b> is following you", notification.getFromUserName());
                    break;
            }

            tv_notification.setText(Html.fromHtml(msg), TextView.BufferType.SPANNABLE);

            if (notification.isRead()) {
                tv_notification.setTextColor(ContextCompat.getColor(context, R.color.silver));
            } else {
                tv_notification.setTextColor(ContextCompat.getColor(context, R.color.midnight_blue));
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            int isDatesEqualed = sdf.format(notification.getCreatedAt()).compareTo(sdf.format(new Date()));

            if (isDatesEqualed == 0) {
                long diff = new Date().getTime() - notification.getCreatedAt().getTime();

                if (diff > 0) {
                    int diffHours = (int) (diff / (60 * 60 * 1000) % 24);
                    tv_time.setText(String.format(context.getString(R.string.ago), diffHours));
                } else {
                    String date = dateUtil.formatDateToString(notification.getCreatedAt(), DateUtil.FULL_DATE);
                    tv_time.setText(date);
                }
            } else {
                String date = dateUtil.formatDateToString(notification.getCreatedAt(), DateUtil.FULL_DATE);
                tv_time.setText(date);
            }

            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(AWSUrls.GetPI64(context, notification.getFromUserId()))
                    .into(iv_profile);
        }

        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            final Notification notification = getItem(position);

            switch (v.getId()) {
                case R.id.iv_profile:
                    Intent profileIntent = new Intent(context, ProfileActivity.class);
                    profileIntent.putExtra(ProfileActivity.PROFILE_PLACE_HOLDER, R.drawable.pattern_13);
                    profileIntent.putExtra(ProfileActivity.PROFILE_ID, notification.getFromUserId());
                    context.startActivity(profileIntent);
                    break;
                default:
                    if (!notification.isRead()) {
                        RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
                        service.markNotificationAsRead(notification.get_id(), PreferenceUtil.getAccessToken(context)).enqueue(new Callback<Response>() {
                            @Override
                            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                notification.setRead(true);
                                updateItem(position, notification);
                            }

                            @Override
                            public void onFailure(Call<Response> call, Throwable t) {

                            }
                        });
                    }

                    switch (notification.getNotificationType()) {
                        default:
                        case "POST_COMMENTS":
                        case "POST_CLAPS":
                            Intent showPostIntent = new Intent(context, ShowPostActivity.class);
                            showPostIntent.putExtra(ShowPostActivity.POST_PROFILE_ID, notification.getData().getPostId());
                            activity.startActivityForResult(showPostIntent, MainActivity.POST_UPDATE);
                            break;
                        case "USER_FOLLOWING":
                            Intent intent = new Intent(context, ProfileActivity.class);
                            intent.putExtra(ProfileActivity.PROFILE_PLACE_HOLDER, R.drawable.pattern_13);
                            intent.putExtra(ProfileActivity.PROFILE_ID, notification.getFromUserId());
                            context.startActivity(intent);
                            break;
                    }
                    break;
            }
        }
    }
}