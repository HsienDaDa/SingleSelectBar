package com.supermumu.ui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supermumu.R;
import com.supermumu.ui.helper.SelectBoardResHelper;

import java.util.List;
import java.util.Locale;

/**
 * Created by hsienhsu on 2017/9/20.
 */

public class SingleSelectBoard extends LinearLayout {
    private static final long SCROLL_ANIMATION_DURATION_MS = 120L;
    private static final int MIN_COUNT = 2;
    private static final int MAX_COUNT = 5;
    private TextView[] itemViews;
    
    private int boardTextAppearance;
    
    public interface OnItemSelectListener {
        void onSelect(int position, View view);
    }
    
    private OnItemSelectListener itemSelectListener;
    
    private SelectBoardResHelper selectBoardResHelper;
    private int visibleButtonCount;
    private int dividerWidth;
    
    private Rect dividerRect = new Rect();
    private RectF selectedRectF = new RectF();
    private Path selectedPath = new Path();
    
    private float transitionOffset;
    private int currentPos = 0;
    private int previousPos = 0;
    
    public SingleSelectBoard(Context context) {
        super(context);
        init(context, null);
    }
    
    public SingleSelectBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    
    public SingleSelectBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    
    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        setGravity(Gravity.CENTER_VERTICAL);
        initSelectBoardThemeAttributes(context, attrs);
        invalidBackground();
    
