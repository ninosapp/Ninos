package in.ninos.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import in.ninos.R;
import in.ninos.utils.CrashUtil;

/**
 * Created by FAMILY on 03-01-2018.
 */

public class UploadAdapter extends CommonRecyclerAdapter<String> {

    private Context mContext;

    public UploadAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_upload, parent, false);
        return new UploadViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        UploadViewHolder sampleViewHolder = (UploadViewHolder) genericHolder;

        sampleViewHolder.bindData(position);
    }

    private class UploadViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;

        UploadViewHolder(View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.iv_image);
        }

        private void bindData(int position) {
            try {
                String path = getItem(position);
                Glide.with(mContext).load(path).into(iv_image);
            } catch (Exception e) {
                CrashUtil.report(e);
            }
        }
    }
}
