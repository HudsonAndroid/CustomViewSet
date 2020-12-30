package com.hudson.customview.diagramview.percentview.type.sector.entity.indicator

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

/**
 * Created by Hudson on 2020/12/29.
 */
interface IIndicator {

    companion object{
        const val RATE_COLOR_CAKE = 0.35f
        const val MAX_CAKE_WIDTH = 35f
        const val RATE_CAKE_TEXT_SPACE = 0.03f
        const val COLOR_TIPS = Color.BLACK
    }

    fun drawIndicator(
        canvas: Canvas,
        paint: Paint
    )

    fun setRange(range: RectF)
}