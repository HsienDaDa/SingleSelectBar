package com.supermumu.ui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
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
    private static final float TOTAL_TRANSITION_COUNT = 15.0F;
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
    
    private float transitionX;
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
        if (colorSelected == -1 && a.hasValue(R.styleable.SingleSelectBoard_boardColorSelected)) {
            colorSelected = a.getColor(R.styleable.SingleSelectBoard_boardColorSelected, 0);
        }
    
        int colorUnselected;
        if (a.hasValue(R.styleable.SingleSelectBoard_boardColorUnselected)) {
            colorUnselected = a.getColor(R.styleable.SingleSelectBoard_boardColorUnselected, 0);
        } else {
            colorUnselected = ContextCompat.getColor(context, R.color.unselected_theme_color);
        }
        
        float boardElevation;
        if (a.hasValue(R.styleable.SingleSelectBoard_boardElevation)) {
            boardElevation = a.getDimension(R.styleable.SingleSelectBoard_boardElevation, 0);
        } else {
            boardElevation = context.getResources().getDimension(R.dimen.single_select_board_elevation);
        }
        
        if (a.hasValue(R.styleable.SingleSelectBoard_boardStrokeWidth)) {
            dividerWidth = a.getDimensionPixelSize(R.styleable.SingleSelectBoard_boardStrokeWidth, 0);
        } else {
            dividerWidth = context.getResources().getDimensionPixelSize(R.dimen.single_select_board_stroke_width);
        }
        a.recycle();
    
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(boardElevation);
        }
        int roundRadius = context.getResources().getDimensionPixelSize(R.dimen.single_select_board_radius);
        selectBoardResHelper = new SelectBoardResHelper(colorSelected, colorUnselected, roundRadius, dividerWidth);
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
        if (dividerRect.left == 0) {
            dividerRect.left = -1;
        }
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
            drawSelectedButtonFinish(canvas, currentView);
        } else {
            float bothViewsDistance = (currentView.getX() - previousView.getX());
            transitionX += (bothViewsDistance / TOTAL_TRANSITION_COUNT);
            if (isEndTransition(transitionX - bothViewsDistance)) {
                drawSelectedButtonFinish(canvas, currentView);
                previousPos = currentPos;
            } else {
                drawSelectedButton(canvas, previousView, previousPos);
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
    }
    
    private void drawSelectedButtonFinish(Canvas canvas, View view) {
        transitionX = 0F;
        drawSelectedButton(canvas, view, currentPos);
    }
    
    private int getSelectedColorFromStyle(Context context) {
        int[] textAttrs = {android.R.attr.textColor};
        int[] selectedColorAttrs = {android.R.attr.state_selected};
        
        final TypedArray ta = context.obtainStyledAttributes(boardTextAppearance, textAttrs);
        int colorSelected = -1;
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
        final float left = view.getX() + transitionX;
        final float top = 0;
        final float right = left + view.getMeasuredWidth();
        final float bottom = getMeasuredHeight();
        if (index == 0) {
            selectedRectF.set(left, top, right, bottom);
            selectedPath.addRoundRect(selectedRectF, selectBoardResHelper.getStartCornerRadii(), Path.Direction.CCW);
        } else if (index == visibleButtonCount - 1) {
            selectedRectF.set(left, top, right, bottom);
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
     * @param textAppearance The style of board text.
     */
    public void setItemTextAppearance(@StyleRes int textAppearance) {
        boardTextAppearance = textAppearance;
    
        int colorSelected = getSelectedColorFromStyle(getContext());
        if (colorSelected >= 0) {
            selectBoardResHelper.setColorSelected(colorSelected);
        }
        
        for (TextView view : itemViews) {
            TextViewCompat.setTextAppearance(view, textAppearance);
        }
    }
    
    /**
     * Set a selected color for selector and others.
     *
     * @param color The color of selected.
     */
    public void setSelectedColor(@ColorInt int color) {
        if (selectBoardResHelper.setColorSelected(color)) {
            invalidate();
            invalidBackground();
            invalidTextColor();
        }
    }
    
    /**
     * Set an unselected color for non-selector.
     *
     * @param color The color of unselected.
     */
    public void setUnselectedColor(@ColorInt int color) {
        if (selectBoardResHelper.setColorUnselected(color)) {
            invalidate();
            invalidBackground();
            invalidTextColor();
        }
    }
    
    /**
     * Set a dimension for board and divider.
     *
     * @param width The width of board and divider.
     */
    public void setBoardStrokeWidth(@Dimension int width) {
        if (selectBoardResHelper.setBoardStrokeWidth(width)) {
            invalidate();
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
