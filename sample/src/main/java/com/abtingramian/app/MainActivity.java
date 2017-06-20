package com.abtingramian.app;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.PathShape;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;

import com.abtingramian.purtee.shapedrawablewithborder.ShapeDrawableWithBorder;
import com.abtingramian.purteedemo.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // circle with border
        ShapeDrawableWithBorder circleShapeDrawable = new ShapeDrawableWithBorder(new OvalShape());
        circleShapeDrawable.getPaint().setDither(true);
        circleShapeDrawable.getPaint().setAntiAlias(true);
        circleShapeDrawable.setStrokeColor(Color.parseColor("red"));
        circleShapeDrawable.setStrokeWidth(20);
        // triangle with border
        int sizeDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
        PathShape trianglePath = new PathShape(getPathTriangle(sizeDp, sizeDp), sizeDp, sizeDp);
        ShapeDrawableWithBorder triangleShapeDrawable = new ShapeDrawableWithBorder(trianglePath);
        triangleShapeDrawable.getPaint().setDither(true);
        triangleShapeDrawable.getPaint().setAntiAlias(true);
        triangleShapeDrawable.setStrokeColor(Color.parseColor("red"));
        triangleShapeDrawable.setStrokeWidth(20);
        // set background
        View circle = findViewById(R.id.circle);
        View triangle = findViewById(R.id.triangle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            circle.setBackground(circleShapeDrawable);
            triangle.setBackground(triangleShapeDrawable);
        } else {
            circle.setBackgroundDrawable(circleShapeDrawable);
            triangle.setBackgroundDrawable(triangleShapeDrawable);
        }
    }

    private Path getPathTriangle(int width, int height) {
        Path path = new Path();
        // bottom left
        path.moveTo(0, height);
        // top center tip
        path.lineTo(width / 2, 0);
        // bottom right
        path.lineTo(width, height);
        // close
        path.close();
        return path;
    }

}
