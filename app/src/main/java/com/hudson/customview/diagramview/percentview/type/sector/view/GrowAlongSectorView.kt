package com.hudson.customview.diagramview.percentview.type.sector.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.hudson.customview.diagramview.percentview.LoadAnimateProxy
import com.hudson.customview.diagramview.percentview.type.sector.entity.percent.SimpleEmptyPercentage
import com.hudson.customview.diagramview.percentview.type.sector.entity.percent.SimplePercentageEntity
import com.hudson.customview.diagramview.percentview.type.sector.percentinfo.PercentInfo
import com.hudson.customview.diagramview.percentview.view.IAnimateView

/**
 * 装载数据时动画显示（各个块同时由0增长到目标值）的扇形统计图
 * Created by Hudson on 2020/12/29.
 */
abstract class GrowAlongSectorView<T: PercentInfo> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    SectorDiagramView<T>(context, attrs, defStyle), LoadAnimateProxy.LoadListener, IAnimateView {
    private val mAnimateProxy: LoadAnimateProxy<T> by lazy {
        createProxy()
    } //代理者
    private var mEmptyColor = 0

    override fun dataReady() {
        mAnimateProxy.dataReady()
    }

    override fun onLoadStart() {
        mEmptyColor = mEmptyPercentage.getColor()
        mEmptyPercentage.setColor(Color.TRANSPARENT)
        percentEntityList.remove(mEmptyPercentage) //避免由于之前存在的绘制导致动画出现问题
    }

    override fun onLoadFinish() {
        mEmptyPercentage.setColor(mEmptyColor)
    }

    private fun createProxy(): LoadAnimateProxy<T> {
        val animateProxy = LoadAnimateProxy(this)
        animateProxy.setLoadListener(this)
        return animateProxy
    }
}

// 如果直接使用PercentInfo类型，直接使用本控件
class SimpleGrowAlongSectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
): GrowAlongSectorView<PercentInfo>(context, attrs, defStyle){
    override fun getEmptyPercentage() = SimpleEmptyPercentage()

    fun addPercentInfo(info: PercentInfo): SimpleGrowAlongSectorView {
        addPercentEntity(SimplePercentageEntity(info))
        return this
    }
}