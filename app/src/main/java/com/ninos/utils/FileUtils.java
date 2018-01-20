package com.ninos.utils;

import java.io.File;

import static com.ninos.utils.CrashUtil.report;

/**
 * Created by FAMILY on 20-01-2018.
 */

public class FileUtils {
    public static boolean createDir(String dirPath) {
        boolean isDirExist = false;

        try {

            File dir = new File(dirPath);
            isDirExist = dir.exists();

            if (!dir.exists()) {
                isDirExist = dir.mkdirs();
            }
        } catch (Exception e) {
            report(e);
        }

        return isDirExist;
    }

    public static void createFileInDir(String dirPath, String fileName) {

        try {
            new File(dirPath, fileName).createNewFile();
        } catch (Exception e) {
            report(e);
        }
    }
}
