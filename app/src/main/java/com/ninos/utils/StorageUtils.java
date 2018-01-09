package com.ninos.utils;

import android.content.Context;
import android.os.Environment;

import com.ninos.R;
import com.ninos.firebase.Database;
import com.ninos.videoTrimmer.utils.FileUtils;

import java.io.File;

/**
 * Created by FAMILY on 09-01-2018.
 */

public class StorageUtils {
    private static String getExternalAppStoragePath(Context context) {
        return String.format("%s/%s", Environment.getExternalStorageDirectory(), context.getResources().getString(R.string.app_name));
    }

    public static String getAttachmentsPath(Context context) {
        return String.format("%s/%s/%s", getExternalAppStoragePath(context), "Attachments", Database.getUserId());
    }

    public static String getAttachmentsPath(Context context, String postId) {
        String attachmentDir = String.format("%s/%s", getAttachmentsPath(context), postId);
        FileUtils.createDir(attachmentDir);
        FileUtils.createFileInDir(attachmentDir, ".nomedia");
        return attachmentDir;
    }

    public static String getAttachmentPath(Context context, String taskId, String attachmentName) {
        return String.format("%s/%s", getAttachmentsPath(context, taskId), attachmentName);
    }

    public static long getAttachDirSize(Context context) {
        File file = new File(getAttachmentsPath(context));
        return getAttachDirSize(file);
    }

    private static long getAttachDirSize(File directory) {

        long size = 0;

        if (directory.isDirectory()) {

            for (File file : directory.listFiles()) {

                size += getAttachDirSize(file);
            }
        } else {
            size = directory.length();
        }

        return size;
    }
}
