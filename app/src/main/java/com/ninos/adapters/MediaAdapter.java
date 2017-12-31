package com.ninos.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ninos.R;
import com.ninos.models.MediaObject;
import com.ninos.utils.BitmapDecoderUtil;

/**
 * Created by FAMILY on 30-12-2017.
 */

public class MediaAdapter extends CommonRecyclerAdapter<MediaObject> {
    private Context context;

    MediaAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_media, parent, false);
        return new BucketHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        BucketHolder bucketHolder = (BucketHolder) genericHolder;
        bucketHolder.bindData(getItem(position));
    }

    private class BucketHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;

        BucketHolder(View view) {
            super(view);
            iv_image = view.findViewById(R.id.iv_image);
        }

        void bindData(final MediaObject mo) {
            new ImageTask().execute(mo.getPath());
        }

        private class ImageTask extends AsyncTask<String, Void, Bitmap> {

            @Override
            protected Bitmap doInBackground(String... values) {
                String path = values[0];

                Bitmap thumb = BitmapDecoderUtil.decodeBitmapFromFile(path);
                return thumb;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                iv_image.setImageBitmap(bitmap);
            }
        }
    }


}