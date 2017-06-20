package com.abtingramian.purtee.shapedrawablewithborder;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;

public class ShapeDrawableWithBorder extends ShapeDrawable {

    private Paint strokePaint;

    public ShapeDrawableWithBorder() {
        super();
        init();
    }

    public ShapeDrawableWithBorder(Shape s) {
        super(s);
        init();
    }

    // http://betaful.com/post/82668809883/programmatic-shapes-in-android
    @Override
    protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
        if (strokePaint.getStrokeWidth() > 0) {
            shape.resize(canvas.getClipBounds().right, canvas.getClipBounds().bottom);
            shape.draw(canvas, paint);
            Matrix matrix = new Matrix();
            matrix.setRectToRect(new RectF(0, 0, canvas.getClipBounds().right, canvas.getClipBounds().bottom),
                    new RectF(strokePaint.getStrokeWidth() / 2,
                            strokePaint.getStrokeWidth() / 2,
                            canvas.getClipBounds().right - strokePaint.getStrokeWidth() / 2,
                            canvas.getClipBounds().bottom - strokePaint.getStrokeWidth() / 2),
                    Matrix.ScaleToFit.FILL);
            canvas.concat(matrix);
            shape.draw(canvas, strokePaint);
        } else {
            super.onDraw(shape, canvas, paint);
        }
    }

    public void setStrokeColor(int c) {
        strokePaint.setColor(c);
    }

    public void setStrokeWidth(int width) {
        if (width < 0) {
            width = 0;
        }
        strokePaint.setStrokeWidth(width);
        setIntrinsicWidth(getIntrinsicWidth() + (width * 4));
        setIntrinsicHeight(getIntrinsicHeight() + (width * 4));
    }

    private void init() {
        strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setAntiAlias(true);
        strokePaint.setDither(true);
    }

}
