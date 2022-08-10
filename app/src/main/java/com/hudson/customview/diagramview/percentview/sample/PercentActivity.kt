package com.hudson.customview.diagramview.percentview.sample

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.hudson.customview.R
import com.hudson.customview.diagramview.percentview.type.progress.SimpleAnimatedSetView
import com.hudson.customview.diagramview.percentview.type.sector.ClearFocusConstraintLayout
import com.hudson.customview.diagramview.percentview.type.sector.percentinfo.PercentInfo
import com.hudson.customview.diagramview.percentview.type.sector.view.SimpleGrowAlongSectorView
import com.hudson.customview.diagramview.percentview.type.sector.view.SimpleSweepSectorView
import com.hudson.customview.diagramview.percentview.type.sector.view.indicator.SimpleIndicatorView
import com.hudson.register.annotation.SubPage

@SubPage("百分比控件（扇形图）")
class PercentActivity : AppCompatActivity() {
    private lateinit var datas: List<SimulateData>
    private lateinit var sectorView: SimpleSweepSectorView
    private lateinit var growSectorView: SimpleGrowAlongSectorView
    private lateinit var progressSetView: SimpleAnimatedSetView
    private lateinit var rootView:ClearFocusConstraintLayout<PercentInfo>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 示例一：扫描式
        // 1.创建布局和创建指示器（这里使用了默认扇形和默认指示器）
        setContentView(R.layout.activity_percent)
        // 2.绑定扇形和指示器
        sectorView = findViewById<SimpleSweepSectorView>(R.id.sv_default)
        val indicator = findViewById<SimpleIndicatorView>(R.id.indicator_default)
        sectorView.associateIndicator(indicator)
        // 允许选中描边（会禁用硬件加速）
        sectorView.isBlurFocus = true
//        sectorView.isCenterHollow = false
        // 3.绑定数据并初始化
        datas = createSimulateData() // 创建模拟数据
        attachData(sectorView, datas)

        // 示例二：同时增长式
        growSectorView = findViewById<SimpleGrowAlongSectorView>(R.id.sv_grow_default)
        // 与示例一类似，这里不绑定任何指示器
        attachDataGrow(growSectorView, datas)

        // 示例三：非扇形图
        progressSetView = findViewById<SimpleAnimatedSetView>(R.id.as_default)
        attachProgressData(progressSetView, datas)

        // 同时触发初始化
        sectorView.dataReady()
        growSectorView.dataReady()
        progressSetView.dataReady()

        // 局部或者全局取消选中
        rootView = findViewById<ClearFocusConstraintLayout<PercentInfo>>(R.id.cl_root)
    }

    private fun attachData(sectorView: SimpleSweepSectorView, datas: List<SimulateData>){
        for (data in datas) {
            sectorView.addPercentInfo(PercentInfo(data.percentage, data.color, data.name, data.desc))
        }
//        sectorView.dataReady() // start initialize
    }

    private fun attachDataGrow(sectorView: SimpleGrowAlongSectorView, datas: List<SimulateData>){
        for (data in datas) {
            sectorView.addPercentInfo(PercentInfo(data.percentage, data.color, data.name, data.desc))
        }
//        sectorView.dataReady() // start initialize
    }

    private fun attachProgressData(view: SimpleAnimatedSetView, datas: List<SimulateData>){
        for (data in datas) {
            view.addPercentInfo(PercentInfo(data.percentage, data.color, data.name, data.desc), 20F)
        }
//        sectorView.dataReady() // start initialize
    }

    private fun createSimulateData(): List<SimulateData>{
        return mutableListOf<SimulateData>(
            SimulateData(0.2f, Color.BLACK, "first", ""),
            SimulateData(0.1f, Color.RED, "second", ""),
            SimulateData(0.35f, Color.BLUE, "third",""),
            SimulateData(0.15f, Color.CYAN, "forth", ""),
            SimulateData(0.2f, Color.MAGENTA,"fifth","")
        )
    }

    private var hasModify = false
    private fun modifySimulateData(/*datas: List<SimulateData>*/): FloatArray{
        if(!hasModify){
//            datas[0].percentage = 0.1f
//            datas[1].percentage = 0.2f
//            datas[2].percentage = 0.25f
//            datas[3].percentage = 0.3f
//            datas[4].percentage = 0.15f
            val floatArray = FloatArray(5)
            floatArray[0] = 0.1f
            floatArray[1] = 0.2f
            floatArray[2] = 0.25f
            floatArray[3] = 0.3f
            floatArray[4] = 0.15f
            hasModify = true
            return floatArray
        }else{
            // recover
//            datas[0].percentage = 0.2f
//            datas[1].percentage = 0.1f
//            datas[2].percentage = 0.35f
//            datas[3].percentage = 0.15f
//            datas[4].percentage = 0.2f
            val floatArray = FloatArray(5)
            floatArray[0] = 0.2f
            floatArray[1] = 0.1f
            floatArray[2] = 0.35f
            floatArray[3] = 0.15f
            floatArray[4] = 0.2f
            hasModify = false
            return floatArray
        }
    }

    fun smoothChange(view: View) {
        val changePercentArray = modifySimulateData()
        // 变动需要传入的参数是修改后的按照顺序的百分比数组 Float[]
        sectorView.smoothChangeTo(changePercentArray, true)
        growSectorView.smoothChangeTo(changePercentArray, true)
        progressSetView.smoothChangeTo(changePercentArray, true)

        // finally, invoke animator start
        sectorView.startSmoothChange()
    }

    fun directChange(view: View) {
        val changePercentArray = modifySimulateData()
        // 变动需要传入的参数是修改后的按照顺序的百分比数组 Float[]
        sectorView.changeTo(changePercentArray)
        growSectorView.changeTo(changePercentArray)
        progressSetView.changeTo(changePercentArray)
    }

    private var isHollow = true
    fun fillOrHollow(view: View) {
        isHollow = !isHollow
        growSectorView.isCenterHollow = isHollow
        sectorView.isCenterHollow = isHollow
    }

    private var globalCancel = false
    fun globalOrPart(view: View) {
        globalCancel = !globalCancel
        if(globalCancel){
            rootView.associateSectorView(sectorView,growSectorView)
        }else{
            rootView.cleanAssociateChildren()
        }
    }
}


data class SimulateData(
    var percentage: Float,
    val color: Int,
    val name: String,
    val desc: String
)