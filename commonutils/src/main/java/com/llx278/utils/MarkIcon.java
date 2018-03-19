package com.llx278.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;

/**
 * Created by llx on 15-11-11.
 */
public class MarkIcon {

    private static int brodeWidth = 2;
    private static float center = 5;
    private static Paint mPaint = new Paint();

    private static int i = 0;
    /**
     * 生成ICon图标,默认 19px x 19px
     * @param context
     * @param resId
     * @param titleStr
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static Bitmap getMarkerBitmap1(Context context, int resId, String titleStr,int reqWidth,int reqHeight) {

        String title = titleStr;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId,options);
        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), resId,options);
        int iconWidth = icon.getWidth();
        int iconHeight = icon.getHeight();

        int offset = iconHeight / 10;

        mPaint.setTextSize(iconHeight);
        int textWidth = (int) mPaint.measureText(title);

        // 计算图标摆放在地图上的基准点
        int a = iconWidth/2;
        float x = (float)a / (float) (iconWidth + textWidth);

        // 用Canvas直接画出来
        // 未选择的
        Bitmap unSelectBitmap = Bitmap.createBitmap(iconWidth + textWidth, iconHeight, Bitmap.Config.ARGB_4444);
        // 生成title
        Canvas canvas = new Canvas(unSelectBitmap);
        //canvas.drawColor(Color.parseColor("#ccffcc"));
        canvas.drawBitmap(icon, 0, 0, mPaint);
        mPaint.setColor(Color.BLACK);
        RectF r = new RectF(iconWidth, 0, iconWidth + textWidth, iconHeight);
        canvas.drawRoundRect(r, center, center, mPaint);
        mPaint.setColor(Color.YELLOW);
        RectF r1 = new RectF(iconWidth + brodeWidth, brodeWidth, iconWidth + textWidth - brodeWidth, iconHeight - brodeWidth);
        canvas.drawRoundRect(r1, center, center, mPaint);
        mPaint.setColor(Color.BLACK);
        canvas.drawText(title, iconWidth, iconHeight - offset, mPaint);
        //Logger.e("bit map size : " + unSelectBitmap.getAllocationByteCount() / 1024);
        return unSelectBitmap;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

            long totalPixels = width * height / inSampleSize;

            final long totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        return inSampleSize;
    }
}
