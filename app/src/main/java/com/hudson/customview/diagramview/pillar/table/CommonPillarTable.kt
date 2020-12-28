package com.hudson.customview.diagramview.pillar.table

import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.NonNull
import com.hudson.customview.diagramview.interfaces.ITable
import com.hudson.customview.diagramview.pillar.Config
import java.util.*

/**
 * 柱形图表格
 * Created by Hudson on 2020/12/23.
 */
abstract class CommonPillarTable(
    @NonNull datas: List<Int>,
    maxValue: Int,
    verticalCount: Int,
    var config: Config
) : ITable {
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mDatas: MutableList<Int> = ArrayList()
    private val mMaxValue: Int
    private val mVerticalCount: Int
    private val mLineWidth: Float
    private val mFocusIndexSet: MutableList<Int> = ArrayList() //高亢显示的柱形下标
    private var mAverageWidth = 0f
    private var mRangeWidth = 0f //绘制范围的宽度
    private var mMinValue = 0 //最小值

    override fun drawMySelf(
        @NonNull canvas: Canvas,
        width: Float,
        height: Float
    ) {
        if (mDatas.size <= 0 || mMaxValue <= 0) {
            return
        }
        mRangeWidth = width
        mAverageWidth = getTableItemWidth(width, mDatas.size)
        if(config.enableTableLine){
            drawTableLine(canvas, mAverageWidth * mDatas.size, height)
        }
        mPaint.strokeWidth = mAverageWidth * config.itemPillarDrawRatio
        var x = mAverageWidth / 2
        var startY: Float
        var data: Int
        for (i in mDatas.indices) {
            if (mFocusIndexSet.contains(i)) {
                mPaint.color = config.focusColor
            } else {
                mPaint.color = config.normalColor
            }
            data = mDatas[i]
            if (data <= mMinValue) {
                data = mMinValue
            }
            startY = height - height * data / mMaxValue
            canvas.drawLine(x, startY, x, height, mPaint)
            x += mAverageWidth
        }
    }

    override fun getContentWidth(): Float {
        return mAverageWidth * mDatas.size
    }

    override fun getRangeWidth(): Float {
        return mRangeWidth
    }

    /**
     * 绘制表格线
     * @param canvas
     * @param totalWidth
     * @param height
     */
    private fun drawTableLine(
        canvas: Canvas,
        totalWidth: Float,
        height: Float
    ) {
        mPaint.strokeWidth = mLineWidth
        mPaint.color = config.tableLineColor
        val halfWidth = mLineWidth / 2
        canvas.drawLine(halfWidth, 0f, halfWidth, height, mPaint)
        val startX = totalWidth - halfWidth
        canvas.drawLine(startX, 0f, startX, height, mPaint)
        val averageHeight = height / mVerticalCount
        var startY: Float
        for (i in 0..mVerticalCount) {
            startY = halfWidth + averageHeight * i
            canvas.drawLine(halfWidth, startY, startX, startY, mPaint)
        }
    }

    /**
     * 获取表格一项的宽度
     * @param tableWidth 表格总宽度
     * @param dataSize 数据大小
     * @return 宽度
     */
    protected abstract fun getTableItemWidth(tableWidth: Float, dataSize: Int): Float

    /**
     * 设置高亢显示的柱形的下标集合
     * @param focusIndexSet
     */
    fun setFocusIndexSet(focusIndexSet: List<Int>?) {
        focusIndexSet?.run {
            mFocusIndexSet.clear()
            mFocusIndexSet.addAll(this)
        }
    }

    init {
        mDatas.clear()
        mDatas.addAll(datas)
        mMaxValue = maxValue
        mVerticalCount = verticalCount
        mLineWidth = config.tableLineWidth
        mMinValue = config.minDataValue
    }
}