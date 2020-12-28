package com.hudson.customview.diagramview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hudson.customview.R
import com.hudson.customview.diagramview.pillar.Config
import com.hudson.customview.diagramview.pillar.FixedPillarView
import com.hudson.customview.diagramview.pillar.ScrollablePillarView

class DiagramActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagram)

        // 可滚动柱形图示例
        val scrollablePillarView = findViewById<ScrollablePillarView>(R.id.spv_scrollable)
        with(scrollablePillarView){
            val context = this@DiagramActivity
            val config = Config()
                .textSize(R.dimen.text_size_normal, context)
                .itemPillarWidth(80F, context)
                .itemPillarDrawRatio(0.5f)
                .intervalCount(0)
                .minDataValue(1)
                .originalText("origin")
//                .normalColor(Color.RED)
//                .focusColor(Color.BLUE)
//                .enableTableLine(false)
//                .tableLineColor(Color.RED)
//                .tableLineWidth(2F, context)
            this.config = config
            setData(
                mutableListOf("day1","day2","day3", "day4","day5","day6","day7","day8","day9","day10","day11","day12","day13", "day14","day15","day16","day17","day18","day19","day20"),
                mutableListOf("bad", "ok","good","perfect").reversed(),
                mutableListOf(9,8,7,2,3,4,5,6,0,4,9,8,7,2,3,4,5,6,0,4),
                10,
                mutableListOf(5,9)
            )
        }

        // 固定自适应柱形图示例
        val fixedPillarView = findViewById<FixedPillarView>(R.id.spv_scrollable2)
        with(fixedPillarView){
            setData(
                mutableListOf("day1","day2","day3", "day4","day5","day6","day7","day8","day9","day10"),
                mutableListOf("bad", "ok","good","perfect").reversed(),
                mutableListOf(9,8,7,2,3,4,5,6,0,4),
                10,
                mutableListOf(5,9)
            )
        }
    }
}