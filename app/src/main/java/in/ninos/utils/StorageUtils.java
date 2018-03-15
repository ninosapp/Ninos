package in.ninos.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import in.ninos.R;
import in.ninos.firebase.Database;


/**
 * Created by FAMILY on 09-01-2018.
 */

public class StorageUtils {
    private static String getExternalAppStoragePath(Context context) {
        return String.format("%s/%s", Environment.getExternalStorageDirectory(), context.getResources().getString(R.string.app_name));
    }

    public static String getPostPath(Context context) {
        return String.format("%s/%s", getExternalAppStoragePath(context), Database.getUserId());
    }

    public static String getPostPath(Context context, String postId) {
        String attachmentDir = String.format("%s/%s", getPostPath(context), postId);
        FileUtils.createDir(attachmentDir);
        FileUtils.createFileInDir(attachmentDir, ".nomedia");
        return attachmentDir;
    }

    public static String getUserImagePath(Context context) {
        String attachmentDir = getPostPath(context);
        FileUtils.createDir(attachmentDir);
        FileUtils.createFileInDir(attachmentDir, ".nomedia");
        return attachmentDir;
    }

    public static String getPostPath(Context context, String postId, String attachmentName) {
        return String.format("%s/%s", getPostPath(context, postId), attachmentName);
    }

    public static long getPostDirSize(Context context) {
        File file = new File(getPostPath(context));
        return getPostDirSize(file);
    }

    private static long getPostDirSize(File directory) {

        long size = 0;

        if (directory.isDirectory()) {

            for (File file : directory.listFiles()) {

                size += getPostDirSize(file);
            }
        } else {
            size = directory.length();
        }

        return size;
    }
}
