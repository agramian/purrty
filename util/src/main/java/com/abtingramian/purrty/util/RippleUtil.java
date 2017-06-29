package com.abtingramian.purrty.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.abtingramian.purrty.shapedrawablewithborder.ShapeDrawableWithBorder;

/**
 * Helper class for programmatically setting a {@link RippleDrawable} for a views background.
 */
public class RippleUtil {

    public static void setBackgroundCompat(View view,
                                           int colorMain,
                                           int colorBorder,
                                           int colorRipple,
                                           float cornerRadius,
                                           int borderWidth) {
        setBackgroundCompat(view, null, null, colorMain, colorBorder, colorRipple, cornerRadius, borderWidth, 1.0f);
    }

    public static void setBackgroundCompat(View view,
                                           ShapeDrawableWithBorder shapeDrawable,
                                           int colorMain,
                                           int colorRipple) {
        setBackgroundCompat(view, shapeDrawable, null, colorMain, colorMain, colorRipple, -1, -1, 1.0f);
    }

    public static void setBackgroundCompat(View view,
                                           LayerDrawable layerDrawable,
                                           int colorMain,
                                           int colorRipple) {
        setBackgroundCompat(view, null, layerDrawable, colorMain, colorMain, colorRipple, -1, -1, 1.0f);
    }

    public static void setBackgroundCompat(View view,
                                           ShapeDrawableWithBorder shapeDrawable,
                                           LayerDrawable layerDrawable,
                                           int colorMain,
                                           int colorBorder,
                                           int colorRipple,
                                           float cornerRadius,
                                           int borderWidth,
                                           float alpha) {
        Drawable drawable = getBackgroundCompat(view.getContext(), shapeDrawable, layerDrawable, colorMain, colorBorder, colorRipple, cornerRadius, borderWidth, alpha);
        ViewCompat.setBackground(view, drawable);
    }

    private static Drawable getBackgroundCompat(Context context,
                                                ShapeDrawableWithBorder shapeDrawable,
                                                LayerDrawable layerDrawable,
                                                int colorMain,
                                                int colorBorder,
                                                int colorRipple,
                                                float cornerRadius,
                                                int borderWidth,
                                                float alpha) {
        // if ripple color was not set, use black or white depending on color brightness
        if (colorRipple == -1) {
            colorRipple = ContextCompat.getColor(context, ColorUtil.isBright(colorMain) ? R.color.ripple_dark : R.color.ripple_light);
        }
        // apply alpha if necessary
        if (alpha != 1.0f) {
            colorMain = ColorUtil.adjustAlpha(colorMain, alpha);
            colorBorder = ColorUtil.adjustAlpha(colorBorder, alpha);
            colorRipple = ColorUtil.adjustAlpha(colorRipple, alpha);
        }
        // create base drawable
        final Drawable backgroundDrawable;
        if (shapeDrawable != null) {
            backgroundDrawable = shapeDrawable;
            shapeDrawable.getPaint().setColor(colorMain);
            if (borderWidth != -1) {
                shapeDrawable.setStrokeWidth(borderWidth);
                shapeDrawable.setStrokeColor(colorBorder);
            }
        } else if (layerDrawable != null) {
            backgroundDrawable = layerDrawable;
            ShapeDrawableWithBorder shapeDrawableWithBorder = ((ShapeDrawableWithBorder) layerDrawable.getDrawable(1));
            shapeDrawableWithBorder.getPaint().setColor(colorMain);
            if (borderWidth != -1) {
                shapeDrawableWithBorder.setStrokeWidth(borderWidth);
                shapeDrawableWithBorder.setStrokeColor(colorBorder);
            }
        } else {
            backgroundDrawable = new GradientDrawable();
            ((GradientDrawable) backgroundDrawable).setShape(GradientDrawable.RECTANGLE);
            if (cornerRadius != -1) {
                ((GradientDrawable) backgroundDrawable).setCornerRadius(cornerRadius);
            }
            ((GradientDrawable) backgroundDrawable).setColor(colorMain);
            if (borderWidth != -1) {
                ((GradientDrawable) backgroundDrawable).setStroke(borderWidth, colorBorder);
            }
        }
        // if ripple available return ripple drawable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new RippleDrawable(ColorStateList.valueOf(colorRipple), backgroundDrawable, null);
        }
        // else create state list drawable and return
        // create a copy of the normal state drawable and adjust for highlighted state
        final Drawable backgroundDrawableHighlighted = DrawableCompat.wrap(backgroundDrawable.getConstantState().newDrawable());
        if (backgroundDrawableHighlighted instanceof ShapeDrawable) {
            //noinspection ResourceAsColor
            ((ShapeDrawable) backgroundDrawableHighlighted.mutate()).getPaint().setColor(ColorUtil.isBright(colorMain)
                    ? ColorUtil.darkenColorByAmount(colorMain, Float.parseFloat(context.getResources().getString(R.string.ripple_pressed_lighten_darken_amount)), true)
                    : ColorUtil.lightenColorByAmount(colorMain, Float.parseFloat(context.getResources().getString(R.string.ripple_pressed_lighten_darken_amount)), true));
        } else if (backgroundDrawableHighlighted instanceof GradientDrawable) {
            //noinspection ResourceAsColor
            ((GradientDrawable) backgroundDrawableHighlighted.mutate()).setColor(ColorUtil.isBright(colorMain)
                    ? ColorUtil.darkenColorByAmount(colorMain, Float.parseFloat(context.getResources().getString(R.string.ripple_pressed_lighten_darken_amount)), true)
                    : ColorUtil.lightenColorByAmount(colorMain, Float.parseFloat(context.getResources().getString(R.string.ripple_pressed_lighten_darken_amount)), true));
        }
        // create a states with both drawables
        final StateListDrawable states = new StateListDrawable();
        states.addState(new int[] {android.R.attr.state_pressed}, backgroundDrawableHighlighted);
        states.addState(new int[] {}, backgroundDrawable);
        return states;
    }

}
