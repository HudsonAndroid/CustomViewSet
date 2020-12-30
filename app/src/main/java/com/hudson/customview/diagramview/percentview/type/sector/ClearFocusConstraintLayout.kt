package com.hudson.customview.diagramview.percentview.type.sector

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import com.hudson.customview.diagramview.percentview.type.sector.percentinfo.PercentInfo
import com.hudson.customview.diagramview.percentview.type.sector.view.SectorDiagramView
import java.util.*

/**
 * 由于扇形控件某个占比高亢选中之后可能需要在外界点击了非
 * 扇形控件恢复不选中状态，因此可以使用本控件包裹扇形控件
 * 和其他控件。需要使用者手动关联对应的扇形统计图控件。
 *
 * 初始思路通过focus（焦点）来实现，但是发现其与ScrollView
 * 结合之后会出现自动滑动问题，因此修改方案
 * Created by Hudson on 2020/12/30.
 */
class ClearFocusConstraintLayout<T:PercentInfo> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {
    private val mAssociateChildren: MutableList<SectorDiagramView<T>>

    /**
     * 关联对应的扇形图控件。扇形控件应该是其子view
     * @param children 扇形控件集合
     */
    fun associateSectorView(vararg children: SectorDiagramView<T>) {
        for (child in children) {
            if (child != null && !mAssociateChildren.contains(child)) {
                mAssociateChildren.add(child)
            }
        }
    }

    fun cleanAssociateChildren() {
        mAssociateChildren.clear()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        for (child in mAssociateChildren) {
            child.focusEntity = null
        }
        return super.dispatchTouchEvent(ev)
    }

    init {
        mAssociateChildren = ArrayList<SectorDiagramView<T>>()
    }
}