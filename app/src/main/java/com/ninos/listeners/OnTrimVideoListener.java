package com.ninos.listeners;

import android.net.Uri;

/**
 * Created by FAMILY on 20-01-2018.
 */

public interface OnTrimVideoListener {

    void onTrimStarted();

    void getResult(final Uri uri);

    void cancelAction();

    void onError(final String message);
}