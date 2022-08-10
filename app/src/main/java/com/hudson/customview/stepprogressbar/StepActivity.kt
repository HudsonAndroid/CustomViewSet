package com.hudson.customview.stepprogressbar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hudson.customview.R
import com.hudson.register.annotation.SubPage

@SubPage("步骤进度条控件")
class StepActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step)


        val stepProgressView = findViewById<StepProgressView>(R.id.step)
        val stepCounts = stepProgressView.stepCounts
        val strs = arrayOfNulls<String>(stepCounts)
        for (i in 0 until stepCounts) {
            strs[i] = "步骤$i"
        }
        stepProgressView.setStepDesc(strs)
        stepProgressView.curStepIndex = 6
        stepProgressView.setOnItemClickListener { i -> stepProgressView.curStepIndex = i }
    }
}