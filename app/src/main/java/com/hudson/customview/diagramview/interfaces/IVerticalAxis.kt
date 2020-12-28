package com.hudson.customview.diagramview.interfaces

/**
 * 竖直轴，或者y轴
 * Created by Hudson on 2020/12/23.
 */
interface IVerticalAxis : IElement {
    /**
     * 获取竖直轴的宽度
     * @return 宽度
     */
    fun getWidth(): Float
}