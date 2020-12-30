package com.hudson.customview

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hudson.customview.diagramview.percentview.sample.PercentActivity
import com.hudson.customview.diagramview.table.TableDiagramActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun jump(view: View) {
        startTargetPage<PercentActivity>()
    }
}