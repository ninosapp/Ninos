package in.ninos.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import in.ninos.R;
import in.ninos.activities.BaseActivity;
import in.ninos.models.MediaObject;

/**
 * Created by FAMILY on 01-01-2018.
 */

public class VideoPickAdapter extends CommonRecyclerAdapter<MediaObject> {
    private BaseActivity baseActivity;
    private MediaObject selectedMedia;
    private View selectedView;

    public VideoPickAdapter(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_media, parent, false);
        return new VideoPickHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        VideoPickHolder imagePickHolder = (VideoPickHolder) genericHolder;
        imagePickHolder.bindData(getItem(position));
    }

    public String getSelectedMedia() {
        String path = null;

        if (selectedMedia != null) {
            path = selectedMedia.getPath();
        }

        return path;
    }

    private class VideoPickHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_image;
        RelativeLayout rl_selected;

        VideoPickHolder(View view) {
            super(view);
            iv_image = view.findViewById(R.id.iv_image);
            rl_selected = view.findViewById(R.id.rl_selected);

            view.setOnClickListener(this);
        }

        void bindData(MediaObject mediaObject) {
            Glide.with(baseActivity)
                    .setDefaultRequestOptions(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                    .load(mediaObject.getPath())
                    .into(iv_image);

            if (selectedMedia != null && selectedMedia.equals(mediaObject)) {
                rl_selected.setVisibility(View.VISIBLE);
            } else {
                rl_selected.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            MediaObject mediaObject = getItem(getAdapterPosition());

            if (selectedView != null) {
                selectedView.setVisibility(View.GONE);
            }

            if (selectedMedia != null && selectedMedia == mediaObject) {
                selectedMedia = null;
            } else {
                selectedMedia = mediaObject;
                selectedView = rl_selected;
                rl_selected.setVisibility(View.VISIBLE);
            }
        }
    }
}
