package com.supermumu.ui.helper;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.RequiresApi;

import com.supermumu.ui.graphics.drawable.CornerShapeDrawable;

import java.util.Arrays;

/**
 * Created by hsienhsu on 2017/10/5.
 *
 * @hide
 */

public class ResHelper {
    private static final int RIPPLE_ALPHA_VALUE = 77;
    private static final int PRESSED_ALPHA_VALUE = 51;
    
    private Paint selectedColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private @ColorInt int colorSelected;
    private @ColorInt int colorUnselected;
    private int strokeWidth;
    private boolean showPressedEffect;
    
    private float[] fullCornerRadii = new float[8];
    private float[] startCornerRadii = new float[8];
    private float[] centerCornerRadii = new float[8];
    private float[] endCornerRadii = new float[8];
    
    private ColorStateList textColor;
    private Drawable cornerStateDrawable;
    
    public enum CORNER_POSITION {UNSET, START, CENTER, END, ALL}
    
    public ResHelper(@ColorInt int colorSelected, @ColorInt int colorUnselected, int roundRadius,
                     int strokeWidth, boolean showPressedEffect) {
        setColorSelected(colorSelected);
        setColorUnselected(colorUnselected);
        this.strokeWidth = strokeWidth;
        this.showPressedEffect = showPressedEffect;
    
        setRoundRadius(roundRadius);
        updateCornerStateDrawable();
    }
    
    public void setRoundRadius(float roundRadius) {
        Arrays.fill(fullCornerRadii, roundRadius);
        
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
    
    public float[] getCornerRadii(CORNER_POSITION cornerPosition) {
        if (CORNER_POSITION.CENTER == cornerPosition) {
            return centerCornerRadii;
        } else if (CORNER_POSITION.START == cornerPosition) {
            return startCornerRadii;
        } else if (CORNER_POSITION.END == cornerPosition) {
            return endCornerRadii;
        } else {
            return fullCornerRadii;
        }
    }
    
    private void updateCornerStateDrawable() {
        cornerStateDrawable = getCornerDrawable(false, fullCornerRadii);
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
    
    public Drawable getTextBgDrawable(CORNER_POSITION cornerPosition) {
        Drawable backgroundDrawable = null;
        if (showPressedEffect) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                backgroundDrawable = getRippleEffect(cornerPosition);
            } else {
                backgroundDrawable = getPressedEffectBeforeLollipop();
            }
        }
        return backgroundDrawable;
    }
    
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Drawable getRippleEffect(CORNER_POSITION cornerPosition) {
        int[] colors = new int[] {Color.BLACK};
        int[][] states = new int[1][];
        states[0] = new int[] {android.R.attr.state_enabled, android.R.attr.state_pressed};
        ColorStateList colorStateList = new ColorStateList(states, colors);
    
        Drawable maskDrawable;
        if (CORNER_POSITION.CENTER == cornerPosition) {
            maskDrawable = new ShapeDrawable(new CornerShapeDrawable(centerCornerRadii));
        } else if (CORNER_POSITION.START == cornerPosition) {
            maskDrawable = new ShapeDrawable(new CornerShapeDrawable(startCornerRadii));
        } else if (CORNER_POSITION.END == cornerPosition) {
            maskDrawable = new ShapeDrawable(new CornerShapeDrawable(endCornerRadii));
        } else {
            maskDrawable = new ShapeDrawable(new CornerShapeDrawable(fullCornerRadii));
        }
        maskDrawable.setAlpha(RIPPLE_ALPHA_VALUE);
    
        return new RippleDrawable(colorStateList, null, maskDrawable);
    }
    
    private Drawable getPressedEffectBeforeLollipop() {
        ColorDrawable colorDrawablePressed = new ColorDrawable(Color.BLACK);
        colorDrawablePressed.setAlpha(PRESSED_ALPHA_VALUE);
        StateListDrawable textBgDrawable = new StateListDrawable();
        textBgDrawable.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed}, colorDrawablePressed);
        return textBgDrawable;
    }
    
    public void drawRect(Canvas canvas, Rect rect) {
        canvas.drawRect(rect, selectedColorPaint);
    }
    
    public void drawPath(Canvas canvas, Path path) {
        canvas.drawPath(path, selectedColorPaint);
    }
}
