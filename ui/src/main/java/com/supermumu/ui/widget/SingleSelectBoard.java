package com.supermumu.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
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
    private static final int ANIMATION_DURATION = 350;
    private static final float START_TRANSITION_THRESHOLD = 0.05F;
    private static final float END_TRANSITION_THRESHOLD = 0.95F;
    private static final int MIN_COUNT = 2;
    private static final int MAX_COUNT = 5;
    private Item[] items;
    
    private int boardTextAppearance;
    
    private ValueAnimator scrollAnimator;
    
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
    private float animatorValue;
    
    private int currentPos = 0;
    private int previousPos = 0;
    private int transitionPos = 0;
    
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
        updateBackground();
    
        items = new Item[MAX_COUNT];
        buildItemView(context);
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
            view.setLayoutParams(lp);
            TextViewCompat.setTextAppearance(view, boardTextAppearance);
            view.setPadding(margin1X, margin1X, margin1X, margin1X);
            view.setGravity(Gravity.CENTER);
            view.setTextColor(colorStateList);
            view.setOnClickListener(clickListener);
            addView(view);
            
            Item item = new Item();
            item.view = view;
            item.setPosition(i);
            items[i] = item;
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
        
        if (a.hasValue(R.styleable.SingleSelectBoard_boardStrokeWidth)) {
            dividerWidth = a.getDimensionPixelSize(R.styleable.SingleSelectBoard_boardStrokeWidth, 0);
        } else {
            dividerWidth = context.getResources().getDimensionPixelSize(R.dimen.single_select_board_stroke_width);
        }
        a.recycle();
    
        int roundRadius = context.getResources().getDimensionPixelSize(R.dimen.single_select_board_radius);
        selectBoardResHelper = new SelectBoardResHelper(colorSelected, colorUnselected, roundRadius, dividerWidth);
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
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    
        drawDividers(canvas);
        dispatchDrawSelectedItems(canvas);
    
//        mDstPath.reset();
//        mDstPath.lineTo(0, 0);
//        float stopD = animatorValue * mLength;
//        float startD = 0;
//
//        //获取当前进度的路径，同时赋值给传入的mDstPath
//        startD = (float) (stopD - ((0.5 - Math.abs(animatorValue - 0.5)) * mLength));
//        mPathMeasure.getSegment(startD, stopD, mDstPath, true);
//
//        canvas.save();
//        canvas.translate(30+transitionX, 30);
//        canvas.drawPath(mDstPath, mPaint);
//        canvas.restore();
    }
    
    private void drawDividers(Canvas canvas) {
        dividerRect.top = 0;
        dividerRect.bottom = getMeasuredHeight();
        dividerRect.left = - (dividerWidth / 2);
        if (dividerRect.left == 0) {
            dividerRect.left = -1;
        }
        dividerRect.right = 0;
        
        for (Item item : items) {
            if (item.getPosition() > 0 && item.getVisibility() == View.VISIBLE) {
                selectBoardResHelper.drawRect(canvas, dividerRect);
            }
    
            dividerRect.left += item.view.getMeasuredWidth();
            dividerRect.right = dividerRect.left + dividerWidth;
        }
    }
    
    private void dispatchDrawSelectedItems(Canvas canvas) {
        if (animatorValue >= 1F) {
            transitionX = 0F;
            transitionPos = currentPos;
            previousPos = currentPos;
            drawSelectedItem(canvas, items[currentPos]);
        } else {
            if (animatorValue < START_TRANSITION_THRESHOLD) {
                transitionPos = previousPos;
            } else if (animatorValue > END_TRANSITION_THRESHOLD) {
                transitionPos = currentPos;
            }
            float bothViewsDistance = (items[currentPos].view.getX() - items[previousPos].view.getX());
            transitionX = (bothViewsDistance * animatorValue);
            drawSelectedItem(canvas, items[previousPos]);
        }
    }
    private void drawSelectedItem(Canvas canvas, Item item) {
        final float left = item.view.getX() + transitionX;
        final float top = 0;
        final float right = left + item.view.getMeasuredWidth();
        final float bottom = getMeasuredHeight();
        final boolean isStartEndAnimatorValue = (animatorValue < START_TRANSITION_THRESHOLD || animatorValue > END_TRANSITION_THRESHOLD);
    
        // change selected/unselected state in scroll transition
        for (Item selectableItem : items) {
            int selectedHalfPos = (int) (selectableItem.view.getLeft() + (selectableItem.view.getMeasuredWidth() * 0.5F));
            if (right >= selectedHalfPos && left <= selectedHalfPos) {
                selectableItem.view.setSelected(true);
            } else {
                selectableItem.view.setSelected(false);
            }
        }
        
        // draw selected item
        selectedPath.reset();
        if (isStartEndAnimatorValue && (transitionPos == 0)) {
            selectedRectF.set(left, top, right, bottom);
            selectedPath.addRoundRect(selectedRectF, selectBoardResHelper.getStartCornerRadii(), Path.Direction.CCW);
            selectBoardResHelper.drawPath(canvas, selectedPath);
        } else if (isStartEndAnimatorValue && (transitionPos == visibleButtonCount - 1)) {
            selectedRectF.set(left, top, right, bottom);
            selectedPath.addRoundRect(selectedRectF, selectBoardResHelper.getEndCornerRadii(), Path.Direction.CCW);
            selectBoardResHelper.drawPath(canvas, selectedPath);
        } else {
            selectedPath.moveTo(left, top);
            selectedPath.lineTo(right, top);
            selectedPath.lineTo(right, bottom);
            selectedPath.lineTo(left, bottom);
            selectBoardResHelper.drawPath(canvas, selectedPath);
        }
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
        
        for (Item item : items) {
            if (item.getPosition() < visibleButtonCount) {
                item.setText(list.get(item.getPosition()));
            } else {
                item.setText(null);
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
            previousPos = currentPos;
    
            for (Item item : items) {
                if (item.view == view) {
                    currentPos = item.getPosition();
            
                    ensureScrollAnimator();
                    if (scrollAnimator.isRunning()) {
                        scrollAnimator.end();
                    }
                    scrollAnimator.start();
    
                    if (previousPos == currentPos) {
                        scrollAnimator.end();
                    }
                    
                    if (null != itemSelectListener) {
                        itemSelectListener.onSelect(currentPos, view);
                    }
                    break;
                }
            }
        }
    };
    
    private void ensureScrollAnimator() {
        if (null == scrollAnimator) {
            scrollAnimator = ValueAnimator.ofFloat(0, 1);
            scrollAnimator.setDuration(ANIMATION_DURATION);
            scrollAnimator.setInterpolator(new DecelerateInterpolator());
            scrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    animatorValue = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            scrollAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    items[currentPos].view.setSelected(true);
                }
        
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    items[previousPos].view.setSelected(false);
                }
            });
        }
    }
    
