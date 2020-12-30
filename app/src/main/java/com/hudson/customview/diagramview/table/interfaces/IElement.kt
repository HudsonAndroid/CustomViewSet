package com.hudson.customview.diagramview.table.interfaces

import android.graphics.Canvas

/**
 * 数学统计图元素统一抽象，所有数学统计图元素都具备自身绘制的能力
 * 并且由外界给定宽高（有点类似Android中的父view给子view相应宽高的关系）
 * Created by Hudson on 2020/12/23.
 */
interface IElement {
    fun drawMySelf(canvas: Canvas, width: Float, height: Float)
}