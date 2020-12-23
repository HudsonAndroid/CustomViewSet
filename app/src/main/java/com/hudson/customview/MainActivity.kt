package com.hudson.customview

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hudson.customview.circleseekbar.CircleSeekBarActivity
import com.hudson.customview.gesturelock.GestureLockActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun jump(view: View) {
        CircleSeekBarActivity.start(this)
    }
}