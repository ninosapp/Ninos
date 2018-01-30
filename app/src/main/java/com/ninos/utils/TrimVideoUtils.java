package com.ninos.utils;

import android.support.annotation.NonNull;

import com.ninos.listeners.OnTrimVideoListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import processing.ffmpeg.videokit.AsyncCommandExecutor;
import processing.ffmpeg.videokit.Command;
import processing.ffmpeg.videokit.ProcessingListener;
import processing.ffmpeg.videokit.VideoKit;

/**
 * Created by FAMILY on 20-01-2018.
 */

public class TrimVideoUtils {

    private final String TAG = TrimVideoUtils.class.getSimpleName();

    private static String unitFormat(int i) {
        String retStr;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    private static String convertSecondsToTime(long seconds) {
        String timeStr;
        int hour;
        int minute;
        int second;
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

    public void startTrim(@NonNull String inputFile, final @NonNull String outputFile, @NonNull final OnTrimVideoListener onTrimVideoListener) throws IOException {
        try {
            final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            final String fileName = "MP4_" + timeStamp + ".mp4";

            final String destination = outputFile + "/" + fileName;

            String start = convertSecondsToTime(0);
            String duration = convertSecondsToTime(60000 / 1000);

            final VideoKit videoKit = new VideoKit();
            final Command command = videoKit.createCommand()
                    .overwriteOutput()
                    .inputPath(inputFile)
                    .outputPath(destination)
                    .customCommand("-ss " + start + " -t " + duration)
                    .copyVideoCodec()
                    .experimentalFlag()
                    .build();

            new AsyncCommandExecutor(command, new ProcessingListener() {
                @Override
                public void onSuccess(String path) {
                    onTrimVideoListener.getResult(path);
                }

                @Override
                public void onFailure(int returnCode) {
                    onTrimVideoListener.onError(returnCode);
                }
            }).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void loadFFmpeg(Context context) {
//        ffmpeg = FFmpeg.getInstance(context);
//        try {
//            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
//
//                @Override
//                public void onStart() {
//                }
//
//                @Override
//                public void onFailure() {
//                }
//
//                @Override
//                public void onSuccess() {
//                }
//
//                @Override
//                public void onFinish() {
//                }
//            });
//        } catch (FFmpegNotSupportedException e) {
//            Toast.makeText(context, R.string.device_not_supported, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public static void startTrim(Context context, @NonNull String inputFile, @NonNull String outputFile, @NonNull final OnTrimVideoListener callback) throws IOException {
//        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
//        final String fileName = "MP4_" + timeStamp + ".mp4";
//
//        String start = convertSecondsToTime(0);
//        String duration = convertSecondsToTime(60000 / 1000);
//        final String destination = outputFile + "/" + fileName;
//
//        String cmd = "-ss " + start + " -t " + duration + " -i " + inputFile + " -vcodec copy -acodec copy " + destination;
//        String[] command = cmd.split(" ");
//
//        try {
//            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
//                @Override
//                public void onFailure(String s) {
//                        callback.onError(s);
//                }
//
//                @Override
//                public void onSuccess(String s) {
//                    callback.getResult(destination);
//                }
//
//                @Override
//                public void onStart() {
//                    callback.onTrimStarted();
//                }
//
//                @Override
//                public void onFinish() {
//                    callback.finished();
//                }
//            });
//        } catch (Exception e) {
//
//        }
//    }
}