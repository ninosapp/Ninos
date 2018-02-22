package com.ninos.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ninos.R;
import com.ninos.activities.ProfileActivity;
import com.ninos.models.Comment;
import com.ninos.utils.AWSUrls;
import com.ninos.utils.DateUtil;

/**
 * Created by FAMILY on 05-01-2018.
 */

public class CommentAdapter extends CommonRecyclerAdapter<Comment> {

    private Context context;
    private RequestOptions requestOptions;
    private DateUtil dateUtil;

    public CommentAdapter(Context context) {
        this.context = context;

        requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_account)
                .error(R.drawable.ic_account)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .circleCrop();

        dateUtil = new DateUtil();
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        CommentViewHolder sampleViewHolder = (CommentViewHolder) genericHolder;

        sampleViewHolder.bindData(position);
    }

    private class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_image;
        TextView tv_user_name, tv_time, tv_comment;

        CommentViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            iv_image = itemView.findViewById(R.id.iv_image);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_comment = itemView.findViewById(R.id.tv_comment);
        }

        private void bindData(int position) {
            Comment comment = getItem(position);

            tv_comment.setText(comment.getComment());
            tv_user_name.setText(comment.getUserName());

            String time = dateUtil.formatDateToString(comment.getCreatedAt(), DateUtil.FULL_DATE);
            tv_time.setText(time);

            Glide.with(context).load(AWSUrls.GetPI64(context, comment.getUserId())).apply(requestOptions).into(iv_image);
        }

        @Override
        public void onClick(View v) {
            Comment comment = getItem(getAdapterPosition());

            Intent intent = new Intent(context, ProfileActivity.class);
            int resId = R.drawable.pattern_13;
            intent.putExtra(ProfileActivity.PROFILE_PLACE_HOLDER, resId);
            intent.putExtra(ProfileActivity.PROFILE_ID, comment.getUserId());
            context.startActivity(intent);
        }
    }
}
