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
    
    public SelectBoardResHelper(@ColorInt int colorSelected, @ColorInt int colorUnselected, int roundRadius, int strokeWidth) {
        this.colorSelected = colorSelected;
        this.colorUnselected = colorUnselected;
        this.roundRadius = roundRadius;
        this.strokeWidth = strokeWidth;
    }
    
    public Drawable getStartDrawable() {
        float[] cornerRadii = {roundRadius, roundRadius, 0, 0, 0, 0, roundRadius, roundRadius};
        return getCornerStateDrawable(cornerRadii);
    }
    
    public Drawable getCenterDrawable() {
        return getCornerStateDrawable(null);
    }
    
    public Drawable getEndDrawable() {
        float[] cornerRadii = {0, 0, roundRadius, roundRadius, roundRadius, roundRadius, 0, 0};
        return getCornerStateDrawable(cornerRadii);
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
