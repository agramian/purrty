package com.abtingramian.purtee.util;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Helper class for getting display-related info.
 */
public class DisplayUtil {

    public static int getDisplayDensityDp(@NonNull Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (metrics.density * 160f);
    }

    public static Display getDisplay(@NonNull Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay();
    }

    public static int getLargestDimensionSize(@NonNull Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        return Math.max(point.x, point.y);
    }

}
