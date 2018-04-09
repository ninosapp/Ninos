package in.ninos.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import in.ninos.R;
import in.ninos.activities.BaseActivity;
import in.ninos.fragments.ImagePickFragment;
import in.ninos.fragments.ProfilePickFragment;
import in.ninos.fragments.VideoPickFragment;
import in.ninos.models.Bucket;
import in.ninos.utils.CrashUtil;

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
        VIDEOS,
        PROFILE
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
            try {
                tv_bucket.setText(bucket.getBucketName());
                Glide.with(baseActivity)
                        .load(bucket.getPath())
                        .into(iv_image);
            } catch (Exception e) {
                CrashUtil.report(e);
            }
        }

        @Override
        public void onClick(View view) {
            Bucket bucket = getItem(getAdapterPosition());
            FragmentTransaction fts = baseActivity.getSupportFragmentManager().beginTransaction();

            Fragment fragment;
            int id = R.id.fl_file_pick;

            if (type.equals(Type.IMAGES)) {
                fragment = ImagePickFragment.newInstance(bucket.getBucketName());
            } else if (type.equals(Type.VIDEOS)) {
                fragment = VideoPickFragment.newInstance(bucket.getBucketName());
            } else {
                fragment = ProfilePickFragment.newInstance(bucket.getBucketName());
                id = R.id.fl_profile_select;
            }

            fts.add(id, fragment);
            fts.commit();
        }
    }
}
