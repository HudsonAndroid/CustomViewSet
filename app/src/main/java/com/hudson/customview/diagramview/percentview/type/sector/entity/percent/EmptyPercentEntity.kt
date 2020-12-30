package com.hudson.customview.diagramview.percentview.type.sector.entity.percent

import android.graphics.Color
import com.hudson.customview.diagramview.percentview.type.sector.percentinfo.PercentInfo

/**
 * 如果外界给定的各个块占比之和小于100%,那么使用
 * 空白占比填充
 * Created by Hudson on 2020/12/29.
 */
open class EmptyPercentage<T:PercentInfo>(data: T):
    BasePercentEntity<T>(data) {
    override fun getSweepPercentage(curPercentage: Float): Float {
        mPercentage = 1 - curPercentage
        return super.getSweepPercentage(curPercentage)
    }

    fun setColor(color: Int) {
        mColor = color
    }

    fun getColor(): Int {
        return mColor
    }

    override fun needDetectFocus(): Boolean {
        return true
    }
}

class SimpleEmptyPercentage:
    EmptyPercentage<PercentInfo>(data = PercentInfo(0F, Color.LTGRAY))