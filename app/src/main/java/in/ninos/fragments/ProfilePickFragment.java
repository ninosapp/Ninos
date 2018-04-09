package in.ninos.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.ninos.R;
import in.ninos.activities.ProfileSelectActivity;
import in.ninos.adapters.ProfilePickAdapter;
import in.ninos.models.MediaObject;

/**
 * Created by FAMILY on 04-01-2018.
 */

public class ProfilePickFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int URL_LOADER = 1;
    private final static String BUCKET_NAME = "BUCKET_NAME";
    private ProfileSelectActivity mBaseActivity;
    private View cl_home;
    private ProfilePickAdapter profilePickAdapter;
    private String bucketName;

    public static ProfilePickFragment newInstance(String bucketName) {
        ProfilePickFragment imagePickFragment = new ProfilePickFragment();

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
        return inflater.inflate(R.layout.fragment_image_bucket, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            mBaseActivity = (ProfileSelectActivity) getActivity();

            cl_home = mBaseActivity.findViewById(R.id.cl_home);

            GridLayoutManager layoutManager = new GridLayoutManager(mBaseActivity, 3);

            final RecyclerView recyclerView = view.findViewById(R.id.bucket_list);
            recyclerView.setLayoutManager(layoutManager);

            profilePickAdapter = new ProfilePickAdapter(mBaseActivity);

            recyclerView.setAdapter(profilePickAdapter);

            getLoaderManager().initLoader(URL_LOADER, null, this);

        } catch (Exception e) {
            logError(e);
            showSnackBar(R.string.error_message, cl_home);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == URL_LOADER) {
            try {
                String orderBy = MediaStore.Images.Media.DATE_TAKEN;
                String searchParams = "bucket_display_name = \"" + bucketName + "\"";

                return new CursorLoader(getContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null,
                        searchParams,
                        null,
                        orderBy);
            }catch (Exception e) {
                logError(e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        try {
            if (cursor != null && cursor.moveToFirst()) {

                do {
                    String filePath = cursor.getString(1);
                    int id = cursor.getInt(0);

                    final MediaObject mO = new MediaObject(id, filePath);

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            profilePickAdapter.addItem(mO);
                        }
                    });

                } while (cursor.moveToNext());
            }
        }catch (Exception e) {
            logError(e);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