        itemViews = new TextView[MAX_COUNT];
        buildItemView(context);
        itemViews[currentPos].setSelected(true);
    }
    
    private void buildItemView(Context context) {
        final int margin1X = context.getResources().getDimensionPixelSize(R.dimen.margin_1x);
        ColorStateList colorStateList = selectBoardResHelper.getTextColorStateList();
        
        for (int i=0; i<MAX_COUNT; i++) {
            LayoutParams lp = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1);
            
            TextView view = new TextView(context);
            itemViews[i] = view;
            view.setLayoutParams(lp);
            TextViewCompat.setTextAppearance(view, boardTextAppearance);
            view.setPadding(margin1X, margin1X, margin1X, margin1X);
            view.setGravity(Gravity.CENTER);
            view.setTextColor(colorStateList);
            view.setOnClickListener(clickListener);
            addView(view);
        }
    }
    
    private void initSelectBoardThemeAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SingleSelectBoard,
                0,
                0
        );
        
        boardTextAppearance = a.getResourceId(R.styleable.SingleSelectBoard_boardTextAppearance, R.style.TextAppearance_BoardText);
        
        // Text colors come from the text appearance first
        int colorSelected = getSelectedColorFromStyle(context);
        if (a.hasValue(R.styleable.SingleSelectBoard_colorSelected)) {
            colorSelected = a.getColor(R.styleable.SingleSelectBoard_colorSelected, 0);
        }
    
        int colorUnselected;
        if (a.hasValue(R.styleable.SingleSelectBoard_colorUnselected)) {
            colorUnselected = a.getColor(R.styleable.SingleSelectBoard_colorUnselected, 0);
        } else {
            colorUnselected = ContextCompat.getColor(context, R.color.unselected_theme_color);
        }
        
        a.recycle();
    
        int roundRadius = context.getResources().getDimensionPixelSize(R.dimen.single_select_board_radius);
        int strokeWidth = context.getResources().getDimensionPixelSize(R.dimen.single_select_board_stroke_width);
        dividerWidth = strokeWidth;
    
        selectBoardResHelper = new SelectBoardResHelper(colorSelected, colorUnselected, roundRadius, strokeWidth);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    
        drawDividers(canvas);
        drawSelectedButton(canvas);
    }
    
    private void drawDividers(Canvas canvas) {
        dividerRect.top = 0;
        dividerRect.bottom = getMeasuredHeight();
        dividerRect.left = - (dividerWidth / 2);
        dividerRect.right = 0;
    
        int dividerCount = (visibleButtonCount - 1);
        for (int i=0; i<dividerCount; i++) {
            View itemView = itemViews[i];
            dividerRect.left += itemView.getMeasuredWidth();
            dividerRect.right = dividerRect.left + dividerWidth;
            selectBoardResHelper.drawRect(canvas, dividerRect);
        }
    }
    
    private void drawSelectedButton(Canvas canvas) {
        selectedPath.reset();
    
        View currentView = itemViews[currentPos];
        final View previousView = itemViews[previousPos];
        if (previousPos == currentPos) {
            transitionOffset = 0F;
            drawSelectedButton(canvas, currentView, currentPos);
        } else {
            final int transitionCount = (Math.abs(currentPos - previousPos) * 3);
            float bothViewsDistance = (currentView.getX() - previousView.getX());
            float offset = (bothViewsDistance / transitionCount);
            transitionOffset += offset;
            if (isEndTransition(transitionOffset - bothViewsDistance)) {
                transitionOffset = 0F;
                drawSelectedButton(canvas, currentView, currentPos);
                previousPos = currentPos;
            } else {
                drawSelectedButton(canvas, previousView, previousPos);
                postInvalidateDelayed(SCROLL_ANIMATION_DURATION_MS / transitionCount);
            }
        }
    }
    
    private int getSelectedColorFromStyle(Context context) {
        int[] textAttrs = {android.R.attr.textColor};
        int[] selectedColorAttrs = {android.R.attr.state_selected};
        
        final TypedArray ta = context.obtainStyledAttributes(boardTextAppearance, textAttrs);
        int colorSelected = 0;
        try {
            ColorStateList textColors = ta.getColorStateList(0);
            if (null != textColors) {
                colorSelected = textColors.getColorForState(selectedColorAttrs, 0);
            }
        } finally {
            ta.recycle();
        }
        return colorSelected;
    }
    
    private boolean isEndTransition(float nextX) {
        if (previousPos < currentPos) {
            return (nextX >= 0);
        } else {
            return (nextX <= 0);
        }
    }
    
    private void drawSelectedButton(Canvas canvas, View view, int index) {
        final float left = view.getX() + transitionOffset;
        final float top = 0;
        final float right = left + view.getMeasuredWidth();
        final float bottom = getMeasuredHeight();
        selectedRectF.set(left, top, right, bottom);
        if (index == 0) {
            selectedPath.addRoundRect(selectedRectF, selectBoardResHelper.getStartCornerRadii(), Path.Direction.CCW);
        } else if (index == visibleButtonCount - 1) {
            selectedPath.addRoundRect(selectedRectF, selectBoardResHelper.getEndCornerRadii(), Path.Direction.CCW);
        } else {
            selectedPath.moveTo(left, top);
            selectedPath.lineTo(right, top);
            selectedPath.lineTo(right, bottom);
            selectedPath.lineTo(left, bottom);
        }
        selectBoardResHelper.drawPath(canvas, selectedPath);
    }
    
    /**
     * Set a new list of text to selected board. The texts display give title.
     *
     * @param list The list of text to display for all selectors.
     *
     * @throws IndexOutOfBoundsException &nbsp;
     */
    public void setItems(@NonNull List<CharSequence> list) {
        setItems(list, 0);
    }
    
    /**
     * Set a new list of text to selected board with targt position. The texts display give title.
     *
     * @param list The list of text to display for all selectors.
     * @param selectorPos A default position to select after data change.
     *
     * @throws IndexOutOfBoundsException &nbsp;
     */
    public void setItems(@NonNull List<CharSequence> list, @IntRange(from = 0, to = MAX_COUNT - 1) int selectorPos) {
        visibleButtonCount = checkButtonCount(list.size());
        
        for (int i=0; i<MAX_COUNT; i++) {
            TextView itemView = itemViews[i];
            if (i < visibleButtonCount) {
                itemView.setText(list.get(i));
                itemView.setVisibility(View.VISIBLE);
            } else {
                itemView.setVisibility(View.GONE);
            }
        }
    
        setSelector(selectorPos);
    }
    
    /**
     * Get a maximum count for select board.
     *
     * @return The maximum count of selectors.
     */
    public int getMaxSelectorCount() {
        return MAX_COUNT;
    }
    
    /**
     * Get a minimum count for select board.
     *
     * @return The minimum count of selectors.
     */
    public int getMinSelectorCount() {
        return MIN_COUNT;
    }
    
    @IntRange(from = MIN_COUNT, to = MAX_COUNT)
    private int checkButtonCount(int count) {
        if (MIN_COUNT > count || count > MAX_COUNT) {
            throw new IndexOutOfBoundsException(String.format(Locale.getDefault(), "The item count must be %d to %d", MIN_COUNT, MAX_COUNT));
        }
        return count;
    }
    
    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            View previousView = itemViews[currentPos];
            previousView.setSelected(false);
            previousPos = currentPos;
    
            int rightIndex = visibleButtonCount - 1;
            for (int index = rightIndex; index >= 0; index--) {
                View itemView = itemViews[index];
                if (itemView == view) {
                    currentPos = index;
                    itemView.setSelected(true);
                    invalidate();
            
                    if (null != itemSelectListener) {
                        itemSelectListener.onSelect(currentPos, view);
                    }
                    break;
                }
            }
        }
    };
    
    /**
     * Set a text appearance for all items.
     *
     * @param textAppearance A text style
     */
    public void setItemTextAppearance(@StyleRes int textAppearance) {
        boardTextAppearance = textAppearance;
    
        int colorSelected = getSelectedColorFromStyle(getContext());
        if (colorSelected > 0) {
            selectBoardResHelper.setColorSelected(colorSelected);
        }
        
        for (TextView view : itemViews) {
            TextViewCompat.setTextAppearance(view, textAppearance);
        }
    }
    
    /**
     * Set a selected color for selector and others.
     *
     * @param color A selected color.
     */
    public void setSelectedColor(@ColorInt int color) {
        if (selectBoardResHelper.setColorSelected(color)) {
            invalidate();
            invalidBackground();
            invalidTextColor();
        }
    }
    
    /**
     * Set a unselected color for non-selector.
     *
     * @param color An unselected color.
     */
    public void setUnselectedColor(@ColorInt int color) {
        if (selectBoardResHelper.setColorUnselected(color)) {
            invalidate();
            invalidBackground();
            invalidTextColor();
        }
    }
    
    /**
     * Set selector position in range.
     *
     * @param position A selector position.
     *
     * @see #getMaxSelectorCount()
     * @see #getMinSelectorCount()
     */
    public void setSelector(@IntRange(from = 0, to = MAX_COUNT - 1) int position) {
        if (position > visibleButtonCount) {
            position = (visibleButtonCount - 1);
        } else if (position < 0) {
            position = 0;
        }
        
        View itemView = itemViews[position];
        if (itemView.getVisibility() == View.VISIBLE) {
            itemView.callOnClick();
        }
    }
    
    /**
     * Register a callback to be invoked when this item view is selected.
     *
     * @param listener The callback that will run
     */
    public void setOnItemSelectListener(OnItemSelectListener listener) {
        itemSelectListener = listener;
    }
    
    private void invalidBackground() {
        Drawable boardBackground = selectBoardResHelper.getBoardBackgroundDrawable();
        setBackground(boardBackground);
    }
    
    private void invalidTextColor() {
        ColorStateList colorStateList = selectBoardResHelper.getTextColorStateList();
        for (TextView itemView : itemViews) {
            itemView.setTextColor(colorStateList);
        }
    }
}
