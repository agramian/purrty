package com.abtingramian.purrty.overlay;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * This is a custom view which draws a full screen overlay.
 */
public class Overlay extends View {

    private static final @ColorInt int DEFAULT_COLOR = Color.TRANSPARENT;
    private @ColorInt int color = DEFAULT_COLOR;

    public Overlay(Context context) {
        super(context);
        setBackground();
    }

    public Overlay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        final TypedArray customAttrs = context.obtainStyledAttributes(attrs, R.styleable.Overlay);
        try {
            color = customAttrs.getColor(R.styleable.Overlay_overlay_color, Color.TRANSPARENT);
        } finally {
            customAttrs.recycle();
        }
        setBackground();
    }

    public void show(@NonNull final Activity activity) {
        setClickable(true);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setBackground();
        ((ViewGroup) activity.getWindow().getDecorView()).addView(this);
    }

    public void hide() {
        ((ViewGroup) getParent()).removeView(this);
    }

    public void setColor(@ColorInt final int color) {
        this.color = color;
    }

    public void setColorRes(@ColorRes final int colorResId) {
        this.color = ContextCompat.getColor(getContext(), colorResId);
    }

    private void setBackground() {
        ViewCompat.setBackground(this, new ColorDrawable(color));
    }

    public static class Builder {
        private final Context context;
        private @ColorInt int color = DEFAULT_COLOR;

        public Builder(@NonNull final Context context) {
            this.context = context;
        }

        public Builder color(final int color) {
            this.color = color;
            return this;
        }

        public Builder colorRes(@ColorRes final int colorResId) {
            this.color = ContextCompat.getColor(context, colorResId);
            return this;
        }

        public Overlay build() {
            final Overlay overlay = new Overlay(context);
            overlay.setColor(color);
            return overlay;
        }
    }

}
