package com.hudson.customview.diagramview.percentview.type.sector.entity.percent

import android.graphics.Matrix
import android.graphics.Path

/**
 * 需求要同一个扇形图中的不同扇形块使用相同的颜色（这会导致无法区分块），
 * 然后衍生出要用斜线覆盖来指定的扇形块，以区分不同的扇形块。
 * （按理，使用不同的颜色区分就可以了，线条阴影区域只会增加系统本身负担）
 * 注意不同的控件如果大小不同，可能需要不同大小的参考Path,如果为了简单，
 * 可以创建一个屏幕大小的Path，但是由于过多的Rect可能没有必要。
 * Created by Hudson on 2020/12/29.
 */
object LineShadeCreator {
    /**
     * 获取线条阴影
     * Before you call this method,you should make sure method [.createLineShadeShape]
     * has been invoked,otherwise you will get a null object.
     * @return LineShadePath
     */
    var lineShadePath: Path? = null //本身只会被UI线程操作
    private const val SHADE_LINE_WIDTH = 1.5f //阴影线的宽度
    private const val SHADE_LINE_SPACE = 15f //阴影线之间的间距
    const val LINE_ROTATE_DEGREE = 30 //倾斜角度

    /**
     * 创建线条阴影
     * Don't call this method in [android.view.View.onDraw]
     * @param centerX 扇形圆心x
     * @param centerY 扇形圆心y
     * @param radius 参考半径
     */
    fun createLineShadeShape(
        centerX: Float,
        centerY: Float,
        radius: Float
    ) {
        lineShadePath = Path()
        var top = centerY - radius
        top = if (top < 0) 0F else top
        val bottom = centerY + radius
        var left = centerX - radius
        left = if (left < 0) 0F else left
        val right = centerX + radius
        while (true) {
            left += SHADE_LINE_SPACE
            if (left >= right) {
                break
            }
            lineShadePath!!.addRect(
                left,
                top,
                left + SHADE_LINE_WIDTH,
                bottom,
                Path.Direction.CW
            )
            left += SHADE_LINE_WIDTH
        }
        if (!lineShadePath!!.isEmpty) {
            val matrix = Matrix()
            matrix.setRotate(LINE_ROTATE_DEGREE.toFloat(), centerX, centerX)
            lineShadePath!!.transform(matrix)
        }
    }

}