package com.hudson.customview.diagramview.percentview

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.os.Build
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.hudson.customview.diagramview.percentview.percententity.IAnimatedPercentEntity
import com.hudson.customview.diagramview.percentview.type.sector.percentinfo.PercentInfo
import com.hudson.customview.diagramview.percentview.view.BasePercentView
import com.hudson.customview.diagramview.percentview.view.IAnimateView

/**
 * 加载动画处理具体逻辑类
 * Created by Hudson on 2020/12/29.
 */
class LoadAnimateProxy<T: PercentInfo>(private val targetView: BasePercentView<T, out IAnimatedPercentEntity<T>>) :
    IAnimateView {
    private val mAnimator: ValueAnimator = targetView.animator
    private var mLoadListener: LoadListener? = null
    
    override fun dataReady() {
        val percentageEntityList: List<IAnimatedPercentEntity<T>> =
            targetView.percentEntityList
        for (entity in percentageEntityList) {
            entity.setResPercentage(0F)
            entity.setCurPercentage(0F)
        }
        startLoadAnimate()
    }

    private fun startLoadAnimate() {
        if (targetView.width != 0 && targetView.height != 0) {
            if (mLoadListener != null) {
                mLoadListener!!.onLoadStart()
            }
            mAnimator.duration = LOAD_ANIMATE_TIME
            mAnimator.start()
            targetView.setAnimateCancelable(false) //加载动画不允许取消
        } else {
            targetView.viewTreeObserver
                .addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        if(Build.VERSION.SDK_INT < 16){
                            targetView.viewTreeObserver.removeGlobalOnLayoutListener(this)
                        }else{
                            targetView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                        startLoadAnimate()
                    }
                })
        }
    }

    private fun onLoadFinish() {
        if (mLoadListener != null) {
            mLoadListener!!.onLoadFinish()
        }
        mAnimator.duration = BasePercentView.DATA_CHANGE_ANIMATE_TIME
        targetView.setAnimateCancelable(true)
    }

    interface LoadListener {
        fun onLoadStart()
        fun onLoadFinish()
    }

    fun setLoadListener(loadListener: LoadListener?) {
        mLoadListener = loadListener
    }

    companion object {
        const val LOAD_ANIMATE_TIME: Long = 1300
    }

    init {
        mAnimator.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                onLoadFinish()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }
}