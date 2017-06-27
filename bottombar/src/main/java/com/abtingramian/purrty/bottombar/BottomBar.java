package com.abtingramian.purrty.bottombar;

import android.support.annotation.NonNull;
import android.support.design.internal.SnackbarContentLayout;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

/**
 * This is a custom bottom bar which is similar to a {@link Snackbar} but more customizeable.
 */
public class BottomBar extends BaseTransientBottomBar {

    private BottomBar(@NonNull ViewGroup parent, @NonNull View content, @NonNull ContentViewCallback contentViewCallback) {
        super(parent, content, contentViewCallback);
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

    public static class Builder {
        private final BottomBar bottomBar;

        public Builder(@NonNull View view) {
            final ViewGroup parent = findSuitableParent(view);
            if (parent == null) {
                throw new IllegalArgumentException("No suitable parent found from the given view. "
                        + "Please provide a valid view.");
            }

            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final SnackbarContentLayout content =
                    (SnackbarContentLayout) inflater.inflate(
                            android.support.design.R.layout.design_layout_snackbar_include, parent, false);
            bottomBar = new BottomBar(parent, content, content);
        }

        public BottomBar build() {
            return bottomBar;
        }
    }
}
