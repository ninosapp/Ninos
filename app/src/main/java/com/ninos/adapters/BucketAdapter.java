package com.ninos.adapters;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ninos.BaseActivity;
import com.ninos.R;
import com.ninos.fragments.ImagePickFragment;
import com.ninos.models.Bucket;
import com.ninos.utils.BitmapDecoderUtil;

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

    public enum Type {
        IMAGES,
        VIDEOS
    }

    private class BucketHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_bucket;
        ImageView iv_image;
        View place_holder_view;

        BucketHolder(View view) {
            super(view);
            tv_bucket = view.findViewById(R.id.tv_bucket);
            iv_image = view.findViewById(R.id.iv_image);
            place_holder_view = view.findViewById(R.id.place_holder_view);
            place_holder_view.setOnClickListener(this);
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

        @Override
        public void onClick(View view) {
            Bucket bucket = getItem(getAdapterPosition());
            FragmentTransaction fts = baseActivity.getSupportFragmentManager().beginTransaction();
            fts.replace(R.id.fl_file_pick, ImagePickFragment.newInstance(bucket.getBucketName()), ImagePickFragment.class.getSimpleName());
            fts.commit();
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
