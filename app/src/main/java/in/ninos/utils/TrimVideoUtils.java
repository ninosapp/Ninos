package in.ninos.utils;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import in.ninos.listeners.OnTrimVideoListener;
import processing.ffmpeg.videokit.AsyncCommandExecutor;
import processing.ffmpeg.videokit.Command;
import processing.ffmpeg.videokit.ProcessingListener;
import processing.ffmpeg.videokit.VideoKit;

/**
 * Created by FAMILY on 20-01-2018.
 */

class TrimVideoUtils {

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

    void startTrim(@NonNull String inputFile, final @NonNull String outputFile, @NonNull final OnTrimVideoListener onTrimVideoListener) throws IOException {
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
                    .limitVideoBitrate("1M")
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


}