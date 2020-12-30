package com.hudson.customview.diagramview.percentview.percententity

import com.hudson.customview.diagramview.percentview.type.sector.percentinfo.PercentInfo

/**
 * 动画变动的百分比实例
 * Created by Hudson on 2020/12/29.
 */
interface IAnimatedPercentEntity<T : PercentInfo> {
    /**
     * 获取当前实际显示的占比
     * @return
     */
    fun getPercentage(): Float

    /**
     * 设置变化动作的目标占比
     * @param targetPercentage
     */
    fun setTargetPercentage(targetPercentage: Float)

    /**
     * 获取该变化动作最初的占比
     * @return
     */
    fun getResPercentage(): Float

    /**
     * 设置变化动作的起始值。使用扇形图一方不应该回调本方法，因为
     * 内部扇形图会自行处理。
     * @param resPercentage
     */
    fun setResPercentage(resPercentage: Float)

    /**
     * 获取变化的目标占比
     * @return
     */
    fun getTargetPercentage(): Float

    /**
     * 设置当前显示的占比
     * @param percentage
     */
    fun setCurPercentage(percentage: Float)

    /**
     * 指变化动画完成后的操作
     * @param forceToTarget 有可能当前动画被取消了导致最后执行过程中取到的是中间值，而这时又回调了
     * onChangeFinished方法，如果该参数被设置为true，那么将会强制将
     * TargetPercentage（预期动画终止时占比）设置为最终的值，这将导致界面出
     * 现不连贯的刷新现象，因此不建议使用
     */
    fun onChangeFinished(forceToTarget: Boolean)
    fun getExtendInfo(): T
}
