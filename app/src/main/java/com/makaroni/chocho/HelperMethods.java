package com.makaroni.chocho;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class HelperMethods {
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public static int[] calculateInSampleSize(Bitmap bitmap, int reqWidth, int reqHeight) {
        // Raw height and width of image
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int inSampleSize = 2;

        if (height > reqHeight || width > reqWidth) {

            while (height >= reqHeight && width >= reqWidth ) {
                height = height / 2;
                width = width / 2;
            }

        }
        int [] widthHeightArray = new int[2];
        widthHeightArray[0] = width;
        widthHeightArray[1] = height;
        return widthHeightArray;
    }

    public static Bitmap decodeSampledBitmap(byte [] blob, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeByteArray(blob,0,blob.length,options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(blob,0,blob.length,options);
    }

    public static void setScaledImage(ImageView imageView, final byte[] blob) {
        final ImageView iv = imageView;
        ViewTreeObserver viewTreeObserver = iv.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                iv.getViewTreeObserver().removeOnPreDrawListener(this);
                int imageViewHeight = iv.getMeasuredHeight();
                int imageViewWidth = iv.getMeasuredWidth();
                iv.setImageBitmap(
                        decodeSampledBitmap(blob,imageViewWidth, imageViewHeight));
                return true;
            }
        });
    }
}
