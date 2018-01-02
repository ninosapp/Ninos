package com.ninos.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ninos.R;
import com.ninos.activities.FilePickerActivity;
import com.ninos.adapters.ImagePickAdapter;
import com.ninos.models.MediaObject;

/**
 * Created by FAMILY on 29-12-2017.
 */

public class ImagePickFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, ImagePickAdapter.ISetImageSelected {

    private static final int URL_LOADER = 1;
    private final static String BUCKET_NAME = "BUCKET_NAME";
    private FilePickerActivity mBaseActivity;
    private View cl_home;
    private ImagePickAdapter imagePickAdapter;
    private String bucketName;
    private TextView tv_select_count;

    public static ImagePickFragment newInstance(String bucketName) {
        ImagePickFragment imagePickFragment = new ImagePickFragment();

        Bundle bundle = new Bundle();
        bundle.putString(BUCKET_NAME, bucketName);
        imagePickFragment.setArguments(bundle);

        return imagePickFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            bucketName = getArguments().getString(BUCKET_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_pick, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            mBaseActivity = (FilePickerActivity) getActivity();

            cl_home = mBaseActivity.findViewById(R.id.cl_home);

            Toolbar toolbar_image_pick = view.findViewById(R.id.toolbar_image_pick);
            toolbar_image_pick.setTitle(R.string.app_name);
            toolbar_image_pick.setTitleTextColor(ContextCompat.getColor(mBaseActivity, R.color.colorAccent));
            mBaseActivity.setSupportActionBar(toolbar_image_pick);

            ActionBar actionBar = mBaseActivity.getSupportActionBar();

            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(mBaseActivity, R.drawable.ic_back));
            }

            GridLayoutManager layoutManager = new GridLayoutManager(mBaseActivity, 3);

            final RecyclerView recyclerView = view.findViewById(R.id.bucket_list);
            recyclerView.setLayoutManager(layoutManager);

            imagePickAdapter = new ImagePickAdapter(mBaseActivity, this);

            recyclerView.setAdapter(imagePickAdapter);

            getLoaderManager().initLoader(URL_LOADER, null, this);

            view.findViewById(R.id.tv_cancel).setOnClickListener(this);
            tv_select_count = view.findViewById(R.id.tv_select_count);
            tv_select_count.setText(String.format(getString(R.string.select_count), 0));
            tv_select_count.setOnClickListener(this);
        } catch (Exception e) {
            logError(e);
            showSnackBar(R.string.error_message, cl_home);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == URL_LOADER) {
            String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            String searchParams = "bucket_display_name = \"" + bucketName + "\"";

            return new CursorLoader(getContext(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    searchParams,
                    null,
                    orderBy);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {

            do {
                String filePath = cursor.getString(1);
                int id = cursor.getInt(0);

                final MediaObject mO = new MediaObject(id, filePath);

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        imagePickAdapter.addItem(mO);
                    }
                });

            } while (cursor.moveToNext());

            cursor.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                mBaseActivity.onBackPressed();
                break;
            case R.id.tv_select_count:
                mBaseActivity.setSelectedImages(imagePickAdapter.getSelectedMedia());
                break;
        }
    }

    @Override
    public void updateCount(int count) {
        tv_select_count.setText(String.format(getString(R.string.select_count), count));
    }
}