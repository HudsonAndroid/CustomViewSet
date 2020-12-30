package com.hudson.customview.diagramview.table.interfaces

/**
 * 水平轴，或者x轴
 * Created by Hudson on 2020/12/23.
 */
interface IHorizontalAxis : IElement {
    /**
     * 获取当前水平轴的高度，因为外界需要根据它的高度决定其他部分的排版
     */
    fun getHeight():Float

    fun setIntervalCount(count: Int)
}