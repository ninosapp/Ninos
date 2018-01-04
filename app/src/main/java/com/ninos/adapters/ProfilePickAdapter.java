package com.ninos.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ninos.R;
import com.ninos.activities.ProfileSelectActivity;
import com.ninos.models.MediaObject;

/**
 * Created by FAMILY on 04-01-2018.
 */

public class ProfilePickAdapter extends CommonRecyclerAdapter<MediaObject> {
    private ProfileSelectActivity baseActivity;

    public ProfilePickAdapter(ProfileSelectActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_media, parent, false);
        return new ImagePickHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        ImagePickHolder imagePickHolder = (ImagePickHolder) genericHolder;
        imagePickHolder.bindData(getItem(position));
    }


    private class ImagePickHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_image;

        ImagePickHolder(View view) {
            super(view);
            iv_image = view.findViewById(R.id.iv_image);
            view.findViewById(R.id.rl_selected).setVisibility(View.GONE);

            view.setOnClickListener(this);
        }

        void bindData(MediaObject mediaObject) {
            Glide.with(baseActivity)
                    .load(mediaObject.getPath())
                    .into(iv_image);
        }

        @Override
        public void onClick(View view) {
            MediaObject mediaObject = getItem(getAdapterPosition());
            baseActivity.setSelectedImage(mediaObject.getPath());
        }
    }
}