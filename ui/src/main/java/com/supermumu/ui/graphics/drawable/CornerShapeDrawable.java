package com.supermumu.ui.graphics.drawable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.shapes.RectShape;

/**
 * Created by hsienhsu on 2017/10/20.
 */

public class CornerShapeDrawable extends RectShape {
    private RectF paddingRect;
    private Path path = new Path();
    private float[] cornetRadii;
    
    public CornerShapeDrawable(float[] cornetRadii) {
        this(cornetRadii, null);
    }
    
    public CornerShapeDrawable(float[] cornetRadii, RectF paddingRect) {
        this.cornetRadii = cornetRadii;
        this.paddingRect = paddingRect;
    }
    
    @Override
    protected void onResize(float width, float height) {
        super.onResize(width, height);
    
        if (null != paddingRect && !paddingRect.isEmpty()) {
            RectF rect = rect();
            rect.left += paddingRect.left;
            rect.top += paddingRect.top;
            rect.right -= paddingRect.right;
            rect.bottom -= paddingRect.bottom;
        }
        path.addRoundRect(rect(), cornetRadii, Path.Direction.CCW);
    }
    
    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawPath(path, paint);
    }
}
