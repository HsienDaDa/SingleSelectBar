package com.supermumu.ui.widget;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.FloatRange;
import android.support.annotation.RequiresApi;
import android.widget.LinearLayout;

import com.supermumu.ui.graphics.drawable.RoundCornerShape;

import java.util.Arrays;

/**
 * Created by hsienhsu on 2017/10/5.
 */
class ResHelper {
    private static final int RIPPLE_ALPHA_VALUE = 55;   // 20%
    private static final int PRESSED_ALPHA_VALUE = 25;  // 10%
    
    private Paint selectedColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private @ColorInt int colorSelected;
    private @ColorInt int colorUnselected;
    private @ColorInt int colorPressed;
    private int strokeWidth;
    private float roundRadius;
    private int orientation;
    
    private float[] fullCornerRadii = new float[8];
    private float[] startCornerRadii = new float[8];
    private float[] centerCornerRadii = new float[8];
    private float[] endCornerRadii = new float[8];
    private float[] commonCornerRadii = new float[8];
    
    private ColorStateList textColor;
    private Drawable cornerStateDrawable;
    
    enum CORNER_POSITION {UNSET, START, CENTER, END, ALL}
        
    ResHelper(@ColorInt int colorSelected, @ColorInt int colorUnselected, float roundRadius,
                     int strokeWidth, @ColorInt int colorPressed) {
        setColorSelected(colorSelected);
        setColorUnselected(colorUnselected);
        this.roundRadius = roundRadius;
        this.strokeWidth = strokeWidth;
    
        setColorPressed(colorPressed);
        setRoundRadius(roundRadius);
        updateCornerStateDrawable();
    }
    
    void setOrientation(int orientation) {
        this.orientation = orientation;
        setRoundRadius(roundRadius);
    }
    
    void setRoundRadius(float roundRadius) {
        // all
        Arrays.fill(fullCornerRadii, roundRadius);
    
        // center
        Arrays.fill(centerCornerRadii, 0F);
    
        // start
        setStartRoundRadius(startCornerRadii, roundRadius);
        
        // end
        setEndRoundRadius(endCornerRadii, roundRadius);
    }
    
    private void setStartRoundRadius(float[] cornetRadii, float roundRadius) {
        if (orientation == LinearLayout.HORIZONTAL) {
            Arrays.fill(cornetRadii, 0, 2, roundRadius);
            Arrays.fill(cornetRadii, 2, 6, 0F);
            Arrays.fill(cornetRadii, 6, 8, roundRadius);
        } else {
            Arrays.fill(cornetRadii, 0, 4, roundRadius);
            Arrays.fill(cornetRadii, 4, 8, 0F);
        }
    }
    
    private void setEndRoundRadius(float[] cornetRadii, float roundRadius) {
        if (orientation == LinearLayout.HORIZONTAL) {
            Arrays.fill(cornetRadii, 0, 2, 0F);
            Arrays.fill(cornetRadii, 2, 6, roundRadius);
            Arrays.fill(cornetRadii, 6, 8, 0F);
        } else {
            Arrays.fill(cornetRadii, 0, 4, 0F);
            Arrays.fill(cornetRadii, 4, 8, roundRadius);
        }
    }
    
    boolean setColorSelected(@ColorInt int colorSelected) {
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
    
    boolean setColorUnselected(@ColorInt int colorUnselected) {
        boolean hasChanged = false;
        if (this.colorUnselected != colorUnselected) {
            this.colorUnselected = colorUnselected;
            updateTextColor();
            updateCornerStateDrawable();
            hasChanged = true;
        }
        return hasChanged;
    }
    
    void setColorPressed(@ColorInt int colorPressed) {
        this.colorPressed = colorPressed;
    }
    
    boolean setTabStrokeWidth(@Dimension int strokeWidth) {
        boolean hasChanged = false;
        if (this.strokeWidth != strokeWidth) {
            this.strokeWidth = strokeWidth;
            updateCornerStateDrawable();
            hasChanged = true;
        }
        return hasChanged;
    }
    
    Drawable getTabBackgroundDrawable() {
        return cornerStateDrawable;
    }
    
    float[] getCornerRadii(CORNER_POSITION cornerPosition, @FloatRange(from = 0.0F, to = 1.0F) float scrollPercent) {
        if (CORNER_POSITION.CENTER == cornerPosition) {
            return centerCornerRadii;
        } else if (CORNER_POSITION.START == cornerPosition) {
            setStartRoundRadius(commonCornerRadii, roundRadius * scrollPercent);
            return commonCornerRadii;
        } else if (CORNER_POSITION.END == cornerPosition) {
            setEndRoundRadius(commonCornerRadii, roundRadius * scrollPercent);
            return commonCornerRadii;
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
    
    ColorStateList getTextColorStateList() {
        return textColor;
    }
    
    Drawable getTextBgDrawable(CORNER_POSITION cornerPosition) {
        int color = colorPressed;
        if (color == -1) {
            return null;
        }
        
        Drawable backgroundDrawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            backgroundDrawable = getRippleEffect(cornerPosition, color);
        } else {
            backgroundDrawable = getPressedEffectBeforeLollipop(cornerPosition, color);
        }
        return backgroundDrawable;
    }
    
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Drawable getRippleEffect(CORNER_POSITION cornerPosition, int color) {
        int[] colors = new int[] {color};
        int[][] states = new int[1][];
        states[0] = new int[] {android.R.attr.state_enabled, android.R.attr.state_pressed};
        ColorStateList colorStateList = new ColorStateList(states, colors);
    
        Drawable maskDrawable = getCornerStateDrawable(cornerPosition);
        maskDrawable.setAlpha(RIPPLE_ALPHA_VALUE);
        maskDrawable.mutate();
    
        return new RippleDrawable(colorStateList, null, maskDrawable);
    }
    
    private Drawable getPressedEffectBeforeLollipop(CORNER_POSITION cornerPosition, int color) {
        ShapeDrawable colorDrawablePressed = getCornerStateDrawable(cornerPosition);
        colorDrawablePressed.getPaint().setColor(color);
        colorDrawablePressed.getPaint().setStyle(Paint.Style.FILL);
        colorDrawablePressed.getPaint().setAlpha(PRESSED_ALPHA_VALUE);
        colorDrawablePressed.mutate();
    
        StateListDrawable textBgDrawable = new StateListDrawable();
        textBgDrawable.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed}, colorDrawablePressed);
        return textBgDrawable;
    }
    
    private ShapeDrawable getCornerStateDrawable(CORNER_POSITION cornerPosition) {
        ShapeDrawable stateDrawable;
        if (CORNER_POSITION.CENTER == cornerPosition) {
            stateDrawable = new ShapeDrawable(new RoundCornerShape(centerCornerRadii));
        } else if (CORNER_POSITION.START == cornerPosition) {
            stateDrawable = new ShapeDrawable(new RoundCornerShape(startCornerRadii));
        } else if (CORNER_POSITION.END == cornerPosition) {
            stateDrawable = new ShapeDrawable(new RoundCornerShape(endCornerRadii));
        } else {
            stateDrawable = new ShapeDrawable(new RoundCornerShape(fullCornerRadii));
        }
        return stateDrawable;
    }
    
    void drawRect(Canvas canvas, Rect rect) {
        canvas.drawRect(rect, selectedColorPaint);
    }
    
    void drawPath(Canvas canvas, Path path) {
        canvas.drawPath(path, selectedColorPaint);
    }
}
