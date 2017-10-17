package com.supermumu.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.supermumu.R;
import com.supermumu.ui.helper.ResHelper;

import java.util.Locale;

/**
 * Created by hsienhsu on 2017/10/17.
 */

public class BubbleView extends View {
    
    private ResHelper resHelper;
    
    private Path circlePath = new Path();
    private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private RectF rectF = new RectF();
    private Paint.FontMetrics fontMetrics;
    
    private int bubbleCount;
    private String bubbleResultText;
    private int bubbleMaxCount = 99;
    private boolean isBubbleCountChanged = true;
    
    private Paint debugLinePaint = new Paint();
    
    private enum VIEW_EFFECT {ADD, UPDATE, REMOVE}
    
    public BubbleView(Context context) {
        super(context);
        init(context, null);
    }
    
    public BubbleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    
    public BubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }
    
    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        
        int colorSelected = Color.RED;
//        int colorSelected = ContextCompat.getColor(context, R.color.selected_theme_color);
        resHelper = new ResHelper(colorSelected, colorSelected, 0, 0);
        
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);
        fontMetrics = textPaint.getFontMetrics();
    
        debugLinePaint.setColor(Color.RED);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    
        if (null != bubbleResultText) {
            drawCircleWithCount(canvas);
        } else {
            drawCircle(canvas);
        }
        
        canvas.drawLine(0, canvas.getHeight() / 2, canvas.getWidth(), canvas.getHeight() / 2, debugLinePaint);
        canvas.drawLine(canvas.getWidth()/2, 0, canvas.getWidth()/2, canvas.getHeight(), debugLinePaint);
    }
    
    private void drawCircleWithCount(Canvas canvas) {
        float textHeight = (fontMetrics.bottom - fontMetrics.top);
        float badgeTextWidth = textPaint.measureText(bubbleResultText);
//        Log.e("A", "Hsien_ // size: " + textPaint.getTextSize());
//        Log.e("A", "Hsien_ // top: " + fontMetrics.top);
//        Log.e("A", "Hsien_ // ascent: " + textPaint.ascent() + ", " + fontMetrics.ascent);
//        Log.e("A", "Hsien_ // descent: " + textPaint.descent() + ", " + fontMetrics.descent);
//        Log.e("A", "Hsien_ // bottom: " + fontMetrics.bottom);
//        Log.e("A", "Hsien_ // 1: " + textPaint.measureText("1") + ", 11: " + textPaint.measureText("11") + ", 國: " + textPaint.measureText("國") + ", A: " + textPaint.measureText("A") + ", AA: " + textPaint.measureText("AA"));
//        Log.e("A", "Hsien_ // textHeight: " + textHeight + ", badgeTextWidth: " + badgeTextWidth + ", w: " + getMeasuredWidth() + ", h: " + getMeasuredHeight());
    
        float viewX = canvas.getWidth() / 2F;
        float viewY = canvas.getHeight() / 2F;
        float baseX = viewX;
        float baseY = (viewY - ((textPaint.descent() + textPaint.ascent()) / 2F));
    
        if (isBubbleCountChanged) {
            isBubbleCountChanged = false;
    
            circlePath.reset();
            
            float padding = getResources().getDimensionPixelSize(R.dimen.margin_2x);
            float radiusY = (textHeight / 2F);
            if (textHeight > (badgeTextWidth + padding)) {
                circlePath.addCircle(viewX, viewY, radiusY, Path.Direction.CW);
            } else {
                float radiusX = ((badgeTextWidth + padding) / 2F);
                resHelper.setRoundRadius(radiusX);
                rectF.set(viewX - radiusX, viewY + radiusY, viewX + radiusX, viewY - radiusY);
                circlePath.addRoundRect(rectF, resHelper.getBackgroundCornerRadii(), Path.Direction.CCW);
            }
        }
        resHelper.drawPath(canvas, circlePath);
    
        canvas.drawText(bubbleResultText, baseX, baseY, textPaint);
    }
    
    private void drawCircle(Canvas canvas) {
        if (isBubbleCountChanged) {
            isBubbleCountChanged = false;
    
            float viewX = canvas.getWidth() / 2F;
            float viewY = canvas.getHeight() / 2F;
            float textHeight = (fontMetrics.bottom - fontMetrics.top);
            float radiusY = (textHeight / 4F);
            circlePath.reset();
            circlePath.addCircle(viewX, viewY, radiusY, Path.Direction.CW);
        }
        resHelper.drawPath(canvas, circlePath);
    }
    
    public void setBubbleCount(int count) {
        setBubbleCount(count, bubbleMaxCount);
    }
    
    public void setBubbleCount(int count, int maxCount) {
        if (count <= 0) {
            clearBubbleCount();
        } else {
            updateBubbleCount(count, maxCount);
        }
        
        if (isBubbleCountChanged) {
            postInvalidateOnAnimation();
        }
    }
    
    public void clearBubbleCount() {
        if (null != bubbleResultText) {
            isBubbleCountChanged = true;
    
            bubbleResultText = null;
            bubbleCount = 0;
        }
    }
    
    private void updateBubbleCount(int count, int maxCount) {
        if (bubbleCount != count || bubbleMaxCount != maxCount) {
            isBubbleCountChanged = true;
        
            bubbleCount = count;
            bubbleMaxCount = maxCount;
        
            if (count > maxCount) {
                bubbleResultText = String.format(Locale.getDefault(), "%d+", maxCount);
            } else {
                bubbleResultText = String.valueOf(bubbleCount);
            }
        }
    }
}
