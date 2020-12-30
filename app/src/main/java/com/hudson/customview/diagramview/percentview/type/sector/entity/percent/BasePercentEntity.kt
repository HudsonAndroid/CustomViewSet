package com.hudson.customview.diagramview.percentview.type.sector.entity.percent

import android.graphics.*
import com.hudson.customview.diagramview.percentview.type.sector.percentinfo.PercentInfo
import com.hudson.customview.diagramview.percentview.type.sector.view.SectorDiagramView
import com.hudson.customview.diagramview.percentview.type.sector.view.SectorDiagramView.Companion.FOCUS_SECTOR_RADIUS_PERCENTAGE
import timber.log.Timber
import kotlin.math.abs


class SimplePercentageEntity(data: PercentInfo): BasePercentEntity<PercentInfo>(data)

/**
 * 如果当前元素的百分比加上原有百分比之和超过100%,那么
 * 该元素的实际占比将会被调整，后续再加入的元素占比将
 * 会被置为0%。
 * Created by Hudson on 2020/12/29.
 */
open class BasePercentEntity<T : PercentInfo>(data: T) :
    IPercentageEntity<T> {
    protected var mTargetPath: Path
    protected var mTargetRegion: Region
    protected var mResRegion: Region
    protected var mPercentage: Float
    private val mTargetRect: RectF
    private val mFocusRect: RectF
    private val mExtendData: T
    protected var mTargetPercentage: Float
    protected var mResPercentage: Float
    protected var mColor: Int
    private var mCakeDivideOffset = 3f //百分块之间的缝隙参照值
    private val mMatrix: Matrix
    private var mLineShadeShow = false //是否线条阴影显示。（尽量不要使用，影响性能）
    override fun setTargetPercentage(targetPercentage: Float) {
        mTargetPercentage = adjustValue(targetPercentage)
    }

    private fun adjustValue(valueInput: Float): Float {
        var valueInput = valueInput
        valueInput = if (valueInput > 1.0f) 1.0f else valueInput
        valueInput = if (valueInput < 0) 0f else valueInput
        return valueInput
    }

    override fun getTargetPercentage(): Float {
        return mTargetPercentage
    }

    override fun getResPercentage(): Float {
        return mResPercentage
    }

    override fun setResPercentage(resPercentage: Float) {
        mResPercentage = adjustValue(resPercentage)
    }

    override fun setCurPercentage(percentage: Float) {
        mPercentage = adjustValue(percentage)
        mExtendData.percentage = percentage
    }

    override fun onChangeFinished(forceToTarget: Boolean) {
        if (forceToTarget) {
            setCurPercentage(mTargetPercentage)
        }
        mResPercentage = mPercentage
        mTargetPercentage = mPercentage
    }

    override fun getExtendInfo(): T {
        return mExtendData
    }

    override fun drawSector(
        canvas: Canvas,
        curPercentage: Float,
        centerX: Float,
        centerY: Float,
        radius: Float,
        paint: Paint
    ) {
        if (curPercentage >= 1.0f) {
            mPercentage = 0f
            mExtendData.percentage = mPercentage
            return
        }
        paint.color = mColor
        mTargetPath.reset()
        mTargetPath.moveTo(centerX, centerY)
        mTargetRect.left = centerX - radius
        mTargetRect.top = centerY - radius
        mTargetRect.right = centerX + radius
        mTargetRect.bottom = centerY + radius
        val sweepPercentage = getSweepPercentage(curPercentage)
        if (sweepPercentage >= 1.0f) { //ARC滑过的距离到达360度时会重新开始绘制弧线，为了避免出现问题此时设置为整圆
            mTargetPath.addCircle(centerX, centerY, radius, Path.Direction.CW)
        } else {
            mTargetPath.arcTo(mTargetRect, 360f * curPercentage - 90f, 360 * sweepPercentage)
            mTargetPath.close()
            val value =
                (360 * curPercentage + 360 * sweepPercentage / 2) / 180 * Math.PI
            mMatrix.setTranslate(
                (mCakeDivideOffset * Math.sin(value)).toFloat(),
                (-(mCakeDivideOffset * Math.cos(value))).toFloat()
            )
            mTargetPath.transform(mMatrix)
        }
        var saveId = -1
        if (mLineShadeShow) {
            saveId = canvas.save()
            val lineShadePath: Path = LineShadeCreator.lineShadePath!!
            if (lineShadePath != null) {
                canvas.clipPath(lineShadePath, Region.Op.DIFFERENCE)
            }
        }
        mResRegion[mTargetRect.left.toInt(), mTargetRect.top.toInt(), mTargetRect.right.toInt()] =
            mTargetRect.bottom.toInt()
        mTargetRegion.setPath(mTargetPath, mResRegion) //更新点击块
        canvas.drawPath(mTargetPath, paint)
        if (saveId != -1) {
            canvas.restore()
        }
    }

    override fun onFocus(
        canvas: Canvas,
        curPercentage: Float,
        centerX: Float,
        centerY: Float,
        radius: Float,
        paint: Paint
    ) {
        if (!needDetectFocus()) {
            return
        }
        paint.color = mColor
        mTargetPath.reset()
        val offset: Float =
            radius / FOCUS_SECTOR_RADIUS_PERCENTAGE * (1f - FOCUS_SECTOR_RADIUS_PERCENTAGE)
        mTargetPath.moveTo(
            (mTargetRect.left + mTargetRect.right) / 2,
            (mTargetRect.top + mTargetRect.bottom) / 2
        )
        mFocusRect.set(mTargetRect)
        mFocusRect.left -= offset
        mFocusRect.top -= offset
        mFocusRect.right += offset
        mFocusRect.bottom += offset
        val sweepPercentage = getSweepPercentage(curPercentage)
        if (sweepPercentage >= 1.0f) {
            mTargetPath.addCircle(
                centerX,
                centerY,
                radius + offset,
                Path.Direction.CW
            )
        } else {
            mTargetPath.arcTo(mFocusRect, 360f * curPercentage - 90f, 360 * sweepPercentage)
            mTargetPath.close()
        }
        var saveId = -1
        if (mLineShadeShow) {
            saveId = canvas.save()
            val lineShadePath: Path = LineShadeCreator.lineShadePath!!
            if (lineShadePath != null) {
                canvas.clipPath(lineShadePath, Region.Op.DIFFERENCE)
            }
        }
        mTargetRegion.setPath(mTargetPath, mResRegion) //更新点击块
        canvas.drawPath(mTargetPath, paint)
        if (saveId != -1) {
            canvas.restore()
        }
    }

    override fun getPercentage(): Float {
        return mPercentage
    }

    override fun getSectorRegion(): Region {
        return mTargetRegion
    }

    open fun getSweepPercentage(curPercentage: Float): Float {
        if (curPercentage + mPercentage > 1.0f) {
            val remainPer = 1.0f - curPercentage
            if(abs(remainPer - mPercentage) > SectorDiagramView.FLOAT_COMPARE_OFFSET){
                // We think is out of 100%, we should modify and notify the user
                Timber.w("total percentage out of 100\\%,the sector view change percentage")
            }
            // 修正值
            mPercentage = if (remainPer < 0) 0f else remainPer
            mExtendData.percentage = mPercentage
        }
        return mPercentage
    }

    open fun needDetectFocus(): Boolean {
        return mPercentage > 0
    }

    fun setCakeDivideOffset(cakeDivideOffset: Float) {
        mCakeDivideOffset = cakeDivideOffset
    }

    fun getCakeDivideOffset(): Float {
        return mCakeDivideOffset
    }

    fun isLineShadeShow(): Boolean {
        return mLineShadeShow
    }

    /**
     * 设置该扇形块增加阴影线，仅在需要同一类颜色区分的情况下使用
     * 不建议使用，因为阴影线需要额外对Path进行相应地clip操作，增加负担
     */
    fun setLineShadeShow(lineShadeShow: Boolean) {
        mLineShadeShow = lineShadeShow
    }

    init {
        mTargetPath = Path()
        mTargetRegion = Region()
        mResRegion = Region()
        mTargetRect = RectF()
        mFocusRect = RectF()
        mExtendData = data
        mColor = data.color
        mPercentage = data.percentage
        mResPercentage = mPercentage
        mTargetPercentage = mResPercentage
        mMatrix = Matrix()
    }
}