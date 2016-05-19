package com.example.micke.lions.indoor;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by iSirux on 2016-04-28.
 */
public class BitmapLoading {

    private Context mContext;
    private int displayWidth, displayHeight;
    private static final int BITMAP_SIZE = 256;

    public BitmapLoading(Context context, int displayWidth, int displayHeight) {
        mContext = context;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
    }

    public Bitmap getFloorImage(int resId) {
        Log.d("display", "w: " + displayWidth + " h: " + displayHeight);
        return decodeSampledBitmapFromResource(mContext.getResources(), resId, displayWidth, displayHeight);
    }

    private static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                          int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);


        // Calculate inSampleSize
        //temp width & height
        reqHeight = BITMAP_SIZE;
        reqWidth = BITMAP_SIZE;
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}