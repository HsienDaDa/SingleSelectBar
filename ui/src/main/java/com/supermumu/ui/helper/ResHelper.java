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

import java.util.Arrays;

/**
 * Created by hsienhsu on 2017/10/5.
 *
 * @hide
 */

public class ResHelper {
    
    private Paint selectedColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private @ColorInt int colorSelected;
    private @ColorInt int colorUnselected;
    private float roundRadius;
    private int strokeWidth;
    
    private float[] backgroundCornerRadii = new float[8];
    private float[] startCornerRadii = new float[8];
    private float[] centerCornerRadii = new float[8];
    private float[] endCornerRadii = new float[8];
    
    private ColorStateList textColor;
    private Drawable cornerStateDrawable;
    
    public ResHelper(@ColorInt int colorSelected, @ColorInt int colorUnselected, int roundRadius, int strokeWidth) {
        setColorSelected(colorSelected);
        setColorUnselected(colorUnselected);
        this.roundRadius = roundRadius;
        this.strokeWidth = strokeWidth;
    
        setRoundRadius(roundRadius);
        updateCornerStateDrawable();
    }
    
    public void setRoundRadius(float roundRadius) {
        Arrays.fill(backgroundCornerRadii, roundRadius);
        
        Arrays.fill(startCornerRadii, 0, 2, roundRadius);
        Arrays.fill(startCornerRadii, 6, 8, roundRadius);
        
        Arrays.fill(centerCornerRadii, 0);
        
        Arrays.fill(endCornerRadii, 2, 6, roundRadius);
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
    
    public boolean setTabStrokeWidth(@Dimension int strokeWidth) {
        boolean hasChanged = false;
        if (this.strokeWidth != strokeWidth) {
            this.strokeWidth = strokeWidth;
            updateCornerStateDrawable();
            hasChanged = true;
        }
        return hasChanged;
    }
    
    public Drawable getTabBackgroundDrawable() {
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
    
    public float[] getBackgroundCornerRadii() {
        return backgroundCornerRadii;
    }
    
    private void updateCornerStateDrawable() {
        Drawable unselectedDrawable = getCornerDrawable(false, backgroundCornerRadii);
        Drawable selectedDrawable = getCornerDrawable(true, backgroundCornerRadii);
    
        int[][] states = new int[2][];
        states[0] = new int[] {android.R.attr.state_selected};
        states[1] = new int[] {};
        StateListDrawable bg = new StateListDrawable();
        bg.addState(states[0], selectedDrawable);
        bg.addState(states[1], unselectedDrawable);
        cornerStateDrawable = bg;
    }
    
    private Drawable getCornerDrawable(boolean selected, float[] cornerRadii) {
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
