package in.ninos.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import in.ninos.R;
import in.ninos.activities.ChallengeActivity;
import in.ninos.listeners.OnLoadMoreListener;
import in.ninos.models.ChallengeInfo;

/**
 * Created by FAMILY on 04-02-2018.
 */

public class ChallengeAdapter extends CommonRecyclerAdapter<ChallengeInfo> {

    private Context context;
    private TypedArray typedArray;

    public ChallengeAdapter(Context context, RecyclerView recyclerView, OnLoadMoreListener onLoadMoreListener) {
        super(recyclerView, onLoadMoreListener);
        this.context = context;
        typedArray = context.getResources().obtainTypedArray(R.array.patterns);
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_challenge, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        ChallengeViewHolder bucketHolder = (ChallengeViewHolder) genericHolder;
        bucketHolder.bindData(position);
    }

    private class ChallengeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_title;
        ImageView iv_challenge;

        ChallengeViewHolder(View view) {
            super(view);
            iv_challenge = view.findViewById(R.id.iv_challenge);
            tv_title = view.findViewById(R.id.tv_title);
            view.setOnClickListener(this);
        }

        void bindData(int position) {
            ChallengeInfo postInfo = getItem(position);
            tv_title.setText(postInfo.getTitle());

            int index = position % 10;
            int resId = typedArray.getResourceId(index, 0);

            iv_challenge.setImageDrawable(ContextCompat.getDrawable(context, resId));

            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(resId)
                    .error(resId)
                    .fallback(resId)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(postInfo.getImageUrl()).into(iv_challenge);
        }

        @Override
        public void onClick(View view) {

            ChallengeInfo postInfo = getItem(getAdapterPosition());

            Intent intent = new Intent(context, ChallengeActivity.class);
            intent.putExtra(ChallengeActivity.CHALLENGE_ID, postInfo.get_id());
            intent.putExtra(ChallengeActivity.CHALLENGE_TITLE, postInfo.getTitle());
            context.startActivity(intent);
        }
    }
}
