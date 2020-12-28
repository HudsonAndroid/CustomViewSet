package com.hudson.customview.diagramview.pillar.verticalaxis

import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.NonNull
import com.hudson.customview.diagramview.interfaces.IVerticalAxis
import com.hudson.customview.diagramview.pillar.Config
import java.util.*

/**
 * 柱形图竖直轴
 * Created by Hudson on 2019/2/20.
 */
class PillarVerticalAxis(
    datas: List<String>,
    config: Config
) : IVerticalAxis {
    private val mStrWidthSet: MutableMap<String, Float> =
        LinkedHashMap()
    private val mTextBaseline: Float
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mWidth = 0f

    private fun measureTextWidth(datas: List<String>) {
        var width: Float
        for (data in datas) {
            width = mPaint.measureText(data)
            if (width > this.mWidth) {
                this.mWidth = width
            }
            mStrWidthSet[data] = width
        }
    }

    override fun getWidth() = mWidth

    override fun drawMySelf(
        @NonNull canvas: Canvas,
        width: Float,
        height: Float
    ) {
        val averageHeight = height / mStrWidthSet.size
        var itemWidth: Float
        val startY = averageHeight / 2 + mTextBaseline
        for ((index, s) in mStrWidthSet.keys.withIndex()) {
            itemWidth = mStrWidthSet[s]!!
            canvas.drawText(s, (width - itemWidth) / 2, startY + averageHeight * index, mPaint)
        }
    }

    init {
        mPaint.textSize = config.textSize
        val fontMetrics = mPaint.fontMetrics
        mTextBaseline = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        measureTextWidth(datas)
    }
}