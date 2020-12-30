package com.hudson.customview.diagramview.percentview.type.sector.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import com.hudson.customview.R
import com.hudson.customview.diagramview.percentview.type.sector.entity.percent.BasePercentEntity
import com.hudson.customview.diagramview.percentview.type.sector.entity.percent.EmptyPercentage
import com.hudson.customview.diagramview.percentview.type.sector.entity.percent.IPercentageEntity
import com.hudson.customview.diagramview.percentview.type.sector.entity.percent.LineShadeCreator
import com.hudson.customview.diagramview.percentview.type.sector.interfaces.OnCakeFocusListener
import com.hudson.customview.diagramview.percentview.type.sector.percentinfo.PercentInfo
import com.hudson.customview.diagramview.percentview.type.sector.view.indicator.SectorIndicatorView
import com.hudson.customview.diagramview.percentview.view.BasePercentView
import com.hudson.customview.getDimension
import timber.log.Timber
import kotlin.math.max
import kotlin.math.min

/**
 * 扇形统计图
 * 使用本控件需要注意：如果外界传入的占比数据
 * 出现总和超过100%的情况，将会自行调整数据，
 * 因此如果需要保证数据的原始性（即使这是错的），
 * 应该在外界自行暂存。
 * 位置说明：扇形将会绘制在给定空间的正中央，并
 * 且不支持padding。
 * Created by Hudson on 2020/12/29.
 */
abstract class SectorDiagramView<T: PercentInfo> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    BasePercentView<T, IPercentageEntity<T>>(context, attrs, defStyle) {
    private var mFocusEntity: IPercentageEntity<T>? = null
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mRadius = 0f
    private val mMaskFilter: BlurMaskFilter
    private val mShadowDimen: Float
    private var mOnCakeFocusListener: OnCakeFocusListener<T>? = null
    val mEmptyPercentage: EmptyPercentage<T> by lazy {
        getEmptyPercentage()
    }
    private var mBlurFocus = false //是否高亢阴影显示选中部分（由于该属性需要禁用硬件加速，会导致CPU占用增加）
    private var mIndicator: SectorIndicatorView<T>? = null
    private var mHollowRegion: Region? = null
    private var mIsCenterHollow = true //扇形图是否中空

    /**
     * 设置中空区域的颜色
     * @param hollowColor color
     */
    var hollowColor = Color.WHITE

    //按理通过Region#getBoundaryPath方法可以获取到路径，但是测试发现在模拟器上无法正常，为了避免出现问题
    //使用成员变量方式
    private var mHollowPath: Path? = null

    override fun addPercentEntity(entity: IPercentageEntity<T>) {
        super.addPercentEntity(entity)
        mIndicator?.addPercentageEntity(entity)
        checkCreateLineShadeShape(width, height)
    }

    override fun cleanPercentEntity() {
        super.cleanPercentEntity()
        mIndicator?.cleanPercentageEntity()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRadius = (min(w, h) / 2 - mShadowDimen) * FOCUS_SECTOR_RADIUS_PERCENTAGE
        if (mIsCenterHollow) {
            val radius = (mRadius * CENTER_HOLLOW_RADIUS_RATE).toInt()
            mHollowRegion = Region()
            mHollowRegion!![w / 2 - radius, h / 2 - radius, w / 2 + radius] = h / 2 + radius
            mHollowPath = Path()
            mHollowPath!!.addCircle(w / 2.toFloat(), h / 2.toFloat(), radius.toFloat(), Path.Direction.CW)
            mHollowRegion!!.setPath(mHollowPath!!, mHollowRegion!!)
        }
        checkCreateLineShadeShape(w, h)
    }

