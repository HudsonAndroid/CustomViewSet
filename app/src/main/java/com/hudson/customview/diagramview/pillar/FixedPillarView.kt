package com.hudson.customview.diagramview.pillar

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.NonNull
import com.hudson.customview.diagramview.pillar.horizontalaxis.CommonHorizontalAxis
import com.hudson.customview.diagramview.pillar.horizontalaxis.FixedAxis
import com.hudson.customview.diagramview.pillar.table.CommonPillarTable
import com.hudson.customview.diagramview.pillar.table.FixedPillarTable
import com.hudson.customview.diagramview.pillar.verticalaxis.PillarVerticalAxis

/**
 * 自适应柱形图
 * 柱形图将会根据外部条件给的大小，
 * 自动调整柱形的宽度、大小。
 * 适用于数据较少情形
 * Created by Hudson on 2020/12/23.
 */
class FixedPillarView @JvmOverloads constructor(
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
        return FixedAxis(horizontalData, config)
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
        return FixedPillarTable(datas, maxValue, verticalData.size, config)
    }
}