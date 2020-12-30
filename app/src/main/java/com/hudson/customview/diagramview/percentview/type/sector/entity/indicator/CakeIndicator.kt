package com.hudson.customview.diagramview.percentview.type.sector.entity.indicator

import android.graphics.*
import com.hudson.customview.diagramview.percentview.type.sector.entity.indicator.IIndicator.Companion.COLOR_TIPS
import com.hudson.customview.diagramview.percentview.type.sector.entity.indicator.IIndicator.Companion.MAX_CAKE_WIDTH
import com.hudson.customview.diagramview.percentview.type.sector.entity.indicator.IIndicator.Companion.RATE_CAKE_TEXT_SPACE
import com.hudson.customview.diagramview.percentview.type.sector.entity.indicator.IIndicator.Companion.RATE_COLOR_CAKE
import com.hudson.customview.diagramview.percentview.type.sector.entity.percent.BasePercentEntity
import com.hudson.customview.diagramview.percentview.type.sector.entity.percent.IPercentageEntity
import com.hudson.customview.diagramview.percentview.type.sector.entity.percent.LineShadeCreator
import com.hudson.customview.diagramview.percentview.type.sector.percentinfo.PercentInfo

/**
 * 普通的块状颜色指示器
 * Created by Hudson on 2020/12/29.
 */
open class CakeIndicator<T: PercentInfo>(percentageEntity: IPercentageEntity<T>) :
    AbsIndicator<T>(percentageEntity) {
    
    override fun drawIndicator(canvas: Canvas, paint: Paint) {
        if (mRange != null) {
            val extendInfo: PercentInfo = mPercentageEntity.getExtendInfo()
            val drawTip: String = extendInfo.name + ":" + (extendInfo.percentage * 100 + 0.5f).toInt() + "%"
            paint.color = extendInfo.color
            var saveId = -1
            if (mPercentageEntity is BasePercentEntity &&
                (mPercentageEntity as BasePercentEntity).isLineShadeShow()
            ) {
                val lineShadePath: Path = LineShadeCreator.lineShadePath!!
                if (lineShadePath != null) {
                    saveId = canvas.save()
                    canvas.clipPath(lineShadePath, Region.Op.DIFFERENCE)
                }
            }
            val drawX = drawTypeCake(mRange!!,canvas, paint)
            if (saveId != -1) {
                canvas.restore()
            }
            drawTips(mRange!!, canvas, paint, drawTip, drawX + mRange!!.width() * RATE_CAKE_TEXT_SPACE)
        }
    }

    open fun drawTypeCake(
        range: RectF,
        canvas: Canvas,
        paint: Paint
    ): Float {
        val cakeHeight: Float = range.height() * RATE_COLOR_CAKE
        val top: Float = (range.height() - cakeHeight) / 2 + range.top
        var cakeWidth: Float = range.width() * RATE_COLOR_CAKE
        cakeWidth = if (cakeWidth > MAX_CAKE_WIDTH) MAX_CAKE_WIDTH else cakeWidth
        val right: Float = range.left + cakeWidth
        canvas.drawRect(range.left, top, right, top + cakeHeight, paint)
        return right
    }

    private fun drawTips(
        range: RectF,
        canvas: Canvas,
        paint: Paint,
        str: String,
        drawX: Float
    ) {
        paint.color = COLOR_TIPS
        val fontMetrics = paint.fontMetrics
        val baseLine = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        canvas.drawText(str, drawX, range.height() / 2 + range.top + baseLine, paint)
    }
}