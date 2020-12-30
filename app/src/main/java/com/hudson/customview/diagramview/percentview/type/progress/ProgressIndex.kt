package com.hudson.customview.diagramview.percentview.type.progress

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import com.hudson.customview.diagramview.percentview.percententity.IAnimatedPercentEntity
import com.hudson.customview.diagramview.percentview.type.sector.percentinfo.PercentInfo

/**
 * 进度条方式的百分比实例
 * Created by Hudson on 2020/12/29.
 */
open class ProgressIndex<T : PercentInfo>(
    data: T,
    private val progressLineHeight: Float, // 一条进度条的高度
    private val bgColor:Int = Color.LTGRAY  // 进度条的背景色
) : IAnimatedPercentEntity<T> {
    private var mPercentage: Float
    private var mResPercentage: Float
    private var mTargetPercentage: Float
    private var mExtendInfo: T

    fun draw(
        canvas: Canvas,
        width: Int,
        height: Float,
        paint: Paint
    ) {
        paint.color = OUTER_LINE_COLOR
        val fontMetrics = paint.fontMetrics
        val baseline = height / 2 - (fontMetrics.bottom + fontMetrics.top) / 2
        val sideWidth = width * (1 - PROGRESS_WIDTH_RATIO) / 2
        canvas.drawText(mExtendInfo.name, (sideWidth - paint.measureText(mExtendInfo.name)) / 2, baseline, paint)
        val endText: String = "${(mPercentage * 100 + 0.5f).toInt()}%"
        val endX = width - sideWidth
        val top = (height - progressLineHeight) / 2
        val bottom = top + progressLineHeight
        paint.color = bgColor
        drawProgress(canvas, sideWidth, top, endX, bottom, paint)
        paint.color = mExtendInfo.color
        canvas.drawText(
            endText,
            endX + (sideWidth - paint.measureText(endText)) / 2,
            baseline,
            paint
        )
        drawProgress(
            canvas,
            sideWidth,
            top,
            sideWidth + (endX - sideWidth) * mPercentage / MAX_VALUE,
            bottom,
            paint
        )
    }

    private fun drawProgress(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        paint: Paint
    ) {
        var top = top
        var bottom = bottom
        if (Build.VERSION.SDK_INT >= 21) {
            var radius = (bottom - top) / 2
            val width = (right - left) / 2
            if (width < radius) {
                val offset = (radius - width) / 2
                top += offset
                bottom -= offset
                radius = width
            }
            canvas.drawRoundRect(left, top, right, bottom, radius, radius, paint)
        } else {
            canvas.drawRect(left, top, right, bottom, paint)
        }
    }

    override fun onChangeFinished(forceToTarget: Boolean) {
        if (forceToTarget) {
            setCurPercentage(mTargetPercentage)
        }
        mResPercentage = mPercentage
        mTargetPercentage = mPercentage
    }

    override fun getExtendInfo(): T {
        return mExtendInfo
    }

    override fun getPercentage(): Float {
        return mPercentage
    }

    override fun setCurPercentage(percentage: Float) {
        mPercentage =
            if (percentage > MAX_VALUE) MAX_VALUE else percentage
        mPercentage = if (mPercentage < 0) 0F else mPercentage
        mExtendInfo.percentage = mPercentage
    }

    override fun getResPercentage(): Float {
        return mResPercentage
    }

    override fun setResPercentage(resPercentage: Float) {
        mResPercentage = resPercentage
    }

    override fun getTargetPercentage(): Float {
        return mTargetPercentage
    }

    override fun setTargetPercentage(targetPercentage: Float) {
        mTargetPercentage = targetPercentage
    }

    companion object {
        const val OUTER_LINE_COLOR = Color.BLACK
        protected const val MAX_VALUE = 1f // 最大值
        protected const val PROGRESS_WIDTH_RATIO = 0.55f // 进度实际占据的宽度比例
    }

    init {
        mPercentage = data.percentage
        mExtendInfo = data
        mResPercentage = mPercentage
        mTargetPercentage = mResPercentage
    }
}

class SimplePercentIndex constructor(
    data: PercentInfo,
    progressLineHeight: Float
): ProgressIndex<PercentInfo>(data, progressLineHeight)
