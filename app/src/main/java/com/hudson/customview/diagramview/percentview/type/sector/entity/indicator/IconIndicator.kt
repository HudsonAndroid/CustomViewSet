package com.hudson.customview.diagramview.percentview.type.sector.entity.indicator

import android.graphics.*
import com.hudson.customview.diagramview.percentview.type.sector.entity.percent.IPercentageEntity
import com.hudson.customview.diagramview.percentview.type.sector.percentinfo.PercentInfo

/**
 * 图标指示器
 * Created by Hudson on 2020/12/29.
 */
class IconIndicator<T: PercentInfo>(
    private val icon:Bitmap,
    percentageEntity: IPercentageEntity<T>) : CakeIndicator<T>(percentageEntity) {
    
    override fun drawTypeCake(
        range: RectF,
        canvas: Canvas,
        paint: Paint
    ): Float {
        val top: Float = (range.height() - icon.height) / 2 + range.top
        canvas.drawBitmap(icon, range.left, top, paint)
        return range.left + icon.width
    }
}