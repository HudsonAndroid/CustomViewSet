package com.hudson.customview.diagramview.percentview.type.progress

import android.content.Context
import android.util.AttributeSet
import com.hudson.customview.diagramview.percentview.LoadAnimateProxy
import com.hudson.customview.diagramview.percentview.type.sector.percentinfo.PercentInfo
import com.hudson.customview.diagramview.percentview.view.IAnimateView

/**
 * Created by Hudson on 2020/12/29.
 */
open class AnimatedSetView<T: PercentInfo, P: ProgressIndex<T>> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ProgressSetView<T,P>(context, attrs, defStyle), IAnimateView {
    private val mAnimateProxy: LoadAnimateProxy<T> = LoadAnimateProxy(this) //代理者

    override fun dataReady() {
        mAnimateProxy.dataReady()
    }
}

class SimpleAnimatedSetView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
): AnimatedSetView<PercentInfo, SimplePercentIndex>(context, attrs, defStyle){

    fun addPercentInfo(info: PercentInfo, progressLineHeight:Float): SimpleAnimatedSetView {
        percentEntityList.add(SimplePercentIndex(info,progressLineHeight))
        return this
    }
}
