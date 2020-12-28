package com.hudson.customview.diagramview

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.annotation.Keep
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.hudson.customview.diagramview.interfaces.IHorizontalAxis
import com.hudson.customview.diagramview.interfaces.IOrigin
import com.hudson.customview.diagramview.interfaces.ITable
import com.hudson.customview.diagramview.interfaces.IVerticalAxis
import com.hudson.customview.dp2px
import timber.log.Timber
import kotlin.math.abs

/**
 * 数学统计图控件抽象
 * 这里的数学统计图指的是柱形图、折线图这样的数学统计图。
 *
 * 竖直轴 + 水平轴 + 实际表格内容（可能是折线、柱形等）+ 原点位置说明
 * 其中，竖直轴、原点位置、水平轴可以不存在，表格内容必须存在。
 * 如果表格实际内容超过了给定的范围大小，那么将会支持滚动。
 * Created by Hudson on 2020/12/23.
 */
abstract class AbsMathDiagramView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr){
    companion object{
        const val MAX_FLING_SPEED = 8000  // 惯性滑动参考的最大滑动速度
        const val MAX_INERTIA_DURATION = 3000 // 惯性滑动最大时间，ms
        const val INERTIA_LIMIT_VELOCITY:Float = 800F//惯性滑动速度阈值，只有用户滑动的速度大于该速度释放了才触发惯性滑动
        const val EXTEND_VERTICAL_TABLE_SPACE = 6F // 竖直轴(y轴)与表格部分之间的（水平方向）空隙,dp
    }

    private var mHorizontalAxis: IHorizontalAxis? = null
    private var mVerticalAxis: IVerticalAxis? = null
    private var mTable: ITable? = null
    private var mOrigin: IOrigin? = null
    private var mHorizontalTableSpace = 0f
    private var mDetector: GestureDetector? = null
    // 惯性滑动的Interpolator插值器
    private var mOutSlowInInterpolator: LinearOutSlowInInterpolator? = null
    // 惯性滑动的属性动画
    private var mInertiaAnimator: ObjectAnimator? = null

