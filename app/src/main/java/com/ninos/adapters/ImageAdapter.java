package com.ninos.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ninos.R;
import com.ninos.activities.ShowPostActivity;

/**
 * Created by FAMILY on 04-01-2018.
 */

public class ImageAdapter extends CommonRecyclerAdapter<String> {

    private Context context;
    private int resId;
    private String postId;

    public ImageAdapter(Context context, int resId, String postId) {
        this.context = context;
        this.resId = resId;
        this.postId = postId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        ImageViewHolder sampleViewHolder = (ImageViewHolder) genericHolder;

        sampleViewHolder.bindData(position);
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_challenge;

        ImageViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            iv_challenge = itemView.findViewById(R.id.iv_challenge);
        }

        private void bindData(int position) {
            String path = getItem(position);
            iv_challenge.setImageDrawable(ContextCompat.getDrawable(context, resId));

            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(resId)
                    .error(resId)
                    .fallback(resId)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(path).into(iv_challenge);
        }

        @Override
        public void onClick(View v) {
            Intent showPostIntent = new Intent(context, ShowPostActivity.class);
            showPostIntent.putExtra(ShowPostActivity.POST_PROFILE_ID, postId);
            context.startActivity(showPostIntent);
        }
    }
}