package com.hudson.customview.wraplayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 按照顺序从左到右、从上到下排列的layout，有点类似Flutter的wrap
 * Created by Hudson on 2020/5/28.
 */
public class WrapLayout extends ViewGroup {
    public WrapLayout(Context context) {
        this(context, null);
    }

    public WrapLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrapLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthLimit = MeasureSpec.getSize(widthMeasureSpec);
        int childCount = getChildCount();
        View child;
        int widthUsed = 0;
        int heightUsed = 0;
        int maxWidth = 0;
        int levelMaxHeight = 0;// 当前行中目前的最大高度
        int childNeedWidth,childNeedHeight;
        MarginLayoutParams layoutParams;
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            measureChildWithMargins(child,widthMeasureSpec,0,heightMeasureSpec,heightUsed);
            layoutParams = (MarginLayoutParams) child.getLayoutParams();
            childNeedWidth = layoutParams.leftMargin + layoutParams.rightMargin + child.getMeasuredWidth();
            childNeedHeight = layoutParams.topMargin + layoutParams.bottomMargin + child.getMeasuredHeight();

            // 超过了所制定的最大width，因此换行，同时height增加
            if(widthUsed + childNeedWidth > widthLimit){
                heightUsed += levelMaxHeight;
                levelMaxHeight = 0;
                maxWidth = Math.max(widthUsed,maxWidth);
                widthUsed = childNeedWidth;
                measureChildWithMargins(child,widthMeasureSpec,0,heightMeasureSpec,heightUsed);
                childNeedHeight = layoutParams.topMargin + layoutParams.bottomMargin + child.getMeasuredHeight();
            }else{
                widthUsed += childNeedWidth;
            }
            if(childNeedHeight > levelMaxHeight){
                levelMaxHeight = childNeedHeight;
            }
            // 如果最后一个，那么需要判断和累加height
            if(i == childCount - 1){
                heightUsed += levelMaxHeight;
                maxWidth = Math.max(widthUsed,maxWidth);
            }
        }

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int realWidth = 0,realHeight = 0;
        switch (widthMode){
            case MeasureSpec.EXACTLY:
                realWidth = widthSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                realWidth = maxWidth;
                break;
            case MeasureSpec.AT_MOST:
                realWidth = Math.min(widthSize,maxWidth);
                break;
        }
        switch (heightMode){
            case MeasureSpec.EXACTLY:
                // 如果父view指定了给定大小，那么必须为该值
                realHeight = heightSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                // 由子view自行决定大小
                realHeight = heightUsed;
                break;
            case MeasureSpec.AT_MOST:
                // 可以选择父view给的大小和选择自身测量的大小，需要选择更小者
                realHeight = Math.min(heightSize,heightUsed);
        }
//        super.onMeasure(MeasureSpec.makeMeasureSpec(realWidth,widthMode),
//                MeasureSpec.makeMeasureSpec(realHeight,heightMode));
        setMeasuredDimension(realWidth,realHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int widthLimit = getMeasuredWidth();
        int childCount = getChildCount();
        View child;
        int widthUsed = 0;
        int heightUsed = 0;
        int levelMaxHeight = 0;
        int childNeedWidth,childNeedHeight;
        MarginLayoutParams layoutParams;
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            if(child.getVisibility() == GONE){
                continue;
            }
            layoutParams = (MarginLayoutParams) child.getLayoutParams();
            childNeedWidth = layoutParams.leftMargin + layoutParams.rightMargin + child.getMeasuredWidth();
            childNeedHeight =  layoutParams.topMargin + layoutParams.bottomMargin + child.getMeasuredHeight();
            boolean needNextLevel = widthUsed + childNeedWidth > widthLimit;
            if(needNextLevel){
                heightUsed += levelMaxHeight;
                levelMaxHeight = 0;
                widthUsed = 0;
            }
            if(childNeedHeight > levelMaxHeight){
                levelMaxHeight = childNeedHeight;
            }
            child.layout(widthUsed + layoutParams.leftMargin,heightUsed + layoutParams.topMargin,
                    widthUsed + layoutParams.leftMargin + child.getMeasuredWidth(),
                    heightUsed + layoutParams.topMargin + child.getMeasuredHeight());
            widthUsed += childNeedWidth;
        }
    }

    // 确保LayoutParams必须是MarginLayoutParams
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(),attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }
}
