package com.abtingramian.purrty.bottombar;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.RestrictTo;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;


/**
 * @hide
 */
@RestrictTo(LIBRARY_GROUP)
public class BottomBarContentLayout extends LinearLayout implements BaseTransientBottomBar.ContentViewCallback {
    private ImageView imageView;
    private TextView messageView;
    private Button actionView;

    private int maxWidth;
    private int maxInlineActionWidth;

    public BottomBarContentLayout(Context context) {
        this(context, null);
    }

    public BottomBarContentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, android.support.design.R.styleable.SnackbarLayout);
        maxWidth = a.getDimensionPixelSize(R.styleable.BottomBarContentLayout_android_maxWidth, -1);
        maxInlineActionWidth = a.getDimensionPixelSize(R.styleable.BottomBarContentLayout_maxActionInlineWidth, -1);
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        imageView = findViewById(R.id.bottombar_image);
        messageView = findViewById(R.id.bottombar_text);
        actionView = findViewById(R.id.bottombar_action);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getMessageView() {
        return messageView;
    }

    public Button getActionView() {
        return actionView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (maxWidth > 0 && getMeasuredWidth() > maxWidth) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        final int multiLineVPadding = getResources().getDimensionPixelSize(R.dimen.bottombar_padding_vertical_2lines);
        final int singleLineVPadding = getResources().getDimensionPixelSize(R.dimen.bottombar_padding_vertical);
        final boolean isMultiLine = messageView.getLayout().getLineCount() > 1;

        boolean remeasure = false;
        if (isMultiLine && maxInlineActionWidth > 0
                && actionView.getMeasuredWidth() > maxInlineActionWidth) {
            if (updateViewsWithinLayout(VERTICAL, multiLineVPadding,
                    multiLineVPadding - singleLineVPadding)) {
                remeasure = true;
            }
        } else {
            final int messagePadding = isMultiLine ? multiLineVPadding : singleLineVPadding;
            if (updateViewsWithinLayout(HORIZONTAL, messagePadding, messagePadding)) {
                remeasure = true;
            }
        }

        if (remeasure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private boolean updateViewsWithinLayout(final int orientation,
                                            final int messagePadTop, final int messagePadBottom) {
        boolean changed = false;
        if (orientation != getOrientation()) {
            setOrientation(orientation);
            changed = true;
        }
        if (messageView.getPaddingTop() != messagePadTop
                || messageView.getPaddingBottom() != messagePadBottom) {
            updateTopBottomPadding(messageView, messagePadTop, messagePadBottom);
            changed = true;
        }
        return changed;
    }

    private static void updateTopBottomPadding(View view, int topPadding, int bottomPadding) {
        if (ViewCompat.isPaddingRelative(view)) {
            ViewCompat.setPaddingRelative(view,
                    ViewCompat.getPaddingStart(view), topPadding,
                    ViewCompat.getPaddingEnd(view), bottomPadding);
        } else {
            view.setPadding(view.getPaddingLeft(), topPadding,
                    view.getPaddingRight(), bottomPadding);
        }
    }

    @Override
    public void animateContentIn(int delay, int duration) {
        ViewCompat.setAlpha(messageView, 0f);
        ViewCompat.animate(messageView).alpha(1f).setDuration(duration)
                .setStartDelay(delay).start();

        if (actionView.getVisibility() == VISIBLE) {
            ViewCompat.setAlpha(actionView, 0f);
            ViewCompat.animate(actionView).alpha(1f).setDuration(duration)
                    .setStartDelay(delay).start();
        }
    }

    @Override
    public void animateContentOut(int delay, int duration) {
        ViewCompat.setAlpha(messageView, 1f);
        ViewCompat.animate(messageView).alpha(0f).setDuration(duration)
                .setStartDelay(delay).start();

        if (actionView.getVisibility() == VISIBLE) {
            ViewCompat.setAlpha(actionView, 1f);
            ViewCompat.animate(actionView).alpha(0f).setDuration(duration)
                    .setStartDelay(delay).start();
        }
    }
}