    init {
        mHorizontalTableSpace = dp2px(EXTEND_VERTICAL_TABLE_SPACE, context)
        mOutSlowInInterpolator = LinearOutSlowInInterpolator()
        mDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (mHorizontalAxis == null || Math.abs(velocityX) < INERTIA_LIMIT_VELOCITY) {
                    return false
                }
                // 快速滑动达到阈值，因此需要惯性滑动
                val scrollTarget: Float = if (velocityX > 0) {
                    mScrollX + calculateTarget(velocityX)
                } else {
                    mScrollX - calculateTarget(velocityX)
                }
                startInertiaScroll(scrollTarget, calculateDuration(velocityX))
                return true
            }
        })
    }


    /**
     * 回调该方法将会对控件的y轴,x轴,表格部分进行初始化
     * 子类设置数据之后必须回调该方法才能刷新页面
     */
    protected open fun attachData() {
        mHorizontalAxis = getHorizontalAxis()
        mVerticalAxis = getVerticalAxis()
        mTable = getTable()
        mOrigin = getOrigin()
        invalidate()
    }

    /**
     * 开始惯性滑动
     */
    private fun startInertiaScroll(target: Float, duration: Int) {
        cancelInertia()
        mInertiaAnimator = ObjectAnimator.ofFloat(this, "scrollX", mScrollX, target)
        mInertiaAnimator?.run {
            interpolator = mOutSlowInInterpolator
            this.duration = duration.toLong()
            start()
        }
    }

    /**
     * 取消当前惯性滑动(如果当前存在)
     */
    private fun cancelInertia() {
        mInertiaAnimator?.cancel()
        mInertiaAnimator = null
    }

    @Keep //属性动画执行的方法，不要删除， 添加该注解是为了避免编译器优化或者混淆时被丢弃
    fun setScrollX(scrollX: Float) {
        mScrollX = scrollX
        modifyScrollX()
    }

    /**
     * 根据当前的速度计算出惯性滑动需要滑动的距离
     * @param velocityX 速度
     * @return 距离
     */
    private fun calculateTarget(velocityX: Float): Float {
        return mTable!!.getContentWidth() * mOutSlowInInterpolator!!.getInterpolation(
            abs(
                velocityX / MAX_FLING_SPEED
            )
        )
    }

    /**
     * 根据当前的速度计算出惯性滑动需要滑动的时间
     * @param velocityX 速度
     * @return 时间
     */
    private fun calculateDuration(velocityX: Float): Int {
        return (MAX_INERTIA_DURATION * mOutSlowInInterpolator!!.getInterpolation(
            abs(velocityX / MAX_FLING_SPEED)
        )).toInt()
    }

    private fun getHorizontalAxisHeight() = mHorizontalAxis?.getHeight() ?: 0f

    private fun getVerticalAxisWidth() = mVerticalAxis?.getWidth() ?: 0f

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            // 滑动冲突处理，在用户点击到本控件的时候，请求父层级禁止拦截（子view触发父view禁止拦截方式，
            // 由于我们不知道父view会是谁，因此只能以这种方式）
            MotionEvent.ACTION_DOWN -> parent.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.x - mInterceptX
                val deltaY = event.y - mInterceptY
                if (abs(deltaX) < abs(deltaY)) {
                    //交给父view处理
                    parent.requestDisallowInterceptTouchEvent(false)
                } else {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            else -> { }
        }
        mInterceptX = event.x
        mInterceptY = event.y
        return super.dispatchTouchEvent(event)
    }

    // 用于判断是否禁止父view拦截
    private var mInterceptY = 0f
    private var mInterceptX = 0f

    private var mScrollX = 0f //当前画布滑动的offset值
    private var mOldScrollX = 0f
    private var mStartX = 0f //触摸起始x坐标
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 如果不允许滑动，直接结束
        if(!canScroll()) return false;
        mDetector!!.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                cancelInertia() //如果用户触摸了，那么终止惯性滑动
                mStartX = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                cancelInertia()
                mScrollX += event.x - mStartX
                mStartX = event.x
            }
            else -> { }
        }
        modifyScrollX()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        if (mTable == null) {
            return
        }
        val height =
            height - paddingTop - paddingBottom - getHorizontalAxisHeight()
        val dy = getHeight() - paddingBottom - getHorizontalAxisHeight()
        val dx = paddingLeft + getVerticalAxisWidth() + mHorizontalTableSpace
        val width = width - dx - paddingRight
        //绘制竖直轴
        mVerticalAxis?.run {
            canvas.save()
            canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())
            drawMySelf(canvas, getVerticalAxisWidth(), height)
            canvas.restore() //恢复到最初状态
        }
        //绘制原点内容
        mOrigin?.run {
            canvas.save()
            canvas.translate(paddingLeft.toFloat(), dy)
            drawMySelf(canvas, getVerticalAxisWidth(), getHorizontalAxisHeight())
            canvas.restore()
        }
        //构建新的图层，后续所有内容都绘制在新图层上面(注意，图层和画布是两个概念，虽然新建了图层，但画布还是原来的画布)
        // 构建新图层的主要原因是表格部分可能是可以滚动的，因此我们需要在本view内部实现滚动(也包含了惯性滚动)
        val layerId = if(Build.VERSION.SDK_INT >= 26){
            canvas.saveLayerAlpha(
                dx,
                0f,
                getWidth().toFloat(),
                getHeight().toFloat(),
                255
            )
        }else{
            // API 26 废弃了
            canvas.saveLayerAlpha(
                dx,
                0f,
                getWidth().toFloat(),
                getHeight().toFloat(),
                255,
                Canvas.ALL_SAVE_FLAG
            )
        }
        //绘制水平轴， 滑动的逻辑由mScrollX控制 【表格不支持竖直方向上滑动】
        mHorizontalAxis?.run {
            canvas.save()
            canvas.translate(dx + mScrollX, dy)
            drawMySelf(canvas, width, getHorizontalAxisHeight())
            canvas.restore() //恢复到新建图层时
        }
        //绘制数据区域
        canvas.translate(dx + mScrollX, paddingTop.toFloat())
        mTable!!.drawMySelf(canvas, width, height)
        canvas.restoreToCount(layerId) //恢复到新建图层之前
    }

    /**
     * 修改滑动值，避免滑出边界
     */
    private fun modifyScrollX() {
        mScrollX = if (mScrollX > 0) 0F else mScrollX
        // 如果实际内容的宽度小于允许的宽度，那么不用修改；否则就需要修改
        val offset = mTable!!.getContentWidth() - mTable!!.getRangeWidth()
        mScrollX = if(offset > 0){
            val minScroll = -offset
            if (mScrollX < minScroll) minScroll else mScrollX
        }else{
            0F // 不允许滑动
        }
        if(abs(mOldScrollX - mScrollX) > 0.01f){// float或者double这样的类型比较相等不能使用==
            invalidate()
        }
        mOldScrollX = mScrollX
    }

    private fun canScroll() = mTable!!.getContentWidth() - mTable!!.getRangeWidth() > 0

    //下面内容需要子View自行处理
    @Nullable
    abstract fun getHorizontalAxis(): IHorizontalAxis?

    @Nullable
    abstract fun getVerticalAxis(): IVerticalAxis?

    @NonNull
    abstract fun getTable(): ITable

    @Nullable
    abstract fun getOrigin(): IOrigin?
}