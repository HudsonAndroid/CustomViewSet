package com.hudson.customview.gesturelock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.hudson.customview.R;

import java.util.ArrayList;

/**
 * Created by Hudson on 2017/3/10.
 * 本控件是自定义ViewGroup的第三个
 * 本控件是一个手势锁的实现
 * <p>
 * 总结：1.执行顺序：onMeasure --> onSizeChanged --> onLayout
 *      2.控件制作过程：先定位，后处理onTouch，随后绘制，最后处理答案
 */

public class GestureLock extends RelativeLayout {
    private static final int DEFAULT_SIDE_COUNT = 4;
    private static final int DEFAULT_TRY_COUNT = 5;
    private static final int DEFAULT_NONE_INNER_COLOR = 0xFF939090;
    private static final int DEFAULT_NONE_OUTER_COLOR = 0xFFE0DBDB;
    private static final int DEFAULT_DOING_COLOR = 0xFF378FC9;
    private static final int DEFAULT_DONE_COLOR = 0xFFFF0000;
    private int mSideCount;//每条边上所包含的密码圈的个数
    private int mGestureViewWidth;//密码圈的宽高
    private int mGestureSpace = 20;//密码圈之间的间隙大小
    private int mViewGroupRealWidth;//控件内容实际占用
    private LockCircle[] mLockCircles;//密码圈数组
    private ArrayList<Integer> mChoose;//用户选择的密码
    /**
     * 用户正在绘制时，末端的线条是可以移动的，我们需要以用户当前的手指位置作为线条的临时终点
     */
    private Point mCurPosition;

    private Paint mPaint;
    private Path mPath;
    private int mNoneInnerColor;
    private int mNoneOuterColor;
    private int mDoingColor;
    private int mDoneColor;

    //===================答案相关==========
    private int[] mAnswer= new int[]{1,2,3};
    private int mTryTimes;

    /**
     * 设置可以尝试的次数
     * @param tryTimes 次数
     */
    public void setTryTimes(int tryTimes) {
        mTryTimes = tryTimes;
    }

    /**
     * 设置答案
     * 注意：答案是从1开始的数字
     * @param answer 答案
     */
    public void setAnswer(int[] answer) {
        mAnswer = answer;
    }

    public GestureLock(Context context) {
        this(context, null);
    }

