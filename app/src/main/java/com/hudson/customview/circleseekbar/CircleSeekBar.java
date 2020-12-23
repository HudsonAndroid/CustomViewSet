package com.hudson.customview.circleseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.hudson.customview.R;

public class CircleSeekBar extends View {
    // default value
    private static final float DEFAULT_REACH_STROKE_WIDTH = 6;//dp
    private static final float DEFAULT_UNREACH_STROKE_WIDTH = 9;//dp
    private static final int DEFAULT_REACH_COLOR = 0xff42aa0f;
    private static final int DEFAULT_UNREACH_COLOR = 0x55000000;
    private static final int DEFAULT_PADDING = 20;//dp
    private int radius;
    private float reach_stroke_width;
    private float unreach_stroke_width;
    private int reach_color;
    private int unreach_color;
    private float padding;
    private int realViewWidth;
    private Paint arcReachPaint;
    private Paint arcUnReachPaint;
    private Paint barPaint;
    private Bitmap barBitmap;
    private RectF mrecF;
    private float centerX,centerY;
    private float barBitmapCenterX,barBitmapCenterY;
    private double touchPositionAngle;
    private double percent;
    private boolean isValidTouch;

    public CircleSeekBar(Context context) {
        this(context, null);
    }

    public CircleSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context,AttributeSet attrs) {
        reach_stroke_width = dp2px(DEFAULT_REACH_STROKE_WIDTH,context);
        unreach_stroke_width = dp2px(DEFAULT_UNREACH_STROKE_WIDTH,context);
        unreach_color = DEFAULT_UNREACH_COLOR;
        reach_color = DEFAULT_REACH_COLOR;
        padding = dp2px(DEFAULT_PADDING, context);
        radius = (int) (getScreenWidth()/2-padding);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleSeekBar);
        radius = (int) ta.getDimension(R.styleable.CircleSeekBar_radius, radius);
        centerX = padding+radius;
        centerY = padding+radius;
        barBitmapCenterX = radius;
        barBitmapCenterY = 0;
        reach_stroke_width = ta.getDimension(R.styleable.CircleSeekBar_reach_stroke_width, reach_stroke_width);
        unreach_stroke_width = ta.getDimension(R.styleable.CircleSeekBar_unreach_stroke_width, unreach_stroke_width);
        reach_color = ta.getColor(R.styleable.CircleSeekBar_reach_color, reach_color);
        unreach_color = ta.getColor(R.styleable.CircleSeekBar_unreach_color, unreach_color);
        padding = ta.getDimension(R.styleable.CircleSeekBar_padding, padding);
        padding = (padding<DEFAULT_PADDING)?DEFAULT_PADDING:padding;
        ta.recycle();
        arcReachPaint = new Paint();
        arcReachPaint.setAntiAlias(true);
        arcReachPaint.setDither(true);
        arcReachPaint.setColor(reach_color);
        arcReachPaint.setStyle(Style.STROKE);
        arcReachPaint.setStrokeWidth(reach_stroke_width);
        arcUnReachPaint = new Paint();
        arcUnReachPaint.setAntiAlias(true);
        arcUnReachPaint.setColor(unreach_color);
        arcUnReachPaint.setStyle(Style.STROKE);
        arcUnReachPaint.setStrokeWidth(unreach_stroke_width);
        barBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_seekbar_indicator);
        mrecF = new RectF(padding, padding, padding+radius*2, padding+radius*2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if(widthMode == MeasureSpec.EXACTLY){
            realViewWidth = widthSize;
        }else {
            realViewWidth = (int) (padding*2+radius*2);
            if(widthMode == MeasureSpec.AT_MOST){
                realViewWidth = Math.min(widthSize, realViewWidth);
            }
        }
        setMeasuredDimension(realViewWidth, realViewWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(mrecF, -90, 360, false, arcUnReachPaint);
        canvas.drawArc(mrecF, -90, (int)touchPositionAngle, false, arcReachPaint);
        canvas.drawBitmap(barBitmap, barBitmapCenterX-barBitmap.getWidth()/2+padding,barBitmapCenterY+padding - barBitmap.getHeight()/2, barPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float up_xoffset = event.getX() - centerX;
        float up_yoffset = event.getY() - centerY;
        float Offset = up_xoffset * up_xoffset + up_yoffset * up_yoffset
                - radius * radius;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (Math.abs(Offset) < 12000) {
                    isValidTouch = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                isValidTouch = false;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(Offset) < 12000) {
                    if(event.getAction()==MotionEvent.ACTION_MOVE&&!isValidTouch){
                        return true;
                    }
                    double tmp = 0;
                    if (up_xoffset >= 0 && up_yoffset <= 0) {// right top
                        tmp = getArcSin(up_xoffset, up_yoffset);
                        touchPositionAngle = tmp;
                        barBitmapCenterX = (float) (radius * (1 + Math.sin(tmp)));
                        barBitmapCenterY = (float) (radius * (1 - Math.cos(tmp)));
                    } else if (up_xoffset > 0 && up_yoffset > 0) {// right bottom
                        tmp = getArcSin(up_yoffset, up_xoffset);
                        touchPositionAngle = tmp + Math.PI / 2;
                        barBitmapCenterX = (float)(radius*(1+Math.cos(tmp)));
                        barBitmapCenterY = (float)(radius*(1+Math.sin(tmp)));
                    } else if (up_xoffset < 0 && up_yoffset > 0) {// left bottom
                        tmp = getArcSin(Math.abs(up_xoffset), up_yoffset);
                        touchPositionAngle = Math.PI + tmp;
                        barBitmapCenterX = (float)(radius*(1-Math.sin(tmp)));
                        barBitmapCenterY = (float)(radius*(1+Math.cos(tmp)));
                    } else if (up_xoffset <= 0 && up_yoffset <= 0) {//left top
                        tmp = getArcSin(Math.abs(up_yoffset),Math.abs(up_xoffset));
                        touchPositionAngle = Math.PI* 3/ 2+ tmp;
                        barBitmapCenterX = (float)(radius*(1-Math.cos(tmp)));
                        barBitmapCenterY = (float)(radius*(1-Math.sin(tmp)));
                    }
                    percent = touchPositionAngle / (2 * Math.PI);
                    touchPositionAngle = touchPositionAngle*180/Math.PI;
                    if(progressBarChangeListener!=null){
                        progressBarChangeListener.onProgressBarChange();
                    }
                    invalidate();
                }
                break;
        }
        return true;
    }

    public double getTouchPercent(){
        return percent;
    }

    private int duration;
    public void setMax(int duration){
        this.duration = duration;
    }

    public void updateProgress(int curProgress){
        touchPositionAngle = curProgress*1.0f/duration*360;
        double tmp = touchPositionAngle *Math.PI/180;
        if(touchPositionAngle<=90){
            barBitmapCenterX = (float) (radius*(Math.sin(tmp)+1));
            barBitmapCenterY = (float)(radius*(1-Math.cos(tmp)));
        }else if(touchPositionAngle <= 180){
            tmp = tmp - Math.PI/2;
            barBitmapCenterX = (float) (radius*(Math.cos(tmp)+1));
            barBitmapCenterY = (float)(radius*(1+Math.sin(tmp)));
        }else if(touchPositionAngle <= 270){
            tmp = tmp - Math.PI;
            barBitmapCenterX = (float) (radius*(1-Math.sin(tmp)));
            barBitmapCenterY = (float)(radius*(1+Math.cos(tmp)));
        }else {
            tmp = tmp - Math.PI*3/2;
            barBitmapCenterX = (float) (radius*(1-Math.cos(tmp)));
            barBitmapCenterY = (float)(radius*(1-Math.sin(tmp)));
        }
        invalidate();
    }

    private UserSelectProgressListener progressBarChangeListener;
    public void setOnUserSelectProgressListener(UserSelectProgressListener listener){
        progressBarChangeListener = listener;
    }
    public interface UserSelectProgressListener {
        void onProgressBarChange();
    }
    /**
     * 获取对应的sin反三角
     * x是角所对应的边
     */
    private double getArcSin(float x, float y) {
        double longWay = Math.sqrt(x * x + y * y);
        return Math.asin(x / longWay);
    }

    /*
     * dp and px
     */
    public  int dp2px(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public  float dp2px(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public  int sp2px(int sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                context.getResources().getDisplayMetrics());
    }

    public int getScreenWidth(){
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        return screenWidth;
    }
}
