package com.hudson.customview.diagramview.percentview.view

/**
 * 加载数据时可动画变化的控件
 * Created by Hudson on 2020/12/29.
 */
interface IAnimateView {
    /**
     * 外界回调该方法即表示数据已经设置完毕了，
     * 通知控件可以动态刷新显示数据。外界必须
     * 保证数据已设置完毕，否则没有预期效果。
     */
    fun dataReady()
}