package com.abtingramian.purrty.coachmarks;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.LinearGradient;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.abtingramian.purrty.shapedrawablewithborder.ShapeDrawableWithBorder;
import com.abtingramian.purrty.util.RippleUtil;
import com.abtingramian.purrty.util.ViewUtil;

/**
 * This is a custom view for drawing a coach marks/tip box ui element.
 */
public class CoachMarks extends FrameLayout {

    private enum TIP_SIDE {
        LEFT(0), RIGHT(1), TOP(2), BOTTOM(3);
        int id;

        TIP_SIDE(int id) {
            this.id = id;
        }

        static TIP_SIDE fromId(int id) {
            for (TIP_SIDE f : values()) {
                if (f.id == id) return f;
            }
            return BOTTOM;
        }
    }

    private int pointHeight;
    private int pointWidth;
    private int boxColor;
    private int cornerRadius;
    private View view;
    private @LayoutRes int layoutResId;
    private TIP_SIDE tipSide;
    private int tipLocationFromEnd;
    private boolean shadow;

    public CoachMarks(Context context) {
        super(context);
    }

    public CoachMarks(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray customAttrs = context.obtainStyledAttributes(attrs, R.styleable.CoachMarks);
        try {
            int pointSizeDefault = getResources().getDimensionPixelSize(R.dimen.coachmarks_point_size);
            pointHeight = customAttrs.getDimensionPixelSize(R.styleable.CoachMarks_point_height, pointSizeDefault);
            pointWidth = customAttrs.getDimensionPixelSize(R.styleable.CoachMarks_point_width, pointSizeDefault);
            boxColor = customAttrs.getColor(R.styleable.CoachMarks_box_color, ContextCompat.getColor(context, R.color.coachmarks_box));
            cornerRadius = customAttrs.getDimensionPixelSize(R.styleable.CoachMarks_corner_radius, 0);
            layoutResId = customAttrs.getResourceId(R.styleable.CoachMarks_custom_layout, 0);
            tipSide = TIP_SIDE.fromId(customAttrs.getInt(R.styleable.CoachMarks_tip_side, 3));
            tipLocationFromEnd = customAttrs.getDimensionPixelSize(R.styleable.CoachMarks_tip_location_from_end, 0);
            shadow = customAttrs.getBoolean(R.styleable.CoachMarks_shadow, false);
        } finally {
            customAttrs.recycle();
        }
        // if a custom layout res id is provided use it
        if (layoutResId != 0) {
            view = LayoutInflater.from(getContext()).inflate(layoutResId, this, false);
        }
        // otherwise create a text view
        else {
            view = new TextView(context, attrs);
        }
        setup();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        init();
    }

    public void setPointHeight(int pointHeight) {
        this.pointHeight = pointHeight;
    }

    public void setPointWidth(int pointWidth) {
        this.pointWidth = pointWidth;
    }

    public void setBoxColor(int boxColor) {
        this.boxColor = boxColor;
    }

    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void setLayoutResId(int layoutResId) {
        this.layoutResId = layoutResId;
    }

    public void setTipSide(TIP_SIDE tipSide) {
        this.tipSide = tipSide;
    }