//    Path mCirclePath;
//    Paint mPaint = new Paint();
//    Path mDstPath;
//    PathMeasure mPathMeasure;
//    float mLength;
//    float animatorValue;
//    ValueAnimator mValueAnimator;
//    private void test() {
//        mCirclePath = new Path();
//        //路径绘制每段截取出来的路径
//        mDstPath = new Path();
//        mPaint.setColor(Color.RED);
//        mCirclePath.addCircle(20, 20, 40, Path.Direction.CW);
//
//        //路径测量类
//        mPathMeasure = new PathMeasure();
//        //测量路径
//        mPathMeasure.setPath(mCirclePath, false);
//
//        //获取被测量路径的总长度
//        mLength = mPathMeasure.getLength();
//
//        if (null != mValueAnimator && mValueAnimator.isRunning()) {
//            mValueAnimator.end();
//        }
//        mValueAnimator = ValueAnimator.ofFloat(0, 1);
//        mValueAnimator.setDuration(ANIMATION_DURATION);
////        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
//        mValueAnimator.setInterpolator(new DecelerateInterpolator());
//        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                //获取从0-1的变化值
//                animatorValue = (float) animation.getAnimatedValue();
//                Log.d("S", "Hsien_ // [onAnimationUpdate] "+animatorValue);
//                invalidate();
//            }
//        });
//        mValueAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                Log.d("S", "Hsien_ // [onAnimationEnd] ");
//                items[currentPos].view.setSelected(true);
//            }
//
//            @Override
//            public void onAnimationStart(Animator animation) {
//                super.onAnimationStart(animation);
//                Log.d("S", "Hsien_ // [onAnimationStart] ");
//            }
//        });
//        mValueAnimator.start();
//    }
    
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
        
        for (Item item : items) {
            TextViewCompat.setTextAppearance(item.view, textAppearance);
        }
    }
    
    /**
     * Set a selected color for selector and others.
     *
     * @param color The color of selected.
     */
    public void setSelectedColor(@ColorInt int color) {
        if (selectBoardResHelper.setColorSelected(color)) {
            updateBackground();
            invalidTextColor();
            invalidate();
        }
    }
    
    /**
     * Set an unselected color for non-selector.
     *
     * @param color The color of unselected.
     */
    public void setUnselectedColor(@ColorInt int color) {
        if (selectBoardResHelper.setColorUnselected(color)) {
            updateBackground();
            invalidTextColor();
            invalidate();
        }
    }
    
    /**
     * Set a dimension for board and divider.
     *
     * @param width The width of board and divider.
     */
    public void setBoardStrokeWidth(@Dimension int width) {
        if (selectBoardResHelper.setBoardStrokeWidth(width)) {
            updateBackground();
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
        
        Item item = items[position];
        if (item.getVisibility() == View.VISIBLE) {
            item.view.callOnClick();
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
    
    private void updateBackground() {
        setBackground(selectBoardResHelper.getBoardBackgroundDrawable());
    }
    
    private void invalidTextColor() {
        ColorStateList colorStateList = selectBoardResHelper.getTextColorStateList();
        for (Item item : items) {
            item.view.setTextColor(colorStateList);
        }
    }
    
    private final class Item {
        TextView view;
        
        private int position;
        private CharSequence text;
        private int visibility = View.VISIBLE;
    
        public int getPosition() {
            return position;
        }
    
        public void setPosition(int position) {
            this.position = position;
        }
    
        public CharSequence getText() {
            return text;
        }
    
        public void setText(CharSequence text) {
            this.text = text;
            updateView();
        }
    
        public int getVisibility() {
            return visibility;
        }
        
        private void updateView() {
            if (TextUtils.isEmpty(text)) {
                visibility = View.GONE;
            } else {
                visibility = View.VISIBLE;
            }
            view.setText(text);
            
            if (view.getVisibility() != visibility) {
                view.setVisibility(visibility);
            }
        }
    }
}
