package com.hudson.customview.diagramview.pillar.table

import androidx.annotation.NonNull
import com.hudson.customview.diagramview.pillar.Config

/**
 * 根据给定的数据自动调整柱形的宽度的柱形图表
 * 不适于数据较多的柱形图
 * Created by Hudson on 2020/12/23.
 */
class FixedPillarTable(
    @NonNull datas: List<Int>,
    maxValue: Int,
    verticalCount: Int,
    config: Config
) : CommonPillarTable(datas, maxValue, verticalCount, config) {
    override fun getTableItemWidth(tableWidth: Float, dataSize: Int) = tableWidth / dataSize
}
