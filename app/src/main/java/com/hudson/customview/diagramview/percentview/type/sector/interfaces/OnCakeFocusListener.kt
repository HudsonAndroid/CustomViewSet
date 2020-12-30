package com.hudson.customview.diagramview.percentview.type.sector.interfaces

import com.hudson.customview.diagramview.percentview.type.sector.entity.percent.IPercentageEntity
import com.hudson.customview.diagramview.percentview.type.sector.percentinfo.PercentInfo

/**
 * Created by Hudson on 2020/12/29.
 */
interface OnCakeFocusListener<T: PercentInfo> {
    fun onFocus(entity: IPercentageEntity<T>)
    fun onCancelFocus()
}