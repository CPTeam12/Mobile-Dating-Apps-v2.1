package com.example.thang.mobile_dating_app_v20.Classes;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Thang on 5/28/2015.
 */
public class Utils {
    private static double DISTANCE_RADIUS = 0.05; // 1000m

    public Utils() {
    }

    public boolean isNearLocation(Person user,Person friend){
        if(Math.sqrt(Math.pow(user.getLatitude() - friend.getLatitude(),2) + Math.pow(user.getLongitude() - friend.getLongitude(),2)) < DISTANCE_RADIUS){
            return true;
        }
        return false;
    }

    public Bitmap getCircleBitmap(Bitmap bitmap) {

        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
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
}
