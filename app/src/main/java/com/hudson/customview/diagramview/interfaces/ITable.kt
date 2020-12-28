package com.hudson.customview.diagramview.interfaces

/**
 * 表格部分
 * Created by Hudson on 2020/12/23.
 */
interface ITable : IElement{

    /**
     * 获取内容的宽度，注意不是绘制的范围宽度
     * @return 绘制的内容的宽度
     */
    fun getContentWidth(): Float

    /**
     * 获取绘制范围的宽度
     * @return 宽度
     */
    fun getRangeWidth(): Float
}