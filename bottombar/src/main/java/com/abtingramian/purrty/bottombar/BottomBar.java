package com.abtingramian.purrty.bottombar;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * This is a custom bottom bar which is similar to a {@link Snackbar} but more customizeable.
 */
public class BottomBar extends BaseTransientBottomBar {

    private final BottomBarContentLayout contentLayout;

    private BottomBar(@NonNull ViewGroup parent, @NonNull View content, @NonNull ContentViewCallback contentViewCallback) {
        super(parent, content, contentViewCallback);
        this.contentLayout = (BottomBarContentLayout) content;
    }

    /**
     * Copied from {@link Snackbar}
     */
    private static ViewGroup findSuitableParent(View view) {
        ViewGroup fallback = null;
        do {
            if (view instanceof CoordinatorLayout) {
                // We've found a CoordinatorLayout, use it
                return (ViewGroup) view;
            } else if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    // If we've hit the decor content view, then we didn't find a CoL in the
                    // hierarchy, so use it.
                    return (ViewGroup) view;
                } else {
                    // It's not the content view but we'll use it as our fallback
                    fallback = (ViewGroup) view;
                }
            }

            if (view != null) {
                // Else, we will loop and crawl up the view hierarchy and try to find a parent
                final ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);

        // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
        return fallback;
    }

    private static boolean isNullOrEmpty(@Nullable String string) {
        return string == null || string.isEmpty();
    }

    @NonNull
    private void setText(@NonNull CharSequence message) {
        final TextView tv = contentLayout.getMessageView();
        tv.setText(message);
    }

    @NonNull
    private void setAction(CharSequence text, final View.OnClickListener listener) {
        final TextView tv = contentLayout.getActionView();

        if (TextUtils.isEmpty(text) || listener == null) {
            tv.setVisibility(View.GONE);
            tv.setOnClickListener(null);
        } else {
            tv.setVisibility(View.VISIBLE);
            tv.setText(text);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(view);
                    // Now dismiss the Snackbar
                    dismiss();
                }
            });
        }
    }

    @NonNull
    private void setActionTextColor(ColorStateList colors) {
        final TextView tv = contentLayout.getActionView();
        tv.setTextColor(colors);
    }

    @NonNull
    private void setActionTextColor(@ColorInt int color) {
        final TextView tv = contentLayout.getActionView();
        tv.setTextColor(color);
    }

    public static class Builder {
        private final Activity activity;
        private @StringRes
        int messageResId = R.string.error;
        private String message;
        private int duration;
        private View view;
        private @IdRes int viewResId = android.R.id.content;
        private Integer backgroundColor;
        private @ColorRes
        int backgroundColorResId = R.color.background;
        private Integer textColor;
        private @ColorRes int textColorResId = R.color.text;
        private View.OnClickListener action;
        private @StringRes int actionTextResId = R.string.error;
        private String actionText;

        public Builder(@NonNull final Activity activity) {
            this.activity = activity;
        }

        public Builder message(@StringRes int messageResId) {
            this.messageResId = messageResId;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder durationShort() {
            this.duration = Snackbar.LENGTH_SHORT;
            return this;
        }

        public Builder durationLong() {
            this.duration = Snackbar.LENGTH_LONG;
            return this;
        }

        public Builder durationIndefinite() {
            this.duration = Snackbar.LENGTH_INDEFINITE;
            return this;
        }

        public Builder view(View view) {
            this.view = view;
            return this;
        }

        public Builder view(@IdRes int viewResId) {
            this.viewResId = viewResId;
            return this;
        }

        public Builder backgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder backgroundColorRes(@ColorRes int backgroundColorResId) {
            this.backgroundColorResId = backgroundColorResId;
            return this;
        }

        public Builder textColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder textColorRes(@ColorRes int textColorResId) {
            this.textColorResId = textColorResId;
            return this;
        }

        public Builder action(View.OnClickListener action) {
            this.action = action;
            return this;
        }

        public Builder actionText(@StringRes int actionTextResId) {
            this.actionTextResId = actionTextResId;
            return this;
        }

        public Builder actionText(String actionText) {
            this.actionText = actionText;
            return this;
        }

        public BottomBar build() {
            if (view == null) {
                view = activity.findViewById(viewResId);
            }
            final ViewGroup parent = findSuitableParent(view);
            if (parent == null) {
                throw new IllegalArgumentException("No suitable parent found from the given view. "
                        + "Please provide a valid view.");
            }

            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final BottomBarContentLayout content =
                    (BottomBarContentLayout) inflater.inflate(R.layout.bottombar_layout, parent, false);
            final BottomBar bottomBar = new BottomBar(parent, content, content);
            bottomBar.setDuration(duration);
            // resolve final resources if necessary
            if (isNullOrEmpty(message)) {
                message = activity.getString(messageResId);
            }
            bottomBar.setText(message);
            if (backgroundColor == null) {
                backgroundColor = ContextCompat.getColor(activity, backgroundColorResId);
            }
            if (textColor == null) {
                textColor = ContextCompat.getColor(activity, textColorResId);
            }
            if (isNullOrEmpty(actionText)) {
                actionText = activity.getString(actionTextResId);
            }
            // set an action string to open the system settings to allow the user to change the permission
            if (action != null && !isNullOrEmpty(actionText)) {
                bottomBar.setAction(actionText, action);
            }
            bottomBar.setActionTextColor(textColor);
            bottomBar.getView().setBackgroundColor(backgroundColor);
            TextView tv = bottomBar.getView().findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(textColor);
            return bottomBar;
        }

    }

}
