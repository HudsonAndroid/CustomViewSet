package com.hudson.customview.diagramview.percentview.type.sector.entity.indicator

import android.graphics.RectF
import com.hudson.customview.diagramview.percentview.type.sector.entity.percent.IPercentageEntity
import com.hudson.customview.diagramview.percentview.type.sector.percentinfo.PercentInfo

/**
 * Created by Hudson on 2020/12/29.
 */
abstract class AbsIndicator<T: PercentInfo>(
    percentageEntity: IPercentageEntity<T>,
    range: RectF? = null
) : IIndicator {
    protected var mPercentageEntity: IPercentageEntity<T> = percentageEntity
    protected var mRange: RectF? = range

    override fun setRange(range: RectF) {
        mRange = range
    }
}
