package com.hudson.customview.circlewaveview.wave;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.hudson.customview.circlewaveview.ColorHelper;

/**
 * Created by hpz on 2019/1/22.
 */
public class CircleWave implements IDrawEntity{
    public static float RADIUS_END = 800f;
    public static long DURATION = 2000;
    private static final int MAX_INSTANCE_COUNT = 7;//复用池中复用对象最大数量
    private static int sRecycledCount = 0;//当前可复用的对象个数
    private static CircleWave sRecycleListHead;//复用链表表头(下一个被使用的复用对象)
    private CircleWave mNext;//下一个对象
    private float mRadius;
    private float mCenterX,mCenterY;
    private ValueAnimator mAnimator;
    private int mStartColor;
    private int mEndColor;
    //是否是被回收了准备复用的对象
    private boolean mIsRecycled = false;
    //是否是已经无用的对象
    private boolean mIsUnused = false;

    public void init(float centerX,float centerY,float radius, int color){
        mCenterX = centerX;
        mCenterY = centerY;
        mRadius = radius;
        mStartColor = color;
        mEndColor = ColorHelper.getTransparentColor(mStartColor);
        mAnimator = ValueAnimator.ofFloat(0f,1.0f);
        mAnimator.setDuration(DURATION);
        mAnimator.start();
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                recycle();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void draw(Canvas canvas,Paint paint) {
        if(!isRecycled()){
            float fraction = mAnimator.getAnimatedFraction();
            float radius = (RADIUS_END - mRadius)* fraction + mRadius;
            paint.setColor(ColorHelper.evaluate(fraction, mStartColor,mEndColor));
            canvas.drawCircle(mCenterX,mCenterY,radius,paint);
        }
    }

    /**
     * 获取对象
     * @param radius
     * @param color
     * @return
     */
    public static CircleWave obtain(float centerX,float centerY,float radius, int color){
        final CircleWave item;
        if(sRecycleListHead == null){
            Log.d("CircleWave","create a new instance of CircleWave");
            item = new CircleWave();
        }else{//如果回收链表存在，则从表头开始拿对象
            Log.d("CircleWave","use a recycle instance");
            item = sRecycleListHead;
            sRecycleListHead = item.mNext;
            sRecycledCount --;
            item.mNext = null;
        }
        item.mIsRecycled = false;
        item.init(centerX,centerY,radius,color);
        return item;
    }

    /**
     * 回收对象
     */
    public void recycle(){
        if(sRecycledCount < MAX_INSTANCE_COUNT){
            Log.d("CircleWave","add an instance into recycle list");
            this.mNext = sRecycleListHead;
            sRecycleListHead = this;
            sRecycledCount ++;
            mIsRecycled = true;
        }else{
            mIsUnused = true;
        }
    }

    /**
     * 清空复用池
     */
    public static void cleanRecycleList(){
        Log.d("CircleWave","clean out recycle list");
        while (sRecycleListHead != null){
            CircleWave item = sRecycleListHead;
            sRecycleListHead = item.mNext;
            item.mIsRecycled = false;
            item.mNext = null;
            item = null;
        }
        sRecycledCount = 0;
    }

    /**
     * 当前对象是否被回收
     * @return true已被回收
     */
    public boolean isRecycled() {
        return mIsRecycled;
    }

    /**
     * 当前对象是否已经被废弃
     * @return true表示对象被废弃
     */
    public boolean isUnused() {
        return mIsUnused;
    }
}
