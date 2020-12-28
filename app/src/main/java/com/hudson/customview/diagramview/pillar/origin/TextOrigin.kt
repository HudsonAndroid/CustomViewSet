package com.hudson.customview.diagramview.pillar.origin

import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.NonNull
import com.hudson.customview.diagramview.interfaces.IOrigin
import com.hudson.customview.diagramview.pillar.Config

/**
 * 文本原点
 * Created by Hudson on 2020/12/23.
 */
class TextOrigin(config: Config) :
    IOrigin {
    private val mOriginText: String
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mTextBaseline: Float

    override fun drawMySelf(
        @NonNull canvas: Canvas,
        width: Float,
        height: Float
    ) {
        val textWidth = mPaint.measureText(mOriginText)
        canvas.drawText(mOriginText, width - textWidth, height / 2 + mTextBaseline, mPaint)
    }

    init {
        mPaint.textSize = config.textSize
        val fontMetrics = mPaint.fontMetrics
        mTextBaseline = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        mOriginText = config.originalText
    }
}