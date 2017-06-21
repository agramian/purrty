package com.abtingramian.purtee.arrow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * This is a custom view which draws an arrow between two points.
 */
public class Arrow extends View {

    private final Path path = new Path();
    private final Path arrow = new Path();
    private final Paint arrowPaint = new Paint();
    private final Paint linePaint = new Paint();
    private PointF startPoint;
    private PointF endPoint;
    private PointF controlPoint;
    private float curvature = 1.0f;
    private float strokeWidth;
    private float dashIntervals[];
    private float arrowSize;

    public Arrow(Context context) {
        super(context);
    }

    public Arrow(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (startPoint != null && endPoint != null && controlPoint != null) {
            // draw line curve
            path.moveTo(startPoint.x, startPoint.y);
            path.quadTo(controlPoint.x, controlPoint.y, endPoint.x, endPoint.y);
            canvas.drawPath(path, linePaint);
            // draw arrow
            arrow.moveTo(endPoint.x, endPoint.y - arrowSize);
            arrow.rLineTo(arrowSize, arrowSize);
            arrow.rLineTo(-arrowSize, arrowSize);
            canvas.drawPath(arrow, arrowPaint);
        }
    }

    private void init() {
        // set up line paint
        linePaint.setColor(Color.WHITE);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(strokeWidth);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setPathEffect(new DashPathEffect(dashIntervals, 0));
        // set up arrow paint
        arrowPaint.setColor(Color.WHITE);
        arrowPaint.setAntiAlias(true);
        arrowPaint.setStrokeWidth(strokeWidth);
        arrowPaint.setStyle(Paint.Style.STROKE);
        // calculate base control point for the curve by finding the 3rd vertex
        // for an equilateral triangle based on the start and end points
        float dX = endPoint.x - startPoint.x;
        float dY = endPoint.y - startPoint.y;
        double angleRadians = Math.toRadians(60);
        PointF thirdVertex = new PointF((float) ((Math.cos(angleRadians) * dX) - (Math.sin(angleRadians) * dY) + startPoint.x),
                (float) ((Math.sin(angleRadians) * dX) + (Math.cos(angleRadians) * dY) + startPoint.y));
        // linearly interpolate along the line between the midpoint and control point
        // based on the given curvature value to calculate the final control point
        PointF midPoint = new PointF(startPoint.x + ((endPoint.x - startPoint.x) * 0.5f),
                startPoint.y + ((endPoint.y - startPoint.y) * 0.5f));
        controlPoint = new PointF(midPoint.x + ((thirdVertex.x - midPoint.x) * curvature),
                midPoint.y + ((thirdVertex.y - midPoint.y) * curvature));
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

    private void setStartPoint(PointF startPoint) {
        this.startPoint = startPoint;
    }

    private void setEndPoint(PointF endPoint) {
        this.endPoint = endPoint;
    }

    private void setCurvature(float curvature) {
        this.curvature = curvature;
    }

    private void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    private void setDashIntervals(float[] dashIntervals) {
        this.dashIntervals = dashIntervals;
    }

    private void setArrowSize(float arrowSize) {
        this.arrowSize = arrowSize;
    }

    public static class Builder {
        private final Context context;
        private PointF startPoint;
        private PointF endPoint;
        private Float curvature;
        private Float strokeWidth;
        private @DimenRes int strokeWidthResId = R.dimen.arrow_stroke_width;
        private Float onIntervalLength;
        private @DimenRes int onIntervalLengthResId  = R.dimen.arrow_interval_on;
        private Float offIntervalLength;
        private @DimenRes int offIntervalLengthResId  = R.dimen.arrow_interval_off;
        private Float arrowSize;
        private @DimenRes int arrowSizeResId  = R.dimen.arrow_size;

        public Builder(@NonNull final Context context) {
            this.context = context;
        }

        public Builder startPoint(@NonNull PointF point) {
            this.startPoint = point;
            return this;
        }

        public Builder endPoint(@NonNull PointF point) {
            this.endPoint = point;
            return this;
        }

        public Builder curvature(float curvature) {
            this.curvature = curvature;
            return this;
        }

        public Arrow build() {
            Arrow arrowView = new Arrow(context);
            arrowView.setStartPoint(startPoint);
            arrowView.setEndPoint(endPoint);
            if (curvature != null) {
                arrowView.setCurvature(curvature);
            }
            if (strokeWidth == null) {
                strokeWidth = context.getResources().getDimension(strokeWidthResId);
            }
            arrowView.setStrokeWidth(strokeWidth);
            if (onIntervalLength == null) {
                onIntervalLength = context.getResources().getDimension(onIntervalLengthResId);
            }
            if (offIntervalLength == null) {
                offIntervalLength = context.getResources().getDimension(offIntervalLengthResId);
            }
            arrowView.setDashIntervals(new float[] {onIntervalLength, offIntervalLength});
            if (arrowSize == null) {
                arrowSize = context.getResources().getDimension(arrowSizeResId);
            }
            arrowView.setArrowSize(arrowSize);
            arrowView.init();
            return arrowView;
        }
    }

}
