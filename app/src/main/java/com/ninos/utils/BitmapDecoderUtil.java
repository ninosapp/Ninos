package com.ninos.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by FAMILY on 30-12-2017.
 */

public class BitmapDecoderUtil {

    public static Bitmap decodeBitmapFromFile(String imagePath) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        options.inSampleSize = calculateSampleSize(options);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }


    public static int calculateSampleSize(BitmapFactory.Options options) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        int maxSize = 120;

        if (height > maxSize || width > maxSize) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > maxSize
                    && (halfWidth / inSampleSize) > maxSize) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;

    }
}
