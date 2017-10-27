package com.supermumu.ui.graphics.drawable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.shapes.RectShape;

/**
 * Created by hsienhsu on 2017/10/20.
 */

public class RoundCornerShape extends RectShape {
    private RectF paddingRect;
    private Path path = new Path();
    private float[] cornerRadii;
    
    public RoundCornerShape(float[] cornerRadii) {
        this(cornerRadii, null);
    }
    
    public RoundCornerShape(float[] cornerRadii, RectF paddingRect) {
        this.cornerRadii = cornerRadii;
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
        path.addRoundRect(rect(), cornerRadii, Path.Direction.CCW);
    }
    
    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawPath(path, paint);
    }
}
