package com.hudson.customview.diagramview.table.pillar.horizontalaxis

import com.hudson.customview.diagramview.table.pillar.Config

/**
 * 水平轴是根据给定的固定宽度自适应的，内容不超出边界（不滚动）
 * Created by Hudson on 2020/12/23.
 */
class FixedAxis(
    datas: List<String>,
    config: Config
) : CommonHorizontalAxis(datas, config) {

    // 由于内容不超出边界（不滚动），因此宽度需要均分到每一个部分
    override fun getItemWidth(totalWidth: Float, dataSize: Int) = totalWidth / dataSize
}