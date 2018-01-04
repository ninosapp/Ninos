package com.ninos.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.ninos.BaseActivity;
import com.ninos.R;
import com.ninos.models.MediaObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FAMILY on 31-12-2017.
 */

public class ImagePickAdapter extends CommonRecyclerAdapter<MediaObject> {
    private BaseActivity baseActivity;
    private List<String> selectedMedia;
    private ISetImageSelected iSetImageSelected;

    public ImagePickAdapter(BaseActivity baseActivity, ISetImageSelected iSetImageSelected) {
        this.baseActivity = baseActivity;
        this.iSetImageSelected = iSetImageSelected;
        selectedMedia = new ArrayList<>();
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

    public ArrayList<String> getSelectedMedia() {
        ArrayList<String> medias = new ArrayList<>(selectedMedia.size());
        medias.addAll(selectedMedia);
        return medias;
    }

    public interface ISetImageSelected {
        void updateCount(int count);
    }

    private class ImagePickHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_image;
        RelativeLayout rl_selected;

        ImagePickHolder(View view) {
            super(view);
            iv_image = view.findViewById(R.id.iv_image);
            rl_selected = view.findViewById(R.id.rl_selected);

            view.setOnClickListener(this);
        }

        void bindData(MediaObject mediaObject) {
            Glide.with(baseActivity)
                    .load(mediaObject.getPath())
                    .into(iv_image);

            if (selectedMedia.contains(mediaObject.getPath())) {
                rl_selected.setVisibility(View.VISIBLE);
            } else {
                rl_selected.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            MediaObject mediaObject = getItem(getAdapterPosition());

            if (selectedMedia.contains(mediaObject.getPath())) {
                rl_selected.setVisibility(View.GONE);
                selectedMedia.remove(mediaObject.getPath());
            } else {
                selectedMedia.add(mediaObject.getPath());
                rl_selected.setVisibility(View.VISIBLE);
            }

            iSetImageSelected.updateCount(selectedMedia.size());
        }
    }
}
