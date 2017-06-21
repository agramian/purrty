package com.abtingramian.purtee.arrow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * This is a custom view which draws an arrow between two points.
 */
public class Arrow extends View {

    private final Path path = new Path();
    private final Path arrow = new Path();
    private final Matrix arrowRotationMatrix = new Matrix();
    private final RectF arrowBounds = new RectF();
    private Paint linePaint;
    private Paint arrowPaint;
    private PointF startPoint = new PointF(0, 0);
    private PointF endPoint = new PointF(0, 0);
    private PointF controlPoint  = new PointF(0, 0);
    private float curvature = 1.0f;
    private Float strokeWidth;
    private Float arrowSize;
    private int lineColor = Color.BLACK;
    private int arrowColor = Color.BLACK;
    private Paint.Style lineStyle = Paint.Style.STROKE;
    private Paint.Style arrowStyle = Paint.Style.STROKE;
    private PathEffect linePathEffect;
    private PathEffect arrowPathEffect;
    private @DimenRes int strokeWidthResId = R.dimen.arrow_stroke_width;
    private @DimenRes int arrowSizeResId  = R.dimen.arrow_size;
    private float arrowRotationDegrees = 0f;

    public Arrow(Context context) {
        super(context);
    }

    public Arrow(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw line curve
        path.moveTo(startPoint.x, startPoint.y);
        path.quadTo(controlPoint.x, controlPoint.y, endPoint.x, endPoint.y);
        canvas.drawPath(path, linePaint);
        // draw arrow
        arrow.moveTo(endPoint.x - arrowSize, endPoint.y);
        arrow.rLineTo(arrowSize, -arrowSize);
        arrow.rLineTo(arrowSize, arrowSize);
        arrow.computeBounds(arrowBounds, true);
        arrowRotationMatrix.postRotate(arrowRotationDegrees, arrowBounds.centerX(), arrowBounds.centerY());
        arrow.transform(arrowRotationMatrix);
        canvas.drawPath(arrow, arrowPaint);
    }

