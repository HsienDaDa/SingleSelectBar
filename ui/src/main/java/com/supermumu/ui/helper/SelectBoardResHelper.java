package com.supermumu.ui.helper;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;

/**
 * Created by hsienhsu on 2017/10/5.
 */

public class SelectBoardResHelper {
    
    private Paint selectedColorPaint = new Paint();
    private @ColorInt int colorSelected;
    private @ColorInt int colorUnselected;
    private float roundRadius;
    private int strokeWidth;
    
    private float[] backgroundCornerRadii;
    private float[] startCornerRadii;
    private float[] centerCornerRadii;
    private float[] endCornerRadii;
    
    private ColorStateList textColor;
    private Drawable cornerStateDrawable;
    
    public SelectBoardResHelper(@ColorInt int colorSelected, @ColorInt int colorUnselected, int roundRadius, int strokeWidth) {
        setColorSelected(colorSelected);
        setColorUnselected(colorUnselected);
        this.roundRadius = roundRadius;
        this.strokeWidth = strokeWidth;
    
        backgroundCornerRadii = new float[]{roundRadius, roundRadius, roundRadius, roundRadius,roundRadius, roundRadius, roundRadius, roundRadius};
        startCornerRadii = new float[]{roundRadius, roundRadius, 0, 0, 0, 0, roundRadius, roundRadius};
        centerCornerRadii = new float[]{0, 0, 0, 0, 0, 0, 0, 0};
        endCornerRadii = new float[]{0, 0, roundRadius, roundRadius, roundRadius, roundRadius, 0, 0};
        updateCornerStateDrawable();
    }
    
    public boolean setColorSelected(@ColorInt int colorSelected) {
        boolean hasChanged = false;
        if (this.colorSelected != colorSelected) {
            this.colorSelected = colorSelected;
            selectedColorPaint.setColor(colorSelected);
            updateTextColor();
            updateCornerStateDrawable();
            hasChanged = true;
        }
        return hasChanged;
    }
    
    public boolean setColorUnselected(@ColorInt int colorUnselected) {
        boolean hasChanged = false;
        if (this.colorUnselected != colorUnselected) {
            this.colorUnselected = colorUnselected;
            updateTextColor();
            updateCornerStateDrawable();
            hasChanged = true;
        }
        return hasChanged;
    }
    
    public boolean setBoardStrokeWidth(@Dimension int strokeWidth) {
        boolean hasChanged = false;
        if (this.strokeWidth != strokeWidth) {
            this.strokeWidth = strokeWidth;
            updateCornerStateDrawable();
            hasChanged = true;
        }
        return hasChanged;
    }
    
    public Drawable getBoardBackgroundDrawable() {
        return cornerStateDrawable;
    }
    
    public float[] getStartCornerRadii() {
        return startCornerRadii;
    }
    
    public float[] getCenterCornerRadii() {
        return centerCornerRadii;
    }
    
    public float[] getEndCornerRadii() {
        return endCornerRadii;
    }
    
    private void updateCornerStateDrawable() {
        Drawable unselectedDrawable = getCornetDrawable(false, backgroundCornerRadii);
        Drawable selectedDrawable = getCornetDrawable(true, backgroundCornerRadii);
    
        int[][] states = new int[2][];
        states[0] = new int[] {android.R.attr.state_selected};
        states[1] = new int[] {};
        StateListDrawable bg = new StateListDrawable();
        bg.addState(states[0], selectedDrawable);
        bg.addState(states[1], unselectedDrawable);
        cornerStateDrawable = bg;
    }
    
    private Drawable getCornetDrawable(boolean selected, float[] cornerRadii) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(selected ? colorSelected : colorUnselected);
        drawable.setCornerRadii(cornerRadii);
        drawable.setStroke(strokeWidth, colorSelected);
        return drawable;
    }
    
    private void updateTextColor() {
        int[] colors = new int[] {colorUnselected, colorSelected};
        int[][] states = new int[2][];
        states[0] = new int[] {android.R.attr.state_selected};
        states[1] = new int[] {};
        textColor = new ColorStateList(states, colors);
    }
    
    public ColorStateList getTextColorStateList() {
        return textColor;
    }
    
    public void drawRect(Canvas canvas, Rect rect) {
        canvas.drawRect(rect, selectedColorPaint);
    }
    
    public void drawPath(Canvas canvas, Path path) {
        canvas.drawPath(path, selectedColorPaint);
    }
}
