package com.hudson.customview.diagramview.pillar

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.NonNull
import com.hudson.customview.diagramview.pillar.horizontalaxis.CommonHorizontalAxis
import com.hudson.customview.diagramview.pillar.horizontalaxis.ScrollableAxis
import com.hudson.customview.diagramview.pillar.table.CommonPillarTable
import com.hudson.customview.diagramview.pillar.table.ScrollablePillarTable
import com.hudson.customview.diagramview.pillar.verticalaxis.PillarVerticalAxis

/**
 * 可以滚动的柱形图
 * 每个部分的宽度是固定的，默认值[.DEFAULT_TABLE_ITEM_WIDTH],
 * 外界可以自定修改该值。控件本身的表格部分可以滚动，以展示全部内容。
 * 适用于数据较多的柱形图
 * Created by Hudson on 2020/12/23.
 */
class ScrollablePillarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : CommonPillarView(context, attrs, defStyle) {
    @NonNull
    override fun getPillarHorizontalAxis(
        horizontalData: List<String>,
        verticalData: List<String>,
        datas: List<Int>,
        maxValue: Int,
        config: Config
    ): CommonHorizontalAxis {
        return ScrollableAxis(horizontalData, config)
    }

    @NonNull
    override fun getPillarVerticalAxis(
        horizontalData: List<String>,
        verticalData: List<String>,
        datas: List<Int>,
        maxValue: Int,
        config: Config
    ): PillarVerticalAxis {
        return PillarVerticalAxis(verticalData, config)
    }

    @NonNull
    override fun getPillarTable(
        verticalData: List<String>,
        datas: List<Int>,
        maxValue: Int,
        config: Config
    ): CommonPillarTable {
        return ScrollablePillarTable(datas, maxValue, verticalData.size, config)
    }
}