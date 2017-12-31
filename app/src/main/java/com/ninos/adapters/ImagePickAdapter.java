package com.ninos.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ninos.BaseActivity;
import com.ninos.R;
import com.ninos.models.MediaObject;
import com.ninos.utils.BitmapDecoderUtil;

/**
 * Created by FAMILY on 31-12-2017.
 */

public class ImagePickAdapter extends CommonRecyclerAdapter<MediaObject> {
    private BaseActivity baseActivity;

    public ImagePickAdapter(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bucket, parent, false);
        return new ImagePickHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        ImagePickHolder imagePickHolder = (ImagePickHolder) genericHolder;
        imagePickHolder.bindData(getItem(position));
    }

    private class ImagePickHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;

        ImagePickHolder(View view) {
            super(view);
            iv_image = view.findViewById(R.id.iv_image);
        }

        void bindData(MediaObject mediaObject) {
            Bitmap thumb = BitmapDecoderUtil.decodeBitmapFromFile(mediaObject.getPath());
            iv_image.setImageBitmap(thumb);
        }
    }
}
