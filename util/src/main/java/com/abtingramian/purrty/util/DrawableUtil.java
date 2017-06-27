package com.abtingramian.purrty.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * Helper class for programmatically controlling drawables.
 */
public class DrawableUtil {

    public static Drawable tint(final Drawable drawable, @ColorInt int color) {
        if (drawable != null) {
            final Drawable wrapped = DrawableCompat.wrap(drawable);
            drawable.mutate();
            DrawableCompat.setTint(wrapped, color);
        }
        return drawable;
    }

    public static VectorDrawableCompat createVectorDrawable(@NonNull Context context, int drawableResId) {
        return VectorDrawableCompat.create(context.getResources(), drawableResId, null);
    }

}
