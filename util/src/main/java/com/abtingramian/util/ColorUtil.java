package com.abtingramian.util;

import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;

public class ColorUtil {

    public static int applyAlpha(int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public static int stripAlpha(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.rgb(red, green, blue);
    }

    public static boolean isBright(int color) {
        return ColorUtils.calculateLuminance(color) > 0.5d;
    }

    public static int darken(int color, float factor) {
        int a = Color.alpha( color );
        int r = Color.red( color );
        int g = Color.green( color );
        int b = Color.blue( color );

        return Color.argb( a,
                Math.max( (int)(r * (1-factor)), 0 ),
                Math.max( (int)(g * (1-factor)), 0 ),
                Math.max( (int)(b * (1-factor)), 0 ) );
    }

    public static int percentTo255Scale(int percent) {
        if (percent >= 100) {
            return 255;
        } else {
            double factor = percent / 100.0;
            return (int) (255 * factor);
        }
    }

    public static int lightenColorByAmount(int color, float amount) {
        int alpha = (int)Math.min(Color.alpha(color) + amount, 255);
        int red = (int)Math.min(Color.red(color) + amount, 255);
        int green = (int)Math.min(Color.green(color) + amount, 255);
        int blue = (int)Math.min(Color.blue(color) + amount, 255);
        return Color.argb(alpha, red, green, blue);
    }

    public static int lightenColorByAmount(int color, float amount, boolean zeroToOneScale) {
        return lightenColorByAmount(color, zeroToOneScale ? 255 * amount : amount);
    }

    public static int darkenColorByAmount(int color, float amount) {
        int alpha = (int)Math.max(Color.alpha(color) - amount, 0);
        int red = (int)Math.max(Color.red(color) - amount, 0);
        int green = (int)Math.max(Color.green(color) - amount, 0);
        int blue = (int)Math.max(Color.blue(color) - amount, 0);
        return Color.argb(alpha, red, green, blue);
    }

    public static int darkenColorByAmount(int color, float amount, boolean zeroToOneScale) {
        return darkenColorByAmount(color, zeroToOneScale ? 255 * amount : amount);
    }

}