    private void init() {
        // set default values if necessary
        if (strokeWidth == null) {
            strokeWidth = getResources().getDimension(strokeWidthResId);
        }
        if (arrowSize == null) {
            arrowSize = getResources().getDimension(arrowSizeResId);
        }
        // set up line paint if necessary
        if (linePaint == null) {
            linePaint = new Paint();
            linePaint.setColor(lineColor);
            linePaint.setAntiAlias(true);
            linePaint.setStrokeWidth(strokeWidth);
            linePaint.setStyle(lineStyle);
            if (linePathEffect != null) {
                linePaint.setPathEffect(linePathEffect);
            }
        }
        // set up arrow paint if necessary
        if (arrowPaint == null) {
            arrowPaint = new Paint();
            arrowPaint.setColor(arrowColor);
            arrowPaint.setAntiAlias(true);
            arrowPaint.setStrokeWidth(strokeWidth);
            arrowPaint.setStyle(arrowStyle);
            if (arrowPathEffect != null) {
                arrowPaint.setPathEffect(arrowPathEffect);
            }
        }
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

    public void setLinePaint(Paint linePaint) {
        this.linePaint = linePaint;
    }

    public void setArrowPaint(Paint arrowPaint) {
        this.arrowPaint = arrowPaint;
    }

    public void setStartPoint(PointF startPoint) {
        this.startPoint = startPoint;
    }

    public void setEndPoint(PointF endPoint) {
        this.endPoint = endPoint;
    }

    public void setCurvature(float curvature) {
        this.curvature = curvature;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setArrowSize(float arrowSize) {
        this.arrowSize = arrowSize;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public void setArrowColor(int arrowColor) {
        this.arrowColor = arrowColor;
    }

    public void setLineStyle(Paint.Style lineStyle) {
        this.lineStyle = lineStyle;
    }

    public void setArrowStyle(Paint.Style arrowStyle) {
        this.arrowStyle = arrowStyle;
    }

    public void setLinePathEffect(PathEffect linePathEffect) {
        this.linePathEffect = linePathEffect;
    }

    public void setArrowPathEffect(PathEffect arrowPathEffect) {
        this.arrowPathEffect = arrowPathEffect;
    }

    public void setArrowRotationDegrees(float arrowRotationDegrees) {
        this.arrowRotationDegrees = arrowRotationDegrees;
    }

    public static class Builder {
        private final Context context;
        private Paint linePaint;
        private Paint arrowPaint;
        private PointF startPoint;
        private PointF endPoint;
        private Float curvature;
        private Float strokeWidth;
        private Float arrowSize;
        private Integer lineColor;
        private Integer arrowColor;
        private Paint.Style lineStyle;
        private Paint.Style arrowStyle;
        private PathEffect linePathEffect;
        private PathEffect arrowPathEffect;
        private Float arrowRotationDegrees;

        public Builder(@NonNull final Context context) {
            this.context = context;
        }

        public Builder linePaint(@NonNull Paint paint) {
            linePaint = paint;
            return this;
        }

        public Builder arrowPaint(@NonNull Paint paint) {
            arrowPaint = paint;
            return this;
        }

        public Builder startPoint(@NonNull PointF point) {
            startPoint = point;
            return this;
        }

        public Builder endPoint(@NonNull PointF point) {
            endPoint = point;
            return this;
        }

        public Builder curvature(float curvature) {
            this.curvature = curvature;
            return this;
        }

        public Builder strokeWidthRes(@DimenRes int strokeWidthResId) {
            strokeWidth = context.getResources().getDimension(strokeWidthResId);
            return this;
        }

        public Builder strokeWidth(float size) {
            strokeWidth = size;
            return this;
        }

        public Builder arrowSizeRes(@DimenRes int arrowSizeResId) {
            arrowSize = context.getResources().getDimension(arrowSizeResId);
            return this;
        }

        public Builder arrowSize(float size) {
            arrowSize = size;
            return this;
        }

        public Builder lineColor(int color) {
            lineColor = color;
            return this;
        }

        public Builder arrowColor(int color) {
            arrowColor = color;
            return this;
        }

        public Builder lineColorRes(@ColorRes int colorResId) {
            lineColor = ContextCompat.getColor(context, colorResId);
            return this;
        }

        public Builder arrowColorRes(@ColorRes int colorResId) {
            arrowColor = ContextCompat.getColor(context, colorResId);
            return this;
        }

        public Builder lineStyle(@NonNull Paint.Style style) {
            lineStyle = style;
            return this;
        }

        public Builder arrowStyle(@NonNull Paint.Style style) {
            arrowStyle = style;
            return this;
        }

        public Builder linePathEffect(@NonNull PathEffect pathEffect) {
            linePathEffect = pathEffect;
            return this;
        }

        public Builder arrowPathEffect(@NonNull PathEffect pathEffect) {
            arrowPathEffect = pathEffect;
            return this;
        }

        public Builder arrowRotationDegrees(@IntegerRes int rotationDegreesResId) {
            arrowRotationDegrees = (float) context.getResources().getInteger(rotationDegreesResId);
            return this;
        }

        public Builder arrowRotationDegrees(float degrees) {
            arrowRotationDegrees = degrees;
            return this;
        }

        public Arrow build() {
            Arrow arrow = new Arrow(context);
            if (linePaint != null) {
                arrow.setLinePaint(linePaint);
            }
            if (arrowPaint != null) {
                arrow.setArrowPaint(arrowPaint);
            }
            if (startPoint != null) {
                arrow.setStartPoint(startPoint);
            }
            if (endPoint != null) {
                arrow.setEndPoint(endPoint);
            }
            if (curvature != null) {
                arrow.setCurvature(curvature);
            }
            if (strokeWidth != null) {
                arrow.setStrokeWidth(strokeWidth);
            }
            if (arrowSize != null) {
                arrow.setArrowSize(arrowSize);
            }
            if (lineColor != null) {
                arrow.setLineColor(lineColor);
            }
            if (arrowColor != null) {
                arrow.setArrowColor(arrowColor);
            }
            if (lineStyle != null) {
                arrow.setLineStyle(lineStyle);
            }
            if (arrowStyle != null) {
                arrow.setArrowStyle(arrowStyle);
            }
            if (linePathEffect != null) {
                arrow.setLinePathEffect(linePathEffect);
            }
            if (arrowPathEffect != null) {
                arrow.setArrowPathEffect(arrowPathEffect);
            }
            if (arrowRotationDegrees != null) {
                arrow.setArrowRotationDegrees(arrowRotationDegrees);
            }
            arrow.init();
            return arrow;
        }
    }

}
