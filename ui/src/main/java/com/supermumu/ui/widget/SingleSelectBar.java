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
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supermumu.R;

import java.util.List;
import java.util.Locale;

/**
 * Created by hsienhsu on 2017/9/20.
 */

public class SingleSelectBar extends LinearLayout {
    private static final int ANIMATION_DURATION = 550;
    private static final float START_TRANSITION_THRESHOLD = 0.05F;
    private static final float END_TRANSITION_THRESHOLD = 0.95F;
    private static final int MIN_COUNT = 2;
    private static final int MAX_COUNT = 5;
    private Tab[] tabs;
    
    private int itemTextAppearance;
    
    private ValueAnimator scrollAnimator;
    
    public interface OnTabSelectListener {
        void onSelect(int position, View view);
    }
    
    private OnTabSelectListener itemSelectListener;
    
    private ResHelper resHelper;
    private int visibleButtonCount;
    private int dividerWidth;
    
    private Rect dividerRect = new Rect();
    private RectF selectedRectF = new RectF();
    private Path selectedPath = new Path();
    
    private float transitionOffsetPos;
    private float animatorValue;
    
    private int currentPos = 0;
    private int previousPos = 0;
    private int transitionPos = 0;
    
    public SingleSelectBar(Context context) {
        super(context);
        init(context, null);
    }
    
    public SingleSelectBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    
    public SingleSelectBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    
    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        initSelectBarThemeAttributes(context, attrs);
        setTabOrientation(HORIZONTAL);
        updateBackground();
    