    public GestureLock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureLock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPath = new Path();
        mChoose = new ArrayList<>();
        mCurPosition = new Point();
        if(attrs!=null){//避免由于第一个构造方法造成异常
            TypedArray ta = context.obtainStyledAttributes(attrs,
                    R.styleable.GestureLock);
            mSideCount = ta.getInteger(R.styleable.GestureLock_sideCount,DEFAULT_SIDE_COUNT);
            mTryTimes = ta.getInteger(R.styleable.GestureLock_tryTimes,DEFAULT_TRY_COUNT);
            mNoneInnerColor = ta.getColor(R.styleable.GestureLock_noneInnerColor,DEFAULT_NONE_INNER_COLOR);
            mNoneOuterColor = ta.getColor(R.styleable.GestureLock_noneOuterColor,DEFAULT_NONE_OUTER_COLOR);
            mDoingColor = ta.getColor(R.styleable.GestureLock_selectingColor,DEFAULT_DOING_COLOR);
            mDoneColor = ta.getColor(R.styleable.GestureLock_selectedColor,DEFAULT_DONE_COLOR);
            ta.recycle();
        }else {
            mSideCount = DEFAULT_SIDE_COUNT;
            mTryTimes = DEFAULT_TRY_COUNT;
            mNoneInnerColor = DEFAULT_NONE_INNER_COLOR;
            mNoneOuterColor = DEFAULT_NONE_OUTER_COLOR;
            mDoingColor = DEFAULT_DOING_COLOR;
            mDoneColor = DEFAULT_DONE_COLOR;
        }
    }

    //当我们的控件大小确定了之后，我们再给子View设置layoutParams，然后使用RelativeLayout的onLayout方法完成布局
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mViewGroupRealWidth = Math.min(w, h);
        //密码圈宽高的计算方法： （控件总长度 - （间隙占用））/ 每一行（列）含有的个数
        // 间隙包括最外侧的两个 + （行个数-1） = 行个数 + 1
        mGestureViewWidth = (mViewGroupRealWidth - mGestureSpace * (mSideCount + 1)) / mSideCount;
        // 设置画笔的宽度为GestureLockView的内圆直径稍微小点（不喜欢的话，随便设）
        mPaint.setStrokeWidth(mGestureViewWidth * 0.29f);
        if (mLockCircles == null) {
            mLockCircles = new LockCircle[mSideCount * mSideCount];//n*n
            for (int i = 0; i < mLockCircles.length; i++) {
                mLockCircles[i] = new LockCircle(getContext(), mNoneInnerColor, mNoneOuterColor
                        , mDoingColor, mDoneColor);
                mLockCircles[i].setId(i + 1);//设置控件的id，android:id
                //设置参数，主要是定位GestureLockView间的位置
                RelativeLayout.LayoutParams lockerParams = new RelativeLayout.LayoutParams(
                        mGestureViewWidth, mGestureViewWidth);
                //=====================布局===============
                if (i % mSideCount != 0) {//如果不是第一列的，那么把该密码圈设置为上一个的右边
                    lockerParams.addRule(RelativeLayout.RIGHT_OF, mLockCircles[i - 1].getId());
                }
                // 从第二行开始，设置为上一行同一位置View的下面
                if (i > mSideCount - 1) {
                    lockerParams.addRule(RelativeLayout.BELOW, mLockCircles[i - mSideCount].getId());
                }
                //设置右下左上的边距
                int rightMargin = mGestureSpace;
                int bottomMargin = mGestureSpace;
                int leftMagin = 0;
                int topMargin = 0;
                /**
                 * 每个View都有右外边距和底外边距 第一行的有上外边距 第一列的有左外边距
                 */
                if (i >= 0 && i < mSideCount) {// 第一行
                    topMargin = mGestureSpace;
                }
                if (i % mSideCount == 0) {// 第一列
                    leftMagin = mGestureSpace;
                }
                lockerParams.setMargins(leftMagin, topMargin, rightMargin, bottomMargin);
                mLockCircles[i].setSelectStatus(LockCircle.SelectStatus.STATUS_NONE);
                addView(mLockCircles[i], lockerParams);
            }
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }


    private int mLastX, mLastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                reset();
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                mPaint.setColor(mDoingColor);
                mPaint.setAlpha(50);
                //找到落地处的密码圈
                int x = (int) event.getX();
                int y = (int) event.getY();
                LockCircle child = getChildIdByPos(x, y);
                if (child != null) {
                    int cId = child.getId();
                    if (!mChoose.contains(cId)) {
                        mChoose.add(cId);
                        child.setSelectStatus(LockCircle.SelectStatus.STATUS_SELECTING);
                        //绘制线条
                        mLastX = (child.getLeft() + child.getRight()) / 2;
                        mLastY = (child.getTop() + child.getBottom()) / 2;
                        if (mChoose.size() == 1) {//如果当前是第一个，那么使用moveTo
                            mPath.moveTo(mLastX, mLastY);
                        } else {
                            mPath.lineTo(mLastX, mLastY);
                        }
                    }
                }
                mCurPosition.x = x;
                mCurPosition.y = y;
                break;
            case MotionEvent.ACTION_UP://一旦抬起，我们就要去验证结果
                if(mChoose.size()<2){//如果选择的个数小于2，那么认为非法密码
                    reset();
                    break;
                }
                //抬起后，我们的密码线就固定了，所以不再需要手指的指引了
                mCurPosition.x = mLastX;
                mCurPosition.y = mLastY;
                mTryTimes--;
                if(mTryTimes ==0){
                    System.out.println("次数用完");
                    if(mOnGestureLockListener!=null)
                        mOnGestureLockListener.onTimesOver();
                }
                boolean isCorrect = checkAnswer();
                if(isCorrect){
                    mDoneColor = Color.GREEN;
                    System.out.println("密码正确");
                    if(mOnGestureLockListener!=null){
                        mOnGestureLockListener.onGesturePasswdCorrect();
                    }
                }else{
                    System.out.println("密码错误");
                    if(mOnGestureLockListener!=null){
                        mOnGestureLockListener.onGesturePasswdWrong(mTryTimes);
                    }
                }
                mPaint.setColor(mDoneColor);
                mPaint.setAlpha(50);
                //修改选中密码圈的状态
                changeSelectedStatusAndShowArrow(isCorrect);
                break;
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    //==============验证答案========
    public boolean checkAnswer(){
        if(mAnswer ==null){
            try {
                throw new Exception("the password is null!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(mAnswer.length!=mChoose.size()){
            return false;
        }else{
            for (int i = 0; i < mAnswer.length; i++) {
                if(mAnswer[i] != mChoose.get(i)){
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mPath != null) {
            canvas.drawPath(mPath, mPaint);
        }
        if (mChoose.size() > 0) {
            if (mLastX != 0 && mLastY != 0)
                canvas.drawLine(mLastX, mLastY, mCurPosition.x,
                        mCurPosition.y, mPaint);
        }
    }

    /**
     * 修改已经被选中的密码圈的状态并显示箭头
     * 本方法在用户绘制完毕密码后使用
     */
    private void changeSelectedStatusAndShowArrow(boolean isCorrect) {
        //这里仅需要处理被选中的
        LockCircle startView = null,nextView;
        for (int i = 0; i < mChoose.size() - 1; i++) {
            if(startView == null)
                startView = (LockCircle) findViewById(mChoose.get(i));
            nextView = (LockCircle) findViewById(mChoose.get(i+1));
            //计算startView需要旋转的角度
            startView.setArrowDegree((int) Math.toDegrees(Math.atan2(nextView.getTop() - startView.getTop(), nextView.getLeft() - startView.getLeft())) + 90);
            //修改被选中的View的Status
//            startView.setSelectStatus(LockCircle.SelectStatus.STATUS_SELECTED);
            startView.selectComplete(mDoneColor);
            startView = nextView;
        }
        //最后一个也要改
//        startView.setSelectStatus(LockCircle.SelectStatus.STATUS_SELECTED);
        startView.selectComplete(mDoneColor);
    }

    //=======通过坐标获取对应位置是否有密码圈================
    /**
     * 检查当前坐标是否在child中
     *
     * @param child
     * @param x
     * @param y
     * @return
     */
    private boolean checkPositionInChild(View child, int x, int y) {
        if (x >= child.getLeft() && x <= child.getRight() && y >= child.getTop()
                && y <= child.getBottom()) {
            return true;
        }
        return false;
    }

    /**
     * 通过x,y获得落入的GestureLockView
     *
     * @param x
     * @param y
     * @return
     */
    private LockCircle getChildIdByPos(int x, int y) {
        for (LockCircle lockCircle : mLockCircles) {
            if (checkPositionInChild(lockCircle, x, y)) {
                return lockCircle;
            }
        }
        return null;
    }

    /**
     * 重置
     */
    public void reset(){
        mChoose.clear();
        mPath.reset();
        for(LockCircle lockCircle : mLockCircles){
            lockCircle.setSelectStatus(LockCircle.SelectStatus.STATUS_NONE);
            lockCircle.setArrowDegree(-1);
        }
    }

    private OnGestureLockListener mOnGestureLockListener;
    public void setOnGestureLockListener(OnGestureLockListener listener){
        mOnGestureLockListener = listener;
    }
    public interface OnGestureLockListener{
        void onGesturePasswdCorrect();

        /**
         * 当用户输入的密码错误时
         * @param times 剩余次数
         */
        void onGesturePasswdWrong(int times);

        /**
         * 当次数用完时
         */
        void onTimesOver();
    }
}
