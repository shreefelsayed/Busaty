package com.armjld.busaty.Utill;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.armjld.busaty.R;

public class BitMapMaker {
    public Bitmap createImage(int width, int height, int color, String name) {
        Bitmap bitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint2 = new Paint();
        paint2.setColor(color);
        canvas.drawRect(0F, 0F, (float) 150, (float) 150, paint2);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(72);
        paint.setTextScaleX(1);
        canvas.drawText(name, 75 - 25, 75 + 20, paint);
        return bitmap;
    }

    public Bitmap createImageRounded(Context context, int width, int height, String name) {
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paintCicle = new Paint();
        Paint paintText = new Paint();

        Rect rect = new Rect(0, 0, width, height);
        RectF rectF = new RectF(rect);
        float density = context.getResources().getDisplayMetrics().density;
        float roundPx = 100*density;

        paintCicle.setColor(context.getResources().getColor(R.color.colorPrimary));
        paintCicle.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);

        paintCicle.setStyle(Paint.Style.STROKE);
        paintCicle.setStrokeWidth(4.0f);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paintCicle);

        paintText.setColor(Color.GRAY);
        paintText.setTextSize(60);

        canvas.drawText(name, 75 - 23, 75 + 25, paintText);

        return output;
    }
}
