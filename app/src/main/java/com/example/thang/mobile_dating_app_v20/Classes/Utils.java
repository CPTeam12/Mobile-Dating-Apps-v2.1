package com.example.thang.mobile_dating_app_v20.Classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Thang on 5/28/2015.
 */
public class Utils {
    private static double DISTANCE_RADIUS = 0.05; // 1000m

    public Utils() {
    }

    public static Bitmap resizeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap result;
        float scaleWidth = ((float) 200) / width;
        float scaleHeight = ((float) 200) / height;
        Matrix matrix = new Matrix();
        if (width > height) {
            result = Bitmap.createBitmap(bitmap, width / 2 - height / 2, 0, height, height, matrix, false);
        } else {
            result = Bitmap.createBitmap(bitmap, 0, height / 2 - width / 2, width, width, matrix, false);
        }
        return result;
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        bitmap = resizeBitmap(bitmap);
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888); //RGB_565
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        paint.setStrokeWidth(3);
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return Bitmap.createScaledBitmap(output, 120, 120, false);
    }

    public static String encodeBitmapToBase64String(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 30, stream);
        byte[] byteArray = stream.toByteArray();
        return new String(Base64.encode(byteArray, 1));
    }

    public static Bitmap decodeBase64StringToBitmap(String base64) {
        byte[] byteArray = Base64.decode(base64, 1);
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    public static Bitmap generateSmaleBitmap(String base64, int reqWidth, int reqHeight) {
        byte[] byteArray = Base64.decode(base64, 1);

        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);

    }

    public static int calculateInSampleSize(
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
