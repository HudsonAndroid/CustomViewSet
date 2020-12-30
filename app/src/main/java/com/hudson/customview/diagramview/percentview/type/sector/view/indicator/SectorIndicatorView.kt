package com.hudson.customview.diagramview.percentview.type.sector.view.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.hudson.customview.R
import com.hudson.customview.diagramview.percentview.listener.OnSectorChangeListener
import com.hudson.customview.diagramview.percentview.type.sector.entity.indicator.CakeIndicator
import com.hudson.customview.diagramview.percentview.type.sector.entity.indicator.IIndicator
import com.hudson.customview.diagramview.percentview.type.sector.entity.percent.IPercentageEntity
import com.hudson.customview.diagramview.percentview.type.sector.percentinfo.PercentInfo
import com.hudson.customview.getDimension
import java.util.*

/**
 * 用于说明扇形图的指示器控件
 * Created by Hudson on 2020/12/29.
 */
open class SectorIndicatorView<T: PercentInfo> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle), OnSectorChangeListener {

    protected val mIndicatorEntityList: MutableList<IIndicator>
    private val mPaint: Paint

    open fun addPercentageEntity(entity: IPercentageEntity<T>) {
        mIndicatorEntityList.add(CakeIndicator(entity))
        assignRange()
    }

    fun cleanPercentageEntity() {
        mIndicatorEntityList.clear()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        assignRange()
    }

    /**
     * 为每个指示器分配区域
     */
    protected fun assignRange() {
        if (width > 0 && height > 0 && mIndicatorEntityList.size > 0) {
            val height = height - paddingTop - paddingBottom
            val width = width - paddingLeft - paddingRight
            val cakeHeight =
                height / VERTICAL_COUNT.toFloat()
            val offset =
                if (mIndicatorEntityList.size % VERTICAL_COUNT == 0) 0 else 1
            val horizontalCount =
                mIndicatorEntityList.size / VERTICAL_COUNT + offset
            val cakeWidth = width / horizontalCount.toFloat()
            var entity: IIndicator
            var startX: Float
            var startY: Float
            for (i in mIndicatorEntityList.indices) {
                startY = i / horizontalCount * cakeHeight + paddingTop
                startX = i % horizontalCount * cakeWidth + paddingLeft
                entity = mIndicatorEntityList[i]
                entity.setRange(RectF(startX, startY, startX + cakeWidth, startY + cakeHeight))
            }
        }
    }

    override fun onChange(isCompleted: Boolean) {
        // we ignore the flag
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (indicatorEntity in mIndicatorEntityList) {
            indicatorEntity.drawIndicator(canvas, mPaint)
        }
    }

    companion object {
        private const val VERTICAL_COUNT = 5 //每列竖直方向上5个指示器
    }

    init {
        mIndicatorEntityList = ArrayList<IIndicator>()
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.style = Paint.Style.FILL
        mPaint.textSize = context.getDimension(R.dimen.text_size_normal)
    }
}

class SimpleIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
): SectorIndicatorView<PercentInfo>(context, attrs, defStyle)
