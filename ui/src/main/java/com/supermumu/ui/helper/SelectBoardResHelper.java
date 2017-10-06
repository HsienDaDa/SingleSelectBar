package com.supermumu.ui.helper;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorInt;

/**
 * Created by hsienhsu on 2017/10/5.
 */

public class SelectBoardResHelper {
    
    private @ColorInt int colorSelected;
    private @ColorInt int colorUnselected;
    private float roundRadius;
    private int strokeWidth;
    
    private float[] backgroundCornerRadii;
    private float[] startCornerRadii;
    private float[] centerCornerRadii;
    private float[] endCornerRadii;
    
    public SelectBoardResHelper(@ColorInt int colorSelected, @ColorInt int colorUnselected, int roundRadius, int strokeWidth) {
        this.colorSelected = colorSelected;
        this.colorUnselected = colorUnselected;
        this.roundRadius = roundRadius;
        this.strokeWidth = strokeWidth;
    
        backgroundCornerRadii = new float[]{roundRadius, roundRadius, roundRadius, roundRadius,roundRadius, roundRadius, roundRadius, roundRadius};
        startCornerRadii = new float[]{roundRadius, roundRadius, 0, 0, 0, 0, roundRadius, roundRadius};
        centerCornerRadii = new float[]{0, 0, 0, 0, 0, 0, 0, 0};
        endCornerRadii = new float[]{0, 0, roundRadius, roundRadius, roundRadius, roundRadius, 0, 0};
    }
    
    public void setColorSelected(@ColorInt int colorSelected) {
        this.colorSelected = colorSelected;
    }
    
    public void setColorUnselected(@ColorInt int colorUnselected) {
        this.colorUnselected = colorUnselected;
    }
    
    public Drawable getBoardBackgroundDrawable() {
        return getCornerStateDrawable(backgroundCornerRadii);
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
    
    private Drawable getCornerStateDrawable(float[] cornerRadii) {
        Drawable unselectedDrawable = getCornetDrawable(false, cornerRadii);
        Drawable selectedDrawable = getCornetDrawable(true, cornerRadii);
    
        int[][] states = new int[2][];
        states[0] = new int[] {android.R.attr.state_selected};
        states[1] = new int[] {};
        StateListDrawable bg = new StateListDrawable();
        bg.addState(states[0], selectedDrawable);
        bg.addState(states[1], unselectedDrawable);
        return bg;
    }
    
    private Drawable getCornetDrawable(boolean selected, float[] cornerRadii) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(selected ? colorSelected : colorUnselected);
        drawable.setCornerRadii(cornerRadii);
        drawable.setStroke(strokeWidth, colorSelected);
        return drawable;
    }
    
    public ColorStateList getTextColorStateList() {
        int[] colors = new int[] {colorUnselected, colorSelected};
        int[][] states = new int[2][];
        states[0] = new int[] {android.R.attr.state_selected};
        states[1] = new int[] {};
        return new ColorStateList(states, colors);
    }
}
