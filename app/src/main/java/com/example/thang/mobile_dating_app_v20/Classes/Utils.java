package com.example.thang.mobile_dating_app_v20.Classes;

import android.content.res.AssetFileDescriptor;
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
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Thang on 5/28/2015.
 */
public class Utils {
    private static double DISTANCE_RADIUS = 0.05; // 1000m

    public Utils() {
    }

    public static Bitmap resizeBitmap(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) 200) / width;
        float scaleHeight = ((float) 200) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

        return resizedBitmap;
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

    public static String encodeBitmapToBase64String(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return new String(Base64.encode(byteArray, 1));
    }

    public static Bitmap decodeBase64StringToBitmap(String base64){
        byte[] byteArray = Base64.decode(base64,1);
        return BitmapFactory.decodeByteArray(byteArray , 0, byteArray.length);
    }
}
