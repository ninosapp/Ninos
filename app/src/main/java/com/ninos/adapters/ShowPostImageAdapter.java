package com.ninos.adapters;

import android.app.Activity;
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

/**
 * Created by FAMILY on 04-01-2018.
 */

public class ShowPostImageAdapter extends CommonRecyclerAdapter<String> {

    private Activity activity;
    private int resId;

    public ShowPostImageAdapter(Activity activity, int resId) {
        this.activity = activity;
        this.resId = resId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_show_post_image, parent, false);
        return new ShowPostImageViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        ShowPostImageViewHolder sampleViewHolder = (ShowPostImageViewHolder) genericHolder;

        sampleViewHolder.bindData(position);
    }

    private class ShowPostImageViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_challenge;

        ShowPostImageViewHolder(View itemView) {
            super(itemView);
            iv_challenge = itemView.findViewById(R.id.iv_challenge);
        }

        private void bindData(int position) {
            String path = getItem(position);
            iv_challenge.setImageDrawable(ContextCompat.getDrawable(activity, resId));

            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(resId)
                    .error(resId)
                    .fallback(resId)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(activity)
                    .setDefaultRequestOptions(requestOptions)
                    .load(path).into(iv_challenge);
        }
    }
}