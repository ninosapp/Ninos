package in.ninos.fragments;

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

import in.ninos.R;
import in.ninos.activities.FilePickerActivity;
import in.ninos.adapters.VideoPickAdapter;
import in.ninos.models.MediaObject;

/**
 * Created by FAMILY on 01-01-2018.
 */

public class VideoPickFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private static final int URL_LOADER = 1;
    private final static String BUCKET_NAME = "BUCKET_NAME";
    private FilePickerActivity mBaseActivity;
    private View cl_home;
    private VideoPickAdapter videoPickAdapter;
    private String bucketName;
    private TextView tv_select_count;

    public static VideoPickFragment newInstance(String bucketName) {
        VideoPickFragment videoPickFragment = new VideoPickFragment();

        Bundle bundle = new Bundle();
        bundle.putString(BUCKET_NAME, bucketName);
        videoPickFragment.setArguments(bundle);

        return videoPickFragment;
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

            GridLayoutManager layoutManager = new GridLayoutManager(mBaseActivity, 3);

            final RecyclerView recyclerView = view.findViewById(R.id.bucket_list);
            recyclerView.setLayoutManager(layoutManager);

            videoPickAdapter = new VideoPickAdapter(mBaseActivity);

            recyclerView.setAdapter(videoPickAdapter);

            getLoaderManager().initLoader(URL_LOADER, null, this);

            view.findViewById(R.id.tv_cancel).setOnClickListener(this);
            tv_select_count = view.findViewById(R.id.tv_select_count);
            tv_select_count.setOnClickListener(this);
        } catch (Exception e) {
            logError(e);
            showSnackBar(R.string.error_message, cl_home);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == URL_LOADER) {
            try {
                String orderBy = MediaStore.Video.Media.DATE_TAKEN;
                String searchParams = "bucket_display_name = \"" + bucketName + "\"";

                return new CursorLoader(getContext(),
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        null,
                        searchParams,
                        null,
                        orderBy);
            } catch (Exception e) {
                logError(e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        try {
            if (cursor != null && cursor.moveToFirst()) {

                do {
                    String filePath = cursor.getString(1);
                    int id = cursor.getInt(0);

                    final MediaObject mO = new MediaObject(id, filePath);

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            videoPickAdapter.addItem(mO);
                        }
                    });

                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            logError(e);
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
        }
    }
}