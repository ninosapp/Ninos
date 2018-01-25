package com.ninos.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.ninos.listeners.OnTrimVideoListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by FAMILY on 20-01-2018.
 */

public class TrimVideoUtils {

    private static final String TAG = TrimVideoUtils.class.getSimpleName();

    public static void startTrim(Context context, @NonNull String inputFile, @NonNull String outputFile, @NonNull final OnTrimVideoListener callback) throws IOException {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        final String fileName = "MP4_" + timeStamp + ".mp4";

        String start = convertSecondsToTime(0);
        String duration = convertSecondsToTime(60000 / 1000);
        final String destination = outputFile + "/" + fileName;

        String cmd = "-ss " + start + " -t " + duration + " -i " + inputFile + " -vcodec copy -acodec copy " + destination;
        String[] command = cmd.split(" ");
        try {
            FFmpeg.getInstance(context).execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    callback.onError(s);
                }

                @Override
                public void onSuccess(String s) {
                    callback.getResult(destination);
                }

                @Override
                public void onStart() {
                    callback.onTrimStarted();
                }

                @Override
                public void onFinish() {
                    callback.finished();
                }
            });
        } catch (Exception e) {

        }
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    private static String convertSecondsToTime(long seconds) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (seconds <= 0)
            return "00:00";
        else {
            minute = (int) seconds / 60;
            if (minute < 60) {
                second = (int) seconds % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = (int) (seconds - hour * 3600 - minute * 60);
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }
}