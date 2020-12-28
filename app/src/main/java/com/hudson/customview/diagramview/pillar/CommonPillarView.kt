package com.hudson.customview.diagramview.pillar

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.hudson.customview.diagramview.AbsMathDiagramView
import com.hudson.customview.diagramview.interfaces.IHorizontalAxis
import com.hudson.customview.diagramview.interfaces.IOrigin
import com.hudson.customview.diagramview.interfaces.ITable
import com.hudson.customview.diagramview.interfaces.IVerticalAxis
import com.hudson.customview.diagramview.pillar.horizontalaxis.CommonHorizontalAxis
import com.hudson.customview.diagramview.pillar.origin.TextOrigin
import com.hudson.customview.diagramview.pillar.table.CommonPillarTable
import com.hudson.customview.diagramview.pillar.verticalaxis.PillarVerticalAxis

/**
 * Created by Hudson on 2020/12/23.
 */
abstract class CommonPillarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    AbsMathDiagramView(context, attrs, defStyle) {
    private var mPillarHorizontalAxis: CommonHorizontalAxis? = null
    private var mPillarVerticalAxis: PillarVerticalAxis? = null
    private var mPillarTable: CommonPillarTable? = null
    var config = Config()

    private fun setData(
        horizontalData: List<String>,
        verticalData: List<String>,
        datas: List<Int>,
        maxValue: Int
    ) {
        mPillarTable = getPillarTable(verticalData, datas, maxValue, config)
        mPillarVerticalAxis = getPillarVerticalAxis(horizontalData, verticalData, datas, maxValue, config)
        mPillarHorizontalAxis =
            getPillarHorizontalAxis(horizontalData, verticalData, datas, maxValue, config)
        attachData()
    }

    /**
     * @param horizontalData 水平轴的描述
     * @param verticalData 竖直轴的描述
     * @param datas 实际数据
     * @param maxValue 数据允许的最大值，表格依据此决定竖直方向上的比例调整；此外，如果数据中有
     * 超过该值的会被重置为该值
     * @param focusIndex 高亢的下标集合
     */
    fun setData(
        horizontalData: List<String>,
        verticalData: List<String>,
        datas: List<Int>,
        maxValue: Int,
        focusIndex: List<Int>?
    ) {
        setData(horizontalData, verticalData, datas, maxValue)
        mPillarTable!!.setFocusIndexSet(focusIndex)
    }

    @NonNull
    protected abstract fun getPillarHorizontalAxis(
        horizontalData: List<String>,
        verticalData: List<String>,
        datas: List<Int>,
        maxValue: Int,
        config: Config
    ): CommonHorizontalAxis

    @NonNull
    protected abstract fun getPillarVerticalAxis(
        horizontalData: List<String>,
        verticalData: List<String>,
        datas: List<Int>,
        maxValue: Int,
        config: Config
    ): PillarVerticalAxis

    @NonNull
    protected abstract fun getPillarTable(
        verticalData: List<String>,
        datas: List<Int>,
        maxValue: Int,
        config: Config
    ): CommonPillarTable

    override fun getHorizontalAxis(): IHorizontalAxis? {
        return mPillarHorizontalAxis
    }

    override fun getVerticalAxis(): IVerticalAxis? {
        return mPillarVerticalAxis
    }

    @NonNull
    override fun getTable(): ITable {
        return mPillarTable!!
    }

    @Nullable
    override fun getOrigin(): IOrigin {
        return TextOrigin(config)
    }
}