package com.ninos.listeners;

/**
 * Created by FAMILY on 20-01-2018.
 */

public interface OnTrimVideoListener {

    void onTrimStarted();

    void getResult(final String url);

    void cancelAction();

    void finished();

    void onError(final String message);
}