    public void setTipLocationFromEnd(int tipLocationFromEnd) {
        this.tipLocationFromEnd = tipLocationFromEnd;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public void show(@NonNull Activity activity) {
        // remove the view if it is already added
        hide();
        ((ViewGroup) activity.getWindow().getDecorView()).addView(this);
    }

    public void hide() {
        if (getParent() != null) {
            ((ViewGroup) getParent()).removeView(this);
        }
    }

    private void setup() {
        if (view != null) {
            view.setVisibility(VISIBLE);
            // set unique id
            ViewUtil.setUniqueViewId(view);
            // add the view
            addView(view);
        }
        // set padding so tip is visible
        switch (tipSide) {
            case LEFT:
                ViewCompat.setPaddingRelative(this, pointHeight, 0 , 0, 0);
                break;
            case TOP:
                ViewCompat.setPaddingRelative(this, 0, pointHeight , 0, 0);
                break;
            case RIGHT:
                ViewCompat.setPaddingRelative(this, 0, 0 , pointHeight, 0);
                break;
            default:
                ViewCompat.setPaddingRelative(this, 0, 0 , 0, pointHeight);
                break;
        }
        setClipChildren(false);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
            }
        });
    }

    private void init() {
        ShapeDrawableWithBorder shapeDrawable = new ShapeDrawableWithBorder(new PathShape(getPath(), getWidth(), getHeight()));
        shapeDrawable.setBounds(0, 0, getWidth(), getHeight());
        shapeDrawable.getPaint().setDither(true);
        shapeDrawable.getPaint().setAntiAlias(true);
        if (shadow) {
            ShapeDrawable shapeDrawableShadow = new ShapeDrawable(new PathShape(getPath(false), getWidth(), getHeight()));
            shapeDrawableShadow.setShaderFactory(new ShapeDrawable.ShaderFactory() {
                @Override
                public Shader resize(int width, int height) {
                    int baseShadowColor = ContextCompat.getColor(getContext(), R.color.shadow);
                    return new LinearGradient(0, 0, 0, height,
                            new int[]{baseShadowColor, baseShadowColor, baseShadowColor, baseShadowColor},
                            new float[]{0, 1, 1, 1},
                            Shader.TileMode.REPEAT);
                }
            });
            LayerDrawable ld = new LayerDrawable(new Drawable[]{shapeDrawableShadow, shapeDrawable});
            ld.setLayerInset(0, 0, 5, 0, 0); // inset the shadow so it doesn't start right at the left/top
            ld.setLayerInset(1, 0, 0, 0, 5); // inset the top drawable so we can leave a bit of space for the shadow to use
            RippleUtil.setBackgroundCompat(this, ld, boxColor, -1);
        } else {
            RippleUtil.setBackgroundCompat(this, shapeDrawable, boxColor, -1);
        }
    }

    private Path getPath() {
        return getPath(true);
    }

    private Path getPath(boolean drawTip) {
        int width = getWidth();
        int height = getHeight();
        // define the tip box points
        Point leftTop;
        Point rightTop;
        Point rightBottom;
        Point leftBottom;
        switch (tipSide) {
            case LEFT:
                leftTop = new Point(pointHeight, 0);
                rightTop = new Point(width, 0);
                rightBottom = new Point(width, height);
                leftBottom = new Point(pointHeight, height);
                break;
            case TOP:
                leftTop = new Point(0, pointHeight);
                rightTop = new Point(width, pointHeight);
                rightBottom = new Point(width, height);
                leftBottom = new Point(0, height);
                break;
            case RIGHT:
                leftTop = new Point(0, 0);
                rightTop = new Point(width - pointHeight, 0);
                rightBottom = new Point(width - pointHeight, height);
                leftBottom = new Point(0, height);
                break;
            case BOTTOM:
                leftTop = new Point(0, 0);
                rightTop = new Point(width, 0);
                rightBottom = new Point(width, height - pointHeight);
                leftBottom = new Point(0, height - pointHeight);
                break;
            default:
                leftTop = new Point(0, 0);
                rightTop = new Point(width, 0);
                rightBottom = new Point(width, height);
                leftBottom = new Point(0, height);
                break;
        }
        // draw the tip box based on the points
        Path path = new Path();
        path.moveTo(leftTop.x, leftTop.y + cornerRadius);
        path.quadTo(leftTop.x, leftTop.y, leftTop.x + cornerRadius, leftTop.y);
        if (drawTip && tipSide == TIP_SIDE.TOP) {
            path = addTipToPath(path);
        }
        path.lineTo(rightTop.x - cornerRadius, rightTop.y);
        path.quadTo(rightTop.x, rightTop.y, rightTop.x, rightTop.y + cornerRadius);
        if (drawTip && tipSide == TIP_SIDE.RIGHT) {
            path = addTipToPath(path);
        }
        path.lineTo(rightBottom.x, rightBottom.y - cornerRadius);
        path.quadTo(rightBottom.x, rightBottom.y, rightBottom.x - cornerRadius, rightBottom.y);
        if (drawTip && tipSide == TIP_SIDE.BOTTOM) {
            path = addTipToPath(path);
        }
        path.lineTo(leftBottom.x + cornerRadius, leftBottom.y);
        path.quadTo(leftBottom.x, leftBottom.y, leftBottom.x, leftBottom.y - cornerRadius);
        if (drawTip && tipSide == TIP_SIDE.LEFT) {
            path = addTipToPath(path);
        }
        path.close();
        return path;
    }

    private Path addTipToPath(Path path) {
        int width = getWidth();
        int height = getHeight();
        Point tipStart = null;
        Point tip = null;
        Point tipEnd = null;
        // set default midway points
        int midwayX = width / 2;
        int midwayY = height / 2;
        // adjust midway point relative to end based on positive or negative value
        if (tipLocationFromEnd > 0) {
            midwayX = tipLocationFromEnd;
            midwayY = tipLocationFromEnd;
        } else if (tipLocationFromEnd < 0) {
            midwayX = width + tipLocationFromEnd;
            midwayY = height + tipLocationFromEnd;
        }
        // set the start, tip, end points
        switch (tipSide) {
            case LEFT:
                tipStart = new Point(pointHeight, midwayY + (pointWidth / 2));
                tip = new Point(0, midwayY);
                tipEnd = new Point(pointHeight, midwayY - (pointWidth / 2));
                break;
            case TOP:
                tipStart = new Point(midwayX - (pointWidth / 2), pointHeight);
                tip = new Point(midwayX, 0);
                tipEnd = new Point(midwayX + (pointWidth / 2), pointHeight);
                break;
            case RIGHT:
                tipStart = new Point(width - pointHeight, midwayY - (pointWidth / 2));
                tip = new Point(width, midwayY);
                tipEnd = new Point(width - pointHeight, midwayY + (pointWidth / 2));
                break;
            default:
                tipStart = new Point(midwayX + (pointWidth / 2), height - pointHeight);
                tip = new Point(midwayX, height);
                tipEnd = new Point(midwayX - (pointWidth / 2), height - pointHeight);
                break;
        }
        // draw the tip
        path.lineTo(tipStart.x, tipStart.y);
        path.lineTo(tip.x, tip.y);
        path.lineTo(tipEnd.x, tipEnd.y);
        return path;
    }

    public static class Builder {
        private final Context context;
        private int pointWidth;
        private int pointHeight;
        private int boxColor;
        private int textColor;
        private int cornerRadius = 0;
        private int padding;
        private CharSequence text;
        private View view;
        private @LayoutRes int customViewResId;
        private TIP_SIDE tipSide = TIP_SIDE.BOTTOM;
        private int tipLocationFromEnd = 0;
        private boolean shadow = false;

        public Builder(@NonNull final Context context) {
            this.context = context;
            pointWidth = pointHeight = context.getResources().getDimensionPixelSize(R.dimen.coachmarks_point_size);
            padding = context.getResources().getDimensionPixelSize(R.dimen.coachmarks_padding);
            boxColor = ContextCompat.getColor(context, R.color.coachmarks_box);
            textColor = ContextCompat.getColor(context, R.color.coachmarks_text);
        }

        public Builder pointSize(int size) {
            pointWidth = pointHeight = size;
            return this;
        }

        public Builder pointSizeRes(@DimenRes int sizeResId) {
            pointWidth = pointHeight = context.getResources().getDimensionPixelSize(sizeResId);
            return this;
        }

        public Builder pointWidth(int size) {
            pointWidth = size;
            return this;
        }

        public Builder pointWidthRes(@DimenRes int sizeResId) {
            pointWidth = context.getResources().getDimensionPixelSize(sizeResId);
            return this;
        }

        public Builder pointHeight(int size) {
            pointHeight = size;
            return this;
        }

        public Builder pointHeightRes(@DimenRes int sizeResId) {
            pointHeight = context.getResources().getDimensionPixelSize(sizeResId);
            return this;
        }

        public Builder boxColor(int color) {
            boxColor = color;
            return this;
        }

        public Builder boxColorRes(@ColorRes int colorResId) {
            boxColor = ContextCompat.getColor(context, colorResId);
            return this;
        }

        public Builder textColor(int color) {
            textColor = color;
            return this;
        }

        public Builder textColorRes(@ColorRes int colorResId) {
            textColor = ContextCompat.getColor(context, colorResId);
            return this;
        }

        public Builder cornerRadius(int size) {
            cornerRadius = size;
            return this;
        }
        
        public Builder cornerRadiusRes(@DimenRes int sizeResId) {
            cornerRadius = context.getResources().getDimensionPixelSize(sizeResId);
            return this;
        }

        public Builder padding(int size) {
            padding = size;
            return this;
        }

        public Builder paddingRes(@DimenRes int sizeResId) {
            padding = context.getResources().getDimensionPixelSize(sizeResId);
            return this;
        }

        public Builder text(@NonNull CharSequence text) {
            this.text = text;
            return this;
        }

        public Builder text(@StringRes int textResId) {
            text = context.getString(textResId);
            return this;
        }

        public Builder customView(@NonNull View view) {
            this.view = view;
            return this;
        }

        public Builder customView(@LayoutRes int layoutResId) {
            customViewResId = layoutResId;
            return this;
        }

        public Builder tipSide(TIP_SIDE tipSide) {
            this.tipSide = tipSide;
            return this;
        }

        public Builder tipLocationFromEnd(int tipLocationFromEnd) {
            this.tipLocationFromEnd = tipLocationFromEnd;
            return this;
        }

        public Builder shadow(boolean shadow) {
            this.shadow = shadow;
            return this;
        }

        public CoachMarks build() {
            CoachMarks coachMarks = new CoachMarks(context);
            coachMarks.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // if a custom view or layout res id is provided use it
            if (customViewResId != 0) {
                view = LayoutInflater.from(context).inflate(customViewResId, coachMarks, false);
            } else if (view == null) {
                // otherwise create a text view
                TextView textView = new TextView(context);
                if (!TextUtils.isEmpty(text)) {
                    textView.setText(text);
                }
                textView.setTextColor(textColor);
                view = textView;
            }
            view.setPadding(padding, padding, padding, padding);
            coachMarks.setView(view);
            coachMarks.setPointWidth(pointWidth);
            coachMarks.setPointHeight(pointHeight);
            coachMarks.setBoxColor(boxColor);
            coachMarks.setCornerRadius(cornerRadius);
            coachMarks.setTipSide(tipSide);
            coachMarks.setTipLocationFromEnd(tipLocationFromEnd);
            coachMarks.setShadow(shadow);
            coachMarks.setup();
            coachMarks.init();
            return coachMarks;
        }

    }

}
