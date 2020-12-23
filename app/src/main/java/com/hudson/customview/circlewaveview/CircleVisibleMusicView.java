package com.hudson.customview.circlewaveview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Hudson on 2019/1/20.
 */
public class CircleVisibleMusicView extends View {
    private static final int DEFAULT_CAKE_COUNT = 120;//块个数
    private static final int DEFAULT_CAKE_COLOR = Color.RED;//默认的频谱块颜色
    private static final int DEFAULT_CAKE_MAX_HEIGHT = 50;//dp,默认的控件外围预留区域
    private static final int DEFAULT_CAKE_MAIN_ALPHA = 255;//默认外部透明度
    private static final int DEFAULT_CAKE_SECOND_ALPHA = 150;//辅助部分透明度
    private static final int DEFAULT_MAIN_SECOND_SPACE = 10;//dp
    private static final int DEFAULT_CAKE_MIN_HEIGHT = 1;//dp
    private static final float DEFAULT_SECOND_RATE = 0.4f;
    private static final float DEFAULT_CAKE_WIDTH = 5;//dp
    private int[] mDatas;
    private int mCakeCount;
    private float mAverageDegree;
    private int mCakeMaxHeight;
    private int mCakeMinHeight;
    private int mCenterX;
    private int mCenterY;
    private int mDrawTop;
    private int[] mColors;
    private Paint mPaint;
    private int mMainAlpha;
    private int mSecondAlpha;
    private int mMainSecondSpace;
    private float mSecondRate;
    private float mCakeWidth;
    private int mSpaceRadius;

    private boolean mRotateColor = false;
    private int mColorOffset = 0;

    public CircleVisibleMusicView(Context context) {
        this(context, null);
    }

    public CircleVisibleMusicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleVisibleMusicView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mCakeMaxHeight = dp2px(context,DEFAULT_CAKE_MAX_HEIGHT);
        mCakeMinHeight = dp2px(context,DEFAULT_CAKE_MIN_HEIGHT);
        mCakeWidth = dp2px(context,DEFAULT_CAKE_WIDTH);
        mCakeCount = DEFAULT_CAKE_COUNT;
        mAverageDegree = 360 / mCakeCount;
        mColors = new int[]{DEFAULT_CAKE_COLOR};
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(mCakeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mMainAlpha = DEFAULT_CAKE_MAIN_ALPHA;
        mSecondAlpha = DEFAULT_CAKE_SECOND_ALPHA;
        mMainSecondSpace = dp2px(context,DEFAULT_MAIN_SECOND_SPACE);
        mSecondRate = DEFAULT_SECOND_RATE;
        mDatas = new int[]{};
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w/2 + getPaddingLeft();
        mCenterY = h/2 + getPaddingTop();
        int width = w - getPaddingLeft() - getPaddingRight();
        int height = w - getPaddingTop() - getPaddingBottom();
        mSpaceRadius = Math.min(width,height)/2- mCakeMaxHeight;
        mDrawTop = mCenterY - mSpaceRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mCakeCount; i++) {
            mPaint.setColor(getCurColor(i));
            canvas.rotate(mAverageDegree,mCenterX,mCenterY);
            if(mDatas.length-1 < i){
                drawLine(canvas,0);
            }else{
                drawLine(canvas, mDatas[i]);
            }
        }
        if(mRotateColor){
            mColorOffset ++;
            if(mColorOffset >= mCakeCount){
                mColorOffset = 0;
            }
        }
    }

    private int getCurColor(int i) {
        return ColorHelper.evaluate(
                (i+mColorOffset)%mCakeCount*1.0f/mCakeCount, mColors);
    }

    public void drawLine(Canvas canvas, int height){
        //绘制main
        mPaint.setAlpha(mMainAlpha);
        height = (height > mCakeMaxHeight)?mCakeMaxHeight:height;
        height = (height < mCakeMinHeight)?mCakeMinHeight:height;
        int startTop = mDrawTop - mMainSecondSpace/2;
        canvas.drawLine(mCenterX,startTop,mCenterX,startTop - height,mPaint);
        //绘制辅助部分
        mPaint.setAlpha(mSecondAlpha);
        startTop = mDrawTop + mMainSecondSpace/2;
        canvas.drawLine(mCenterX,startTop,mCenterX,startTop + height*mSecondRate,mPaint);
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setDatas(int[] datas){
        mDatas = datas;
        invalidate();
    }

    /**
     * 设置颜色
     * @param colors
     */
    public void setColors(int[] colors) {
        mColors = new int[colors.length+1];
        int length = mColors.length;
        for (int i = 0; i < length; i++) {
            if(i == length-1){//颜色需要首尾相接
                mColors[i] = colors[0];
            }else{
                mColors[i] = colors[i];
            }
        }
        invalidate();
    }

    /**
     * 设置cake个数
     * 注意：设置的个数需要是能整除360
     * @param cakeCount 个数
     * @return 实际个数
     */
    public int setCakeCount(int cakeCount){
        mCakeCount = 360 / (360 / cakeCount);
        mAverageDegree = 360 / mCakeCount;
        return mCakeCount;
    }

    public void setRotateColor(boolean rotate){
        mRotateColor = rotate;
    }

    public void setCakeWidth(float cakeWidth) {
        mCakeWidth = cakeWidth;
        mPaint.setStrokeWidth(mCakeWidth);
        invalidate();
    }

    public int[] getColors(){
        return mColors;
    }

    public void updateVisualizer(byte[] fft) {
        int[] model = new int[fft.length / 2 + 1];
        model[0] =  Math.abs(fft[0]);
        for (int i = 2, j = 1; j < mCakeCount;) {
            model[j] = (int) Math.hypot(fft[i], fft[i + 1]);
            i += 2;
            j++;
        }
        setDatas(model);
    }

    public void setMainAlpha(int mainAlpha) {
        mMainAlpha = mainAlpha;
    }

    public void setSecondAlpha(int secondAlpha) {
        mSecondAlpha = secondAlpha;
    }

    public void setMainSecondSpace(int mainSecondSpace) {
        mMainSecondSpace = mainSecondSpace;
    }

    public void setSecondRate(float secondRate) {
        mSecondRate = secondRate;
    }

    public void setCakeMaxHeight(int cakeMaxHeight) {
        mCakeMaxHeight = cakeMaxHeight;
    }

    public void setCakeMinHeight(int cakeMinHeight) {
        mCakeMinHeight = cakeMinHeight;
    }

    public int getSpaceRadius() {
        return mSpaceRadius;
    }

    public int getCurColor(){
        return getCurColor(0);
    }
}
