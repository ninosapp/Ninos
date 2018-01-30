package com.ninos.listeners;

/**
 * Created by FAMILY on 20-01-2018.
 */

public interface OnTrimVideoListener {


    void getResult(final String url);

    void onError(final int message);
}