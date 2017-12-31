package com.ninos.adapters;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ninos.BaseActivity;
import com.ninos.R;
import com.ninos.models.Bucket;
import com.ninos.models.MediaObject;
import com.ninos.utils.BitmapDecoderUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FAMILY on 30-12-2017.
 */

public class BucketAdapter extends CommonRecyclerAdapter<Bucket> {
    private BaseActivity baseActivity;
    private Type type;

    public BucketAdapter(BaseActivity baseActivity, Type type) {
        this.baseActivity = baseActivity;
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bucket, parent, false);
        return new BucketHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        BucketHolder bucketHolder = (BucketHolder) genericHolder;
        bucketHolder.bindData(getItem(position));
    }

    private List<MediaObject> initImages(String bucket) {

        List<MediaObject> mOs = new ArrayList<>();

        try {

            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            String searchParams = "bucket_display_name = \"" + bucket + "\"";
            Cursor cursor = baseActivity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    searchParams, null, orderBy + " DESC");


            if (cursor != null && cursor.moveToFirst()) {

                do {
                    String filePath = cursor.getString(1);
                    int id = cursor.getInt(0);

                    MediaObject mO = new MediaObject(id, filePath);

                    mOs.add(mO);
                } while (cursor.moveToNext());

                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mOs;
    }

    public enum Type {
        IMAGES,
        VIDEOS
    }

    private class BucketHolder extends RecyclerView.ViewHolder {
        TextView tv_bucket;
        ImageView iv_image;

        BucketHolder(View view) {
            super(view);
            tv_bucket = view.findViewById(R.id.tv_bucket);
            iv_image = view.findViewById(R.id.iv_image);
        }

        void bindData(Bucket bucket) {
            tv_bucket.setText(bucket.getBucketName());

            if (Type.IMAGES.equals(type)) {
                new ImageTask().execute(bucket.getPath());
            } else {
                Glide.with(baseActivity)
                        .load(bucket.getPath())
                        .into(iv_image);
            }
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
