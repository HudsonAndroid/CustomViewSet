package com.hudson.customview.diagramview.percentview.view

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.hudson.customview.diagramview.percentview.AnimatorInstanceCreator
import com.hudson.customview.diagramview.percentview.listener.OnSectorChangeListener
import com.hudson.customview.diagramview.percentview.percententity.IAnimatedPercentEntity
import com.hudson.customview.diagramview.percentview.type.sector.percentinfo.PercentInfo
import com.hudson.customview.diagramview.percentview.type.sector.view.SectorDiagramView
import timber.log.Timber
import java.util.*
import kotlin.math.abs

/**
 * 可动态加载的百分比控件
 * 注意：由于控件共用一个Animator，因此如果外界
 * 需要同时动态变动多个控件，需要在变动时设置
 * combineOthers为true。如果不共用则不用考虑。
 * Created by Hudson on 2020/12/29.
 */
open class BasePercentView<P : PercentInfo, T : IAnimatedPercentEntity<P>> @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {
    val percentEntityList: MutableList<T>
    val animator: ValueAnimator
    private val mOnSectorChangeListeners: MutableList<OnSectorChangeListener>?
    private var mAnimateCancelable = true //动画是否允许取消

    open fun onAnimateUpdate(fraction: Float) {
        for (entity in percentEntityList) {
            val targetPercentage: Float = entity.getTargetPercentage()
            val resPercentage: Float = entity.getResPercentage()
            entity.setCurPercentage((targetPercentage - resPercentage) * fraction + resPercentage)
        }
        invalidate()
        notifyDataChange(abs(fraction - 1.0f) <= SectorDiagramView.FLOAT_COMPARE_OFFSET)
    }

    open fun addPercentEntity(entity: T) {
        percentEntityList.add(entity)
    }

    open fun cleanPercentEntity() {
        percentEntityList.clear()
        invalidate()
    }

    /**
     * 重置变换动画
     */
    private fun resetEntityAnimate() {
        for (entity in percentEntityList) {
            entity.onChangeFinished(false)
        }
    }

    /**
     * 动态变动元素占比
     * @param target target value array
     * @param combineOthers if true,you should call [.startSmoothChange]
     * after another view call this method with combine
     * others flag setting true.
     */
    fun smoothChangeTo(target: FloatArray, combineOthers: Boolean) {
        val size = commonDetect()
        if (size == -1) {
            return
        }
        if (target.size != size) {
            Timber.w("target array length doesn't match original array: input array length is ${target.size}, expected length is ${percentEntityList.size} so skip!")
            return
        }
        for (i in target.indices) {
            percentEntityList[i].setTargetPercentage(target[i])
        }
        if (!combineOthers) {
            animator.start()
        }
    }

    fun startSmoothChange() {
        if (isDataChanging) {
            Timber.w("last animator is running,so skip!")
            return
        }
        animator.start()
    }

    open fun commonDetect(): Int {
        if (isDataChanging) {
            Timber.w("last animator is running,so skip!")
            return -1
        }
        return percentEntityList.size
    }

    fun changeTo(target: FloatArray) {
        val size = commonDetect()
        if (size == -1) {
            return
        }
        if (target.size != size) {
            Timber.w("target array length doesn't match original array: input array length is ${target.size}, expected length is ${percentEntityList.size} so skip!")
            return
        }
        for (i in target.indices) {
            percentEntityList[i].setCurPercentage(target[i])
        }
        notifyDataChange(true)
        invalidate()
    }

    /**
     * 动态变动某个元素占比
     * @param index target element index
     * @param target the element target percentage
     * @param combineOthers if true,you should call [.startSmoothChange]
     * after another view call this method with combine
     * others flag setting true.
     */
    open fun smoothChangeTo(
        index: Int,
        target: Float,
        combineOthers: Boolean
    ) {
        val size = commonDetect()
        if (size == -1) {
            return
        }
        if (index < size - 1) {
            percentEntityList[index].setTargetPercentage(target)
        }
        if (!combineOthers) {
            animator.start()
        }
    }

    /**
     * 当前扇形数据是否在变动
     * @return
     */
    val isDataChanging: Boolean
        get() = animator.isRunning

    /**
     * 取消数据变动操作，如果没有变动操作，不做任何处理
     */
    fun cancelLastDataChange() {
        if (isDataChanging && mAnimateCancelable) {
            animator.cancel()
        }
    }

    fun addOnDataChangeListener(listener: OnSectorChangeListener) {
        if (!mOnSectorChangeListeners!!.contains(listener)) {
            mOnSectorChangeListeners.add(listener)
        }
    }

    fun removeDataChangeListener(listener: OnSectorChangeListener?) {
        mOnSectorChangeListeners!!.remove(listener)
    }

    fun notifyDataChange(isCompleted: Boolean) {
        if (mOnSectorChangeListeners != null) {
            for (listener in mOnSectorChangeListeners) {
                listener.onChange(isCompleted)
            }
        }
        if(isCompleted){
            // 这里相当于动画的直接完成，因此调用resetEntityAnimate，因为动画的变动需要一个初始值和一个终止值，
            // 调用该方法会重置初始值和终止值，避免影响下一次的动画
            resetEntityAnimate()
        }
    }

    fun setAnimateCancelable(cancelable: Boolean) {
        mAnimateCancelable = cancelable
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator.removeAllListeners()
        mOnSectorChangeListeners!!.clear()
    }

    companion object {
        const val DATA_CHANGE_ANIMATE_TIME: Long = 500
    }

    init {
        percentEntityList = ArrayList()
        animator = AnimatorInstanceCreator.createSingle()!! //共用Animator
        if (animator.isRunning) { //由于该动画是单一的，为了避免出现之前的动画影响，清除之前动作
            animator.end()
        }
        animator.duration = DATA_CHANGE_ANIMATE_TIME
        mOnSectorChangeListeners = ArrayList<OnSectorChangeListener>()
        animator.addUpdateListener { animation -> onAnimateUpdate(animation.animatedFraction) }
        animator.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                resetEntityAnimate()
            }

            override fun onAnimationCancel(animation: Animator) {
                resetEntityAnimate()
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }
}
