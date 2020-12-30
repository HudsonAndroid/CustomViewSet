package com.hudson.customview.diagramview.percentview.type.sector.percentinfo

/**
 * 占比信息，核心是百分比数据
 * Created by Hudson on 2020/12/29.
 */
open class PercentInfo constructor(
    var percentage: Float,
    val color: Int,
    val name: String = "",
    val desc: String? = null
)