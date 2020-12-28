package com.hudson.customview.diagramview.pillar

import android.content.Context
import android.graphics.Color
import com.hudson.customview.dp2px
import com.hudson.customview.getDimension

/**
 * 配置信息
 * Created by Hudson on 2020/12/28.
 */
class Config {
    internal var itemPillarWith = 60F // 一个柱形占据的宽度，注意不是实际柱形绘制的宽度,dp
    internal var itemPillarDrawRatio = 0.4F // 柱形实际绘制占据柱形宽度的比例, float
    internal var intervalCount = 1 // 横坐标显示标题的间隔数
    internal var minDataValue = 0 // 数据最小值，如果设置了，数据中出现的更小值会被重置为该值
    internal var originalText = "0" // 原点描述
    internal var normalColor = Color.GRAY // 柱子默认颜色
    internal var focusColor = Color.RED // 柱子高亢颜色
    internal var enableTableLine = true // 是否绘制表格线
    internal var tableLineColor = Color.GRAY // 表格线颜色，前提是enableTableLine设置了true
    internal var tableLineWidth = 1F // 表格线的宽度,dp
    internal var textSize = 25F // 字体大小,px

    fun textSize(textSizeId: Int, context: Context): Config{
        this.textSize = context.getDimension(textSizeId)
        return this
    }

    fun itemPillarWidth(itemPillarWith: Float, context: Context): Config {
        this.itemPillarWith = dp2px(itemPillarWith, context)
        return this
    }

    fun itemPillarDrawRatio(itemPillarDrawRatio: Float): Config {
        this.itemPillarDrawRatio = itemPillarDrawRatio
        return this
    }

    fun intervalCount(intervalCount: Int): Config{
        this.intervalCount = intervalCount
        return this
    }

    fun minDataValue(minDataValue: Int): Config {
        this.minDataValue = minDataValue
        return this
    }

    fun originalText(originalText: String): Config {
        this.originalText = originalText
        return this
    }

    fun normalColor(normalColor: Int): Config {
        this.normalColor = normalColor
        return this
    }

    fun focusColor(focusColor: Int): Config {
        this.focusColor = focusColor
        return this
    }

    fun enableTableLine(enableTableLine: Boolean): Config {
        this.enableTableLine = enableTableLine
        return this
    }

    fun tableLineColor(tableLineColor: Int): Config {
        this.tableLineColor = tableLineColor
        return this
    }

    fun tableLineWidth(tableLineWidth: Float, context: Context): Config {
        this.tableLineWidth = dp2px(tableLineWidth, context)
        return this
    }
}