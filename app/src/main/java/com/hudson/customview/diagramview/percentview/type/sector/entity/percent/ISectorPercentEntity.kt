package com.hudson.customview.diagramview.percentview.type.sector.entity.percent

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Region
import com.hudson.customview.diagramview.percentview.percententity.IAnimatedPercentEntity
import com.hudson.customview.diagramview.percentview.type.sector.percentinfo.PercentInfo

/**
 * Created by Hudson on 2020/12/29.
 */
interface IPercentageEntity<T : PercentInfo> : IAnimatedPercentEntity<T> {
    /**
     * 绘制扇形区域
     * @param canvas 画布
     * @param curPercentage 当前已占有的百分比
     * @param centerX 圆心x
     * @param centerY 圆心y
     * @param radius 半径
     * @param paint 画笔
     */
    fun drawSector(
        canvas: Canvas,
        curPercentage: Float,
        centerX: Float,
        centerY: Float,
        radius: Float,
        paint: Paint
    )

    /**
     * 高亢显示当前扇形区域
     * @param canvas 画布
     * @param curPercentage 当前已占有的百分比
     * @param centerX 圆心x
     * @param centerY 圆心y
     * @param radius 半径
     * @param paint 画笔
     */
    fun onFocus(
        canvas: Canvas,
        curPercentage: Float,
        centerX: Float,
        centerY: Float,
        radius: Float,
        paint: Paint
    )

    /**
     * 获取当前扇形占比所在的区域
     * @return
     */
    fun getSectorRegion(): Region
}
