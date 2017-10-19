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
    
    private int margin2x;
    
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
    
        margin2x = getResources().getDimensionPixelSize(R.dimen.margin_2x);
        int colorSelected = Color.RED;
//        int colorSelected = ContextCompat.getColor(context, R.color.selected_theme_color);
        resHelper = new ResHelper(colorSelected, colorSelected, 0, 0);
        
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);
        fontMetrics = textPaint.getFontMetrics();
//        Log.d(getClass().getSimpleName(), "Hsienn // EXACTLY: "+MeasureSpec.EXACTLY+", AT_MOST "+MeasureSpec.AT_MOST+", UNSPECIFIED "+MeasureSpec.UNSPECIFIED);
    
        debugLinePaint.setColor(Color.RED);
    }
    
    private float getMeasureBubbleViewWidth() {
        if (null != bubbleResultText) {
            float width = (textPaint.measureText(bubbleResultText) + margin2x);
            return Math.max(width, getMeasureBubbleViewHeight());
        } else {
            return getMeasureBubbleViewHeight();
        }
    }
    
    private float getMeasureBubbleViewHeight() {
        return (fontMetrics.bottom - fontMetrics.top);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Log.d(getClass().getSimpleName(), "Hsienn // id: "+getId()+", W "+MeasureSpec.getSize(widthMeasureSpec)+", "+MeasureSpec.getMode(widthMeasureSpec));
//        Log.d(getClass().getSimpleName(), "Hsienn // id: "+getId()+", H "+MeasureSpec.getSize(heightMeasureSpec)+", "+MeasureSpec.getMode(widthMeasureSpec));
        if (MeasureSpec.getSize(widthMeasureSpec) <= 0 || MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) getMeasureBubbleViewWidth(), MeasureSpec.EXACTLY);
        }
        if (MeasureSpec.getSize(heightMeasureSpec) <= 0 || MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) getMeasureBubbleViewHeight(), MeasureSpec.EXACTLY);
        }
//        Log.w(getClass().getSimpleName(), "Hsienn // id: "+getId()+", W "+MeasureSpec.getSize(widthMeasureSpec)+", "+MeasureSpec.getMode(widthMeasureSpec));
//        Log.w(getClass().getSimpleName(), "Hsienn // id: "+getId()+", H "+MeasureSpec.getSize(heightMeasureSpec)+", "+MeasureSpec.getMode(widthMeasureSpec));
    
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    
        drawCircleWithCount(canvas);
        
        canvas.drawLine(0, canvas.getHeight() / 2, canvas.getWidth(), canvas.getHeight() / 2, debugLinePaint);
        canvas.drawLine(canvas.getWidth()/2, 0, canvas.getWidth()/2, canvas.getHeight(), debugLinePaint);
    }
    
    private void drawCircleWithCount(Canvas canvas) {
//        Log.e("A", "Hsien_ // size: " + textPaint.getTextSize());
//        Log.e("A", "Hsien_ // top: " + fontMetrics.top);
//        Log.e("A", "Hsien_ // ascent: " + textPaint.ascent() + ", " + fontMetrics.ascent);
//        Log.e("A", "Hsien_ // descent: " + textPaint.descent() + ", " + fontMetrics.descent);
//        Log.e("A", "Hsien_ // bottom: " + fontMetrics.bottom);
//        Log.e("A", "Hsien_ // 1: " + textPaint.measureText("1") + ", 11: " + textPaint.measureText("11") + ", 國: " + textPaint.measureText("國") + ", A: " + textPaint.measureText("A") + ", AA: " + textPaint.measureText("AA"));
//        Log.e("A", "Hsien_ // textHeight: " + textHeight + ", badgeTextWidth: " + badgeTextWidth + ", w: " + getMeasuredWidth() + ", h: " + getMeasuredHeight());
    
        float viewX = canvas.getWidth() / 2F;
        float viewY = canvas.getHeight() / 2F;
    
        // measure and draw bubble
        if (isBubbleCountChanged) {
            isBubbleCountChanged = false;
    
            circlePath.reset();
            
            float radiusY;
            float radiusX;
            if (null != bubbleResultText) {
                radiusY = (getMeasureBubbleViewHeight() / 2F);
                radiusX = (getMeasureBubbleViewWidth() / 2F);
            } else {
                radiusX = radiusY = (getMeasureBubbleViewHeight() / 4F);
            }
            resHelper.setRoundRadius(radiusX);
            rectF.set(viewX - radiusX, viewY + radiusY, viewX + radiusX, viewY - radiusY);
            circlePath.addRoundRect(rectF, resHelper.getBackgroundCornerRadii(), Path.Direction.CCW);
        }
        resHelper.drawPath(canvas, circlePath);
    
        // draw text
        if (null != bubbleResultText) {
            float baseX = viewX;
            float baseY = (viewY - ((textPaint.descent() + textPaint.ascent()) / 2F));
            canvas.drawText(bubbleResultText, baseX, baseY, textPaint);
        }
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
//            postInvalidateOnAnimation();
            requestLayout();
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
