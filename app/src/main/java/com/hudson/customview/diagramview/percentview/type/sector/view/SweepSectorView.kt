package com.hudson.customview.diagramview.percentview.type.sector.view

import android.content.Context
import android.util.AttributeSet
import com.hudson.customview.diagramview.percentview.type.sector.entity.percent.IPercentageEntity
import com.hudson.customview.diagramview.percentview.type.sector.entity.percent.SimpleEmptyPercentage
import com.hudson.customview.diagramview.percentview.type.sector.entity.percent.SimplePercentageEntity
import com.hudson.customview.diagramview.percentview.type.sector.percentinfo.PercentInfo
import kotlin.math.abs

/**
 * 装载数据时动画显示（各个块按照先后顺序依次从0增长到目标值）的扇形统计图
 * 备注：之前采用的是在加载动画执行开始前移除父类已经存在的监听器，但是实际
 * 发现，如果动画已经执行起来了，removeUpdateListener调用之后原有监听器仍
 * 然可以接收到变动的通知，目前原因不明。因此决定不移除原有监听器，而是修改
 * 原有监听器逻辑。
 * Created by Hudson on 2020/12/29.
 */
abstract class SweepSectorView<T: PercentInfo> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : GrowAlongSectorView<T>(context, attrs, defStyle) {

    private var mCompleteIndex: Int //当前已经完成加载动画的下标
    private var mCurrentIndex: Int
    private var mLoadAnimateRunning = false //是否是加载动画执行中

    override fun onAnimateUpdate(fraction: Float) {
        if (mLoadAnimateRunning) { //如果是加载动画执行中，则不走父类逻辑
            var curPercentage = 0f
            var entity: IPercentageEntity<T>
            for (i in 0 until percentEntityList.size) {
                entity = percentEntityList[i]
                val targetPercentage: Float = entity.getTargetPercentage()
                val resPercentage: Float = entity.getResPercentage()
                if (fraction >= curPercentage && fraction < curPercentage + targetPercentage) {
//                        float frac = (fraction - curPercentage) / targetPercentage; 转换后的fraction
                    entity.setCurPercentage(
                        (targetPercentage - resPercentage) *
                                (fraction - curPercentage) / targetPercentage + resPercentage
                    )
                    mCurrentIndex = i
                    break
                } else {
                    confirmEntityTargetPercent()
                    curPercentage += targetPercentage
                }
            }
            if (fraction >= 1.0f) {
                confirmLastEntityPercent()
            }
            invalidate()
            notifyDataChange(abs(fraction - 1.0f) <= FLOAT_COMPARE_OFFSET)
        } else {
            super.onAnimateUpdate(fraction)
        }
    }

    override fun onLoadStart() {
        super.onLoadStart()
        mLoadAnimateRunning = true
    }

    /**
     * 由于转换后的fraction可能并不会出现1.0（导致最终占比不是预期），
     * 为了保证结果正确，修改上次的动画块最终的占比为预期的占比
     */
    private fun confirmEntityTargetPercent() {
        var entity: IPercentageEntity<T>
        val size: Int = percentEntityList.size
        if (mCurrentIndex < size && mCurrentIndex >= 0 && mCompleteIndex < size && mCompleteIndex >= 0) {
            for (i in mCompleteIndex until mCurrentIndex) {
                entity = percentEntityList[i]
                entity.setCurPercentage(entity.getTargetPercentage())
            }
            mCompleteIndex = mCurrentIndex
        }
    }

    /**
     * 由于外界传入的值存在不确定性（总和小于100%），因此最后一个值不能依赖于
     * Animator的fraction为1.0f，为了避免出现问题故而在动画完成后再次确定.
     */
    private fun confirmLastEntityPercent() {
        var curIndex: Int = percentEntityList.size - 1
        if (percentEntityList.contains(mEmptyPercentage)) {
            curIndex--
        }
        if (mCurrentIndex == curIndex) {
            val entity: IPercentageEntity<T> = percentEntityList[mCurrentIndex]
            entity.setCurPercentage(entity.getTargetPercentage())
        }
    }

    override fun onLoadFinish() {
        super.onLoadFinish()
        mLoadAnimateRunning = false
    }

    init {
        mCompleteIndex = 0
        mCurrentIndex = 0
    }
}

// 如果直接使用PercentInfo类型，直接使用本控件
class SimpleSweepSectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
): SweepSectorView<PercentInfo>(context, attrs, defStyle){
    override fun getEmptyPercentage() = SimpleEmptyPercentage()

    fun addPercentInfo(info: PercentInfo): SimpleSweepSectorView {
        addPercentEntity(SimplePercentageEntity(info))
        return this
    }
}
