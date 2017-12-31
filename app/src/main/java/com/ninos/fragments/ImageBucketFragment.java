package com.ninos.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninos.BaseActivity;
import com.ninos.R;
import com.ninos.adapters.BucketAdapter;
import com.ninos.models.Bucket;

/**
 * Created by FAMILY on 30-12-2017.
 */

public class ImageBucketFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int URL_LOADER = 1;
    private BaseActivity mBaseActivity;
    private View cl_home;
    private BucketAdapter bucketAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_bucket, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            mBaseActivity = (BaseActivity) getActivity();

            cl_home = mBaseActivity.findViewById(R.id.cl_home);

            GridLayoutManager layoutManager = new GridLayoutManager(mBaseActivity, 2);

            final RecyclerView recyclerView = view.findViewById(R.id.bucket_list);
            recyclerView.setLayoutManager(layoutManager);

            bucketAdapter = new BucketAdapter(mBaseActivity, BucketAdapter.Type.IMAGES);

            recyclerView.setAdapter(bucketAdapter);

            getLoaderManager().initLoader(URL_LOADER, null, this);
        } catch (Exception e) {
            logError(e);
            showSnackBar(R.string.error_message, cl_home);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == URL_LOADER) {
            String[] PROJECTION_BUCKET = {MediaStore.Images.ImageColumns.BUCKET_ID,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN,
                    MediaStore.Images.ImageColumns.DATA};

            String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";

            String BUCKET_ORDER_BY = "MAX(datetaken) DESC";

            return new CursorLoader(getContext(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    PROJECTION_BUCKET,
                    BUCKET_GROUP_BY,
                    null,
                    BUCKET_ORDER_BY);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Bucket image;

        if (cursor != null && cursor.moveToFirst()) {

            String bucket;
            String path;
            long bucketId;

            int bucketColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int bucketIdColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);

            do {
                bucket = cursor.getString(bucketColumn);
                path = cursor.getString(dataColumn);
                bucketId = cursor.getInt(bucketIdColumn);

                if (bucket != null && bucket.length() > 0) {

                    image = new Bucket();
                    image.setBucketId(bucketId);
                    image.setBucketName(bucket);
                    image.setPath(path);

                    bucketAdapter.addItem(image);
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
