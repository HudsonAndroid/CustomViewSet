package com.hudson.customview.gesturelock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/**
 * Created by Hudson on 2017/3/10.
 * 本控件是手势锁的每一个小圆圈
 */

public class LockCircle extends View {
    private Paint mPaint;//画笔
    private int mRadius;//半径
    private int mStrokeWidth=2;//画笔宽度
    private int mCenterX, mCenterY;//圆心坐标
    /**
     * 箭头（小三角最长边的一半长度 = mArrawRate * mWidth / 2 ）
     */
    private float mArrowRate = 0.333f;
    private int mArrowDegree = -1;
    private Path mArrowPath;
    /**
     * 内圆的半径 = mInnerCircleRadiusRate * mRadus
     */
    private float mInnerCircleRadiusRate = 0.3F;
    /**
     * 四个颜色
     */
    private int mStatusNoneInnerColor;
    private int mStatusNoneOutterColor;
    private int mStatusSelectingColor;
    private int mStatusSelectedColor;

    enum SelectStatus {//三种选中状态，分别是未被选中、正被选中（move），已选中(up)
        STATUS_NONE, STATUS_SELECTING, STATUS_SELECTED;
    }

    private SelectStatus mSelectStatus = SelectStatus.STATUS_NONE;

    public int getArrowDegree() {
        return mArrowDegree;
    }

    public void setArrowDegree(int arrowDegree) {
        mArrowDegree = arrowDegree;
        invalidate();
    }

    public SelectStatus getSelectStatus() {
        return mSelectStatus;
    }

    public void setSelectStatus(SelectStatus selectStatus) {
        mSelectStatus = selectStatus;
        invalidate();
    }

    public void selectComplete(int selectColor){
        mStatusSelectedColor = selectColor;
        setSelectStatus(SelectStatus.STATUS_SELECTED);
    }

    //本控件只能通过new出来，所以我们只有一个
    public LockCircle(Context context, int statusNoneInnerColor, int statusNoneOutterColor
            , int statusSelectingColor, int statusSelectedColor) {
        super(context);
        mStatusNoneInnerColor = statusNoneInnerColor;
        mStatusNoneOutterColor = statusNoneOutterColor;
        mStatusSelectingColor = statusSelectingColor;
        mStatusSelectedColor = statusSelectedColor;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArrowPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mRadius = (Math.min(w, h)) / 2;
        initArrow(mRadius);
        mRadius -= mStrokeWidth/2;
        mCenterX = w / 2;
        mCenterY = h / 2;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 初始化指示方向的三角形（三角形仅在用户选择完图形后显示出来）
     * 默认三角形是箭头朝上的等腰三角形，后面可以通过canvas.rotate来旋转
     *
     * @param referWidth 参考长度，用于设置三角形三边长
     */
    private void initArrow(int referWidth) {
        float mArrowLength = referWidth * mArrowRate;
        //2像素主要是不与外圆相交过于明显
        mArrowPath.moveTo(referWidth, mStrokeWidth + 2);//三角形顶点
        mArrowPath.lineTo(referWidth - mArrowLength, mStrokeWidth + 2
                + mArrowLength);//三角形左下角点
        mArrowPath.lineTo(referWidth + mArrowLength, mStrokeWidth + 2
                + mArrowLength);//三角形右下角点
        mArrowPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (mSelectStatus) {
            case STATUS_NONE://绘制普通状态下
                // 绘制外圆
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mStatusNoneOutterColor);
                canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
                // 绘制内圆
                mPaint.setColor(mStatusNoneInnerColor);
                canvas.drawCircle(mCenterX, mCenterY, mRadius
                        * mInnerCircleRadiusRate, mPaint);
                break;
            case STATUS_SELECTING://绘制用户正在绘制手势并且本控件被选中
                // 绘制外圆
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(mStatusSelectingColor);
                mPaint.setStrokeWidth(mStrokeWidth);
                canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
                // 绘制内圆
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(mCenterX, mCenterY, mRadius
                        * mInnerCircleRadiusRate, mPaint);
                break;
            case STATUS_SELECTED://绘制用户选择完毕后本选择的本控件
                // 绘制外圆
                mPaint.setColor(mStatusSelectedColor);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(mStrokeWidth);
                canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
                // 绘制内圆
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(mCenterX, mCenterY, mRadius
                        * mInnerCircleRadiusRate, mPaint);
                //绘制箭头
                drawArrow(canvas);
                break;
        }
    }

    private void drawArrow(Canvas canvas) {
        if (mArrowDegree != -1) {
            mPaint.setStyle(Paint.Style.FILL);

            canvas.save();
            canvas.rotate(mArrowDegree, mCenterX, mCenterY);
            canvas.drawPath(mArrowPath, mPaint);

            canvas.restore();
        }
    }


}