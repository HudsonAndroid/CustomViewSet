package com.hudson.customview.diagramview.percentview

import android.animation.ValueAnimator

/**
 * Created by Hudson on 2020/12/29.
 */
object AnimatorInstanceCreator {
    @Volatile
    private var sAnimator: ValueAnimator? = null

    /**
     * 重新创建一个属性动画
     * @return
     */
    fun create(): ValueAnimator {
        return ValueAnimator.ofFloat(0f, 1.0f)
    }

    /**
     * 所有控件共用一个属性动画
     * @return
     */
    fun createSingle(): ValueAnimator? {
        if (sAnimator == null) {
            synchronized(AnimatorInstanceCreator::class.java) {
                if (sAnimator == null) {
                    sAnimator = create()
                }
            }
        }
        return sAnimator
    }
}
