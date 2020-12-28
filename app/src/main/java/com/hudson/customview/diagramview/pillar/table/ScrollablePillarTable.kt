package com.hudson.customview.diagramview.pillar.table

import androidx.annotation.NonNull
import com.hudson.customview.diagramview.pillar.Config

/**
 * 可滚动的柱形图表（柱形的宽度需要外界指定，否则使用默认值）
 * Created by Hudson on 2020/12/23.
 */
class ScrollablePillarTable(
    @NonNull datas: List<Int>,
    maxValue: Int,
    verticalCount: Int,
    config: Config
) : CommonPillarTable(datas, maxValue, verticalCount, config) {

    override fun getTableItemWidth(tableWidth: Float, dataSize: Int) = config.itemPillarWith
}