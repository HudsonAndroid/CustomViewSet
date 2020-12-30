package com.hudson.customview.diagramview.percentview.listener

/**
 * 扇形图动画变动监听器
 * Created by Hudson on 2020/12/29.
 */
interface OnSectorChangeListener {
    /**
     * 当扇形统计图数据变化时回调
     * @param isCompleted 本次变化是否已经完成，true表示已经完成。该参数
     * 在扇形图是通过动画变动或动画初始化的过程中时会是false，如果是直接
     * 变动占比，它将一直是true，因为这是立刻完成的
     */
    fun onChange(isCompleted: Boolean)
}