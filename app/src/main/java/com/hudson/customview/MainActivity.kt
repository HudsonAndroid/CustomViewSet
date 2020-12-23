package com.hudson.customview

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hudson.customview.circleseekbar.CircleSeekBarActivity
import com.hudson.customview.gesturelock.GestureLockActivity
import com.hudson.customview.stepprogressbar.StepActivity
import com.hudson.customview.valueselector.ValueSelectActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun jump(view: View) {
        startTargetPage<ValueSelectActivity>()
    }
}