    private fun checkCreateLineShadeShape(w:Int, h: Int){
        if(w == 0 || h == 0) return
        if(LineShadeCreator.lineShadePath != null) return // Already created
        for (entity in percentEntityList) {
            if(entity is BasePercentEntity){
                if(entity.isLineShadeShow()){
                    LineShadeCreator.createLineShadeShape((w / 2).toFloat(), (h / 2).toFloat(), (max(w, h) / 2).toFloat())
                    break
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!percentEntityList.contains(mEmptyPercentage)) {
            percentEntityList.add(mEmptyPercentage)
        }
        if (mBlurFocus) {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
        var curPercentage = 0f
        var tmpPercentage = curPercentage
        for (entity in percentEntityList) {
            if (!entity.equals(mFocusEntity)) {
                mPaint.maskFilter = null
                entity.drawSector(
                    canvas,
                    curPercentage,
                    width / 2f,
                    height / 2f,
                    mRadius,
                    mPaint
                )
            } else {
                tmpPercentage = curPercentage
            }
            curPercentage += entity.getPercentage()
        }
        if (mFocusEntity != null) {
            if (mBlurFocus) {
                mPaint.maskFilter = mMaskFilter
            }
            mFocusEntity!!.onFocus(
                canvas,
                tmpPercentage,
                width / 2f,
                height / 2f,
                mRadius,
                mPaint
            )
        }
        if (mIsCenterHollow && mHollowPath != null) {
            mPaint.color = hollowColor
            //  Path boundaryPath = mHollowRegion.getBoundaryPath();在模拟器上无法正常，不知道是否有机型问题
            canvas.drawPath(mHollowPath!!, mPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_UP -> {
                var sectorRegion: Region
                var handle = false
                if (mIsCenterHollow && mHollowRegion!!.contains(event.x.toInt(), event.y.toInt())){
                    return super.onTouchEvent(event)
                }
                for (percentageEntity in percentEntityList) {
                    sectorRegion = percentageEntity.getSectorRegion()
                    if (sectorRegion.contains(
                            event.x.toInt(),
                            event.y.toInt()
                        )
                    ) {
                        mFocusEntity = percentageEntity
                        invalidate()
                        handle = true
                        mOnCakeFocusListener?.onFocus(percentageEntity)
                        break
                    }
                }
                if (!handle) {
                    mFocusEntity = null
                    invalidate()
                    mOnCakeFocusListener?.onCancelFocus()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    fun setOnCakeFocusListener(onCakeFocusListener: OnCakeFocusListener<T>) {
        mOnCakeFocusListener = onCakeFocusListener
    }

    var focusEntity: IPercentageEntity<T>?
        get() = mFocusEntity
        set(focusEntity) {
            if (mOnCakeFocusListener != null && mFocusEntity != null && focusEntity == null) {
                mOnCakeFocusListener?.onCancelFocus()
            }
            mFocusEntity = focusEntity
            invalidate()
        }

    // 当前是否是阴影高亢显示
    var isBlurFocus: Boolean
        get() = mBlurFocus
        /**
         * 设置选中部分是否高亢阴影显示
         * @param blurFocus 如果设置为true,用户选中部分将会模糊边缘达到阴影效果显示（默认false）,
         * 如果设置false，那么用户选中部分扇形仅半径增加。如果需要降低CPU占用，
         * 可以设置为false，随之则效果可能不是很好。
         */
        set(blurFocus) {
            mBlurFocus = blurFocus
            invalidate()
        }

    /**
     * 动态变动某个元素占比。不建议使用，因为整个扇形图之和必然为100%，除非原有
     * 之和小于100%情况下才适合使用；如果原有之和已经是100%，那么变动某个元素
     * 必然引起其他元素的变动，因此最好使用[BaseAnimatedPercentView.smoothChangeTo]
     * 联动修改。
     * @param index element index
     * @param target element target value
     */
    override fun smoothChangeTo(
        index: Int,
        target: Float,
        combineOthers: Boolean
    ) {
        super.smoothChangeTo(index, target, combineOthers)
    }

    override fun commonDetect(): Int {
        if (isDataChanging) {
            Timber.w("last animator is running,so skip!")
            return -1
        }
        var size: Int = percentEntityList.size
        if (percentEntityList.contains(mEmptyPercentage)) {
            size--
        }
        return size
    }

    fun associateIndicator(indicator: SectorIndicatorView<T>) {
        mIndicator = indicator
        addOnDataChangeListener(indicator)
        for (entity in percentEntityList) {
            mIndicator!!.addPercentageEntity(entity)
        }
    }

    /**
     * 设置扇形图是否中空显示
     * @param centerHollow if true,sector view will show with
     * a center hollow.
     */
    var isCenterHollow: Boolean
        get() = mIsCenterHollow
        set(centerHollow) {
            mIsCenterHollow = centerHollow
            invalidate()
        }

    companion object {
        const val FOCUS_SECTOR_RADIUS_PERCENTAGE = 0.9f
        const val CENTER_HOLLOW_RADIUS_RATE = 0.45f
        const val FLOAT_COMPARE_OFFSET = 0.000001f //float比较参考数
    }

    init {
        mPaint.isDither = true
        mPaint.style = Paint.Style.FILL
        mShadowDimen = context.getDimension(R.dimen.text_size_normal)
        mMaskFilter = BlurMaskFilter(mShadowDimen, BlurMaskFilter.Blur.SOLID)
    }

    abstract fun getEmptyPercentage(): EmptyPercentage<T>
}