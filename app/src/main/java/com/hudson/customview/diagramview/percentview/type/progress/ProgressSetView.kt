package com.hudson.customview.diagramview.percentview.type.progress

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.hudson.customview.R
import com.hudson.customview.diagramview.percentview.type.sector.percentinfo.PercentInfo
import com.hudson.customview.diagramview.percentview.view.BasePercentView
import com.hudson.customview.getDimension

/**
 * Created by Hudson on 2020/12/29.
 */
open class ProgressSetView<T:PercentInfo, P: ProgressIndex<T>> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    BasePercentView<T, P>(context, attrs, defStyle) {
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas) {
        if (percentEntityList.size > 0) {
            val itemProgressHeight: Float = height * 1.0f / percentEntityList.size
            for (i in 0 until percentEntityList.size) {
                if (i > 0) {
                    canvas.translate(0f, itemProgressHeight) //translate是叠加的
                }
                percentEntityList[i].draw(canvas, width, itemProgressHeight, mPaint)
            }
        }
    }

    init {
        mPaint.color = Color.GRAY
        mPaint.textSize = context.getDimension(R.dimen.text_size_normal)
    }
}

class SimpleProgressSetView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
): ProgressSetView<PercentInfo, SimplePercentIndex>(context, attrs, defStyle){

    fun addPercentInfo(info: PercentInfo, progressLineHeight:Float): SimpleProgressSetView {
        percentEntityList.add(SimplePercentIndex(info,progressLineHeight))
        return this
    }
}