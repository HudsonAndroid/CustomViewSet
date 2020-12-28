package com.hudson.customview.diagramview.pillar.horizontalaxis

import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.NonNull
import com.hudson.customview.diagramview.interfaces.IHorizontalAxis
import com.hudson.customview.diagramview.pillar.Config
import java.util.*

/**
 * Created by Hudson on 2020/12/23.
 */
abstract class CommonHorizontalAxis(
    datas: List<String>,
    var config: Config
) : IHorizontalAxis {
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mIntervalCount: Int
    private val mTextBaseline: Float
    private val mTextHeight: Float
    private val mDatas: MutableList<String> = ArrayList()

    override fun setIntervalCount(count: Int) {
        mIntervalCount = count
    }

    override fun getHeight(): Float {
        return mTextHeight
    }

    override fun drawMySelf(
        @NonNull canvas: Canvas,
        width: Float,
        height: Float
    ) {
        val averageWidth = getItemWidth(width, mDatas.size)
        var textWidth: Float
        var text: String
        var i = 0
        while (i < mDatas.size) {
            text = mDatas[i]
            textWidth = mPaint.measureText(text)
            canvas.drawText(
                text,
                averageWidth * i + averageWidth / 2 - textWidth / 2,
                mTextHeight / 2 + mTextBaseline,
                mPaint
            )
            i += mIntervalCount + 1
        }
    }

    protected abstract fun getItemWidth(totalWidth: Float, dataSize: Int): Float

    init {
        mPaint.textSize = config.textSize
        val fontMetrics = mPaint.fontMetrics
        mTextBaseline = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        mTextHeight = fontMetrics.descent - fontMetrics.ascent
        mDatas.clear()
        mDatas.addAll(datas)
        mIntervalCount = config.intervalCount
    }
}