        tabs = new Tab[MAX_COUNT];
        buildTabView(context);
    }
    
    private void buildTabView(Context context) {
        ColorStateList colorStateList = resHelper.getTextColorStateList();
        for (int i=0; i<MAX_COUNT; i++) {
            LayoutParams lp = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1);
            
            TabView view = new TabView(context);
            view.setLayoutParams(lp);
            view.setTabTextAppearance(itemTextAppearance);
            view.setTabTextColor(colorStateList);
            view.setOnClickListener(clickListener);
            addView(view);
            
            Tab tab = new Tab();
            tab.view = view;
            tab.setPosition(i);
            tabs[i] = tab;
        }
    }
    
    private void initSelectBarThemeAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SingleSelectBar,
                0,
                0
        );
        
        itemTextAppearance = a.getResourceId(R.styleable.SingleSelectBar_uiTextAppearance, R.style.TextAppearance_TabText);
        
        // Text colors come from the text appearance first
        int colorSelected = getSelectedColorFromStyle(context);
        if (colorSelected == -1 && a.hasValue(R.styleable.SingleSelectBar_uiColorSelected)) {
            colorSelected = a.getColor(R.styleable.SingleSelectBar_uiColorSelected, 0);
        }
    
        int colorUnselected;
        if (a.hasValue(R.styleable.SingleSelectBar_uiColorUnselected)) {
            colorUnselected = a.getColor(R.styleable.SingleSelectBar_uiColorUnselected, 0);
        } else {
            colorUnselected = ContextCompat.getColor(context, R.color.unselected_theme_color);
        }
        
        if (a.hasValue(R.styleable.SingleSelectBar_uiStrokeWidth)) {
            dividerWidth = a.getDimensionPixelSize(R.styleable.SingleSelectBar_uiStrokeWidth, 0);
        } else {
            dividerWidth = context.getResources().getDimensionPixelSize(R.dimen.single_select_tab_stroke_width);
        }
        
        int pressedEffectMode = a.getInt(R.styleable.SingleSelectBar_uiPressedEffectMode, -1);
        
        a.recycle();
    
        int roundRadius = context.getResources().getDimensionPixelSize(R.dimen.single_select_tab_radius);
        resHelper = new ResHelper(colorSelected, colorUnselected, roundRadius, dividerWidth, pressedEffectMode);
    }
    
    private int getSelectedColorFromStyle(Context context) {
        int[] textAttrs = {android.R.attr.textColor};
        int[] selectedColorAttrs = {android.R.attr.state_selected};
        
        final TypedArray ta = context.obtainStyledAttributes(itemTextAppearance, textAttrs);
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
    public void setOrientation(int orientation) {
        // force override this to ignore orientation change after init
    }
    
    private void setTabOrientation(int orientation) {
        super.setOrientation(orientation);
        
        if (orientation == LinearLayout.HORIZONTAL) {
            setGravity(Gravity.CENTER_VERTICAL);
        } else {
            setGravity(Gravity.CENTER_HORIZONTAL);
        }
        resHelper.setOrientation(orientation);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    
        drawDividers(canvas);
        dispatchDrawSelectedTabs(canvas);
    }
    
    private void drawDividers(Canvas canvas) {
        if (getOrientation() == HORIZONTAL) {
            drawHorizontalDividers(canvas);
        } else {
            drawVerticalDividers(canvas);
        }
    }
    
    private void drawHorizontalDividers(Canvas canvas) {
        dividerRect.top = 0;
        dividerRect.bottom = getMeasuredHeight();
        dividerRect.left = - (dividerWidth / 2);
        if (dividerRect.left == 0) {
            dividerRect.left = -1;
        }
        dividerRect.right = 0;
        
        for (Tab tab : tabs) {
            if (tab.getPosition() > 0 && tab.getVisibility() == View.VISIBLE) {
                resHelper.drawRect(canvas, dividerRect);
            }
    
            dividerRect.left += tab.view.getMeasuredWidth();
            dividerRect.right = dividerRect.left + dividerWidth;
        }
    }
    
    private void drawVerticalDividers(Canvas canvas) {
        dividerRect.top = - (dividerWidth / 2);
        if (dividerRect.top == 0) {
            dividerRect.top = -1;
        }
        dividerRect.bottom = 0;
        dividerRect.left = 0;
        dividerRect.right = getMeasuredWidth();
    
        for (Tab tab : tabs) {
            if (tab.getPosition() > 0 && tab.getVisibility() == View.VISIBLE) {
                resHelper.drawRect(canvas, dividerRect);
            }
        
            dividerRect.top += tab.view.getMeasuredHeight();
            dividerRect.bottom = dividerRect.top + dividerWidth;
        }
    }
    
    private void dispatchDrawSelectedTabs(Canvas canvas) {
        if (animatorValue >= 1F) {
            transitionOffsetPos = 0F;
            transitionPos = currentPos;
            drawSelectedTab(canvas, tabs[currentPos]);
        } else {
            if (animatorValue < START_TRANSITION_THRESHOLD) {
                transitionPos = previousPos;
            } else if (animatorValue > END_TRANSITION_THRESHOLD) {
                transitionPos = currentPos;
            }
            float bothViewsDistance = getBothViewsDistance();
            transitionOffsetPos = (bothViewsDistance * animatorValue);
            drawSelectedTab(canvas, tabs[previousPos]);
        }
    }
    
    private float getBothViewsDistance() {
        if (getOrientation() == HORIZONTAL) {
            return (tabs[currentPos].view.getX() - tabs[previousPos].view.getX());
        } else {
            return (tabs[currentPos].view.getY() - tabs[previousPos].view.getY());
        }
    }
    
    private void drawSelectedTab(Canvas canvas, Tab tab) {
        if (getOrientation() == HORIZONTAL) {
            drawHorizontalSelectedTab(canvas, tab);
        } else {
            drawVerticalSelectedTab(canvas, tab);
        }
    }
    
    private void drawHorizontalSelectedTab(Canvas canvas, Tab tab) {
        final float left = tab.view.getX() + transitionOffsetPos;
        final float top = 0;
        final float right = left + tab.view.getMeasuredWidth();
        final float bottom = getMeasuredHeight();
        final boolean isStartEndAnimatorValue = (animatorValue < START_TRANSITION_THRESHOLD || animatorValue > END_TRANSITION_THRESHOLD);
    
        // change selected/unselected state in scroll transition
        for (Tab selectableTab : tabs) {
            int selectedHalfPos = (int) (selectableTab.view.getLeft() + (selectableTab.view.getMeasuredWidth() * 0.5F));
            if (right >= selectedHalfPos && left <= selectedHalfPos) {
                selectableTab.view.setSelected(true);
            } else {
                selectableTab.view.setSelected(false);
            }
        }
        
        // draw selected Tab
        selectedPath.reset();
        if (isStartEndAnimatorValue && (transitionPos == 0)) {
            selectedRectF.set(left, top, right, bottom);
            selectedPath.addRoundRect(selectedRectF, resHelper.getCornerRadii(ResHelper.CORNER_POSITION.START), Path.Direction.CCW);
            resHelper.drawPath(canvas, selectedPath);
        } else if (isStartEndAnimatorValue && (transitionPos == visibleButtonCount - 1)) {
            selectedRectF.set(left, top, right, bottom);
            selectedPath.addRoundRect(selectedRectF, resHelper.getCornerRadii(ResHelper.CORNER_POSITION.END), Path.Direction.CCW);
            resHelper.drawPath(canvas, selectedPath);
        } else {
            selectedPath.moveTo(left, top);
            selectedPath.lineTo(right, top);
            selectedPath.lineTo(right, bottom);
            selectedPath.lineTo(left, bottom);
            resHelper.drawPath(canvas, selectedPath);
        }
    }
    
    private void drawVerticalSelectedTab(Canvas canvas, Tab tab) {
        final float left = 0;
        final float top =  tab.view.getY() + transitionOffsetPos;
        final float right = getMeasuredWidth();
        final float bottom = top + tab.view.getMeasuredHeight();
        final boolean isStartEndAnimatorValue = (animatorValue < START_TRANSITION_THRESHOLD || animatorValue > END_TRANSITION_THRESHOLD);
    
        // change selected/unselected state in scroll transition
        for (Tab selectableTab : tabs) {
            int selectedHalfPos = (int) (selectableTab.view.getTop() + (selectableTab.view.getMeasuredHeight() * 0.5F));
            if (bottom >= selectedHalfPos && top <= selectedHalfPos) {
                selectableTab.view.setSelected(true);
            } else {
                selectableTab.view.setSelected(false);
            }
        }
        
        // draw selected Tab
        selectedPath.reset();
        if (isStartEndAnimatorValue && (transitionPos == 0)) {
            selectedRectF.set(left, top, right, bottom);
            selectedPath.addRoundRect(selectedRectF, resHelper.getCornerRadii(ResHelper.CORNER_POSITION.START), Path.Direction.CCW);
            resHelper.drawPath(canvas, selectedPath);
        } else if (isStartEndAnimatorValue && (transitionPos == visibleButtonCount - 1)) {
            selectedRectF.set(left, top, right, bottom);
            selectedPath.addRoundRect(selectedRectF, resHelper.getCornerRadii(ResHelper.CORNER_POSITION.END), Path.Direction.CCW);
            resHelper.drawPath(canvas, selectedPath);
        } else {
            selectedPath.moveTo(left, top);
            selectedPath.lineTo(right, top);
            selectedPath.lineTo(right, bottom);
            selectedPath.lineTo(left, bottom);
            resHelper.drawPath(canvas, selectedPath);
        }
    }
    
    /**
     * Set a new list of text to tabs. The texts display give title.
     *
     * @param list The list of text to display for all selectors.
     *
     * @throws IndexOutOfBoundsException &nbsp;
     */
    public void setTabs(@NonNull List<CharSequence> list) {
        setTabs(list, 0);
    }
    
    /**
     * Set a new list of text to tabs with target position. The texts display give title.
     *
     * @param list The list of text to display for all selectors.
     * @param selectorPos A default position to select after data change.
     *
     * @throws IndexOutOfBoundsException &nbsp;
     */
    public void setTabs(@NonNull List<CharSequence> list, @IntRange(from = 0, to = MAX_COUNT - 1) int selectorPos) {
        visibleButtonCount = checkButtonCount(list.size());
        
        for (Tab tab : tabs) {
            int tabPos = tab.getPosition();
            if (tabPos < visibleButtonCount) {
                if (tabPos == 0) {
                    updateTabTextBackground(tab, ResHelper.CORNER_POSITION.START);
                } else if (tabPos == (visibleButtonCount - 1)) {
                    updateTabTextBackground(tab, ResHelper.CORNER_POSITION.END);
                } else {
                    updateTabTextBackground(tab, ResHelper.CORNER_POSITION.CENTER);
                }
                
                tab.setText(list.get(tab.getPosition()));
            } else {
                tab.setText(null);
            }
        }
    
        setSelector(selectorPos);
    }
    
    private void updateTabTextBackground(Tab tab, ResHelper.CORNER_POSITION position) {
        if (tab.getCornerPosition() != position) {
            Drawable backgroundDrawable = resHelper.getTextBgDrawable(position);
            tab.view.setBackground(backgroundDrawable);
            tab.setCornerPosition(position);
        }
    }
    
    /**
     * Get a maximum count for select tabs.
     *
     * @return The maximum count of selectors.
     */
    public int getMaxSelectorCount() {
        return MAX_COUNT;
    }
    
    /**
     * Get a minimum count for select tabs.
     *
     * @return The minimum count of selectors.
     */
    public int getMinSelectorCount() {
        return MIN_COUNT;
    }
    
    @IntRange(from = MIN_COUNT, to = MAX_COUNT)
    private int checkButtonCount(int count) {
        if (MIN_COUNT > count || count > MAX_COUNT) {
            throw new IndexOutOfBoundsException(String.format(Locale.getDefault(), "The Tab count must be %d to %d", MIN_COUNT, MAX_COUNT));
        }
        return count;
    }
    
    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            previousPos = currentPos;
    
            for (Tab tab : tabs) {
                if (tab.view == view) {
                    currentPos = tab.getPosition();
            
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
            scrollAnimator.setInterpolator(new AnticipateOvershootInterpolator(0.6F));
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
                    tabs[currentPos].view.setSelected(true);
                }
        
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    tabs[previousPos].view.setSelected(false);
                }
            });
        }
    }
    
    /**
     * Set a text appearance for all tabs.
     *
     * @param textAppearance The style of tab text.
     */
    public void setTabTextAppearance(@StyleRes int textAppearance) {
        itemTextAppearance = textAppearance;
    
        int colorSelected = getSelectedColorFromStyle(getContext());
        if (colorSelected >= 0) {
            resHelper.setColorSelected(colorSelected);
        }
        
        for (Tab tab : tabs) {
            tab.view.setTabTextAppearance(textAppearance);
        }
    }
    
    /**
     * Set a selected color for selected tab and others.
     *
     * @param color The color of selected.
     */
    public void setSelectedColor(@ColorInt int color) {
        if (resHelper.setColorSelected(color)) {
            updateBackground();
            invalidTextColor();
            invalidate();
        }
    }
    
    /**
     * Set an unselected color for unselected tabs.
     *
     * @param color The color of unselected.
     */
    public void setUnselectedColor(@ColorInt int color) {
        if (resHelper.setColorUnselected(color)) {
            updateBackground();
            invalidTextColor();
            invalidate();
        }
    }
    
    /**
     * Set a dimension for border and divider.
     *
     * @param width The width of border and divider.
     */
    public void setTabStrokeWidth(@Dimension int width) {
        if (resHelper.setTabStrokeWidth(width)) {
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
        
        Tab tab = tabs[position];
        if (tab.getVisibility() == View.VISIBLE) {
            tab.view.callOnClick();
        }
    }
    
    /**
     * Register a callback to be invoked when this Tab view is selected.
     *
     * @param listener The callback that will run
     */
    public void setOnTabSelectListener(OnTabSelectListener listener) {
        itemSelectListener = listener;
    }
    
    private void updateBackground() {
        setBackground(resHelper.getTabBackgroundDrawable());
    }
    
    private void invalidTextColor() {
        ColorStateList colorStateList = resHelper.getTextColorStateList();
        for (Tab tab : tabs) {
            tab.view.setTabTextColor(colorStateList);
        }
    }
    
    private final class TabView extends ConstraintLayout {
    
        private TextView textView;
//        private BubbleView bubbleView;
        
        public TabView(Context context) {
            super(context);
            init(context);
        }
        
        private void init(Context context) {
            initViews(context);
            initConstraintSet(context);
        }
        
        private void initViews(Context context) {
            final int margin1X = context.getResources().getDimensionPixelSize(R.dimen.margin_1x);
            final int margin3X = context.getResources().getDimensionPixelSize(R.dimen.margin_3x);
            
            textView = new TextView(context);
            textView.setId(R.id.tab_text);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(margin3X, margin1X, margin3X, margin1X);
            textView.setMaxLines(1);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            addView(textView);
    
//            bubbleView = new BubbleView(context);
//            bubbleView.setId(R.id.tab_bubble);
//            bubbleView.setBubbleCount(11);
//            addView(bubbleView);
            
            ViewGroup.LayoutParams lp = getLayoutParams();
            if (null == lp) {
                lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1);
                setLayoutParams(lp);
            }
        }
    
        private void initConstraintSet(Context context) {
            ConstraintSet set = new ConstraintSet();
    
            int viewId = textView.getId();
            set.constrainWidth(viewId, ConstraintSet.WRAP_CONTENT);
            set.constrainHeight(viewId, ConstraintSet.WRAP_CONTENT);
            set.connect(viewId, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
            set.connect(viewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            set.connect(viewId, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
            set.connect(viewId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            
//            viewId = bubbleView.getId();
//            set.connect(viewId, ConstraintSet.TOP, R.id.tab_text, ConstraintSet.TOP);
//            set.connect(viewId, ConstraintSet.RIGHT, R.id.tab_text, ConstraintSet.RIGHT);
            
            set.applyTo(this);
        }
    
        public void setTabText(CharSequence text) {
            textView.setText(text);
        }
        
        public void setTabTextColor(ColorStateList colorStateList) {
            textView.setTextColor(colorStateList);
        }
        
        public void setTabTextAppearance(@StyleRes int textAppearance) {
            TextViewCompat.setTextAppearance(textView, textAppearance);
        }
    }
    
    private final class Tab {
        TabView view;
        
        private int position;
        private CharSequence text;
        private int visibility = View.VISIBLE;
        private ResHelper.CORNER_POSITION cornerPosition = ResHelper.CORNER_POSITION.UNSET;
    
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
    
        public ResHelper.CORNER_POSITION getCornerPosition() {
            return cornerPosition;
        }
    
        public void setCornerPosition(ResHelper.CORNER_POSITION cornerPosition) {
            this.cornerPosition = cornerPosition;
        }
    
        private void updateView() {
            if (TextUtils.isEmpty(text)) {
                visibility = View.GONE;
            } else {
                visibility = View.VISIBLE;
            }
            view.setTabText(text);
            
            if (view.getVisibility() != visibility) {
                view.setVisibility(visibility);
            }
        }
    }
}
