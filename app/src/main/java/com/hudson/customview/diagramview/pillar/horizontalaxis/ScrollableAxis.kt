package com.hudson.customview.diagramview.pillar.horizontalaxis

import com.hudson.customview.diagramview.pillar.Config

/**
 * 可滚动的水平轴(x轴)
 * Created by Hudson on 2020/12/23.
 */
class ScrollableAxis(
    datas: List<String>,
    config: Config
) : CommonHorizontalAxis(datas, config) {

    override fun getItemWidth(totalWidth: Float, dataSize: Int) = config.itemPillarWith
}