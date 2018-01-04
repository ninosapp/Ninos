package com.ninos.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class ImageAdapter extends CommonRecyclerAdapter<String> {

    private Context mContext;
    private int resId;

    public ImageAdapter(Context context, int resId) {
        mContext = context;
        this.resId = resId;
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

    private class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_challenge;

        ImageViewHolder(View itemView) {
            super(itemView);
            iv_challenge = itemView.findViewById(R.id.iv_challenge);
        }

        @SuppressLint("CheckResult")
        private void bindData(int position) {
            String path = getItem(position);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(resId);
            requestOptions.error(resId);
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(mContext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(path).into(iv_challenge);
        }
    }
}