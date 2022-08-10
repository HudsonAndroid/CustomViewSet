package com.hudson.customview.circleseekbar

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.hudson.customview.R
import com.hudson.customview.gesturelock.GestureLockActivity
import com.hudson.register.annotation.SubPage
import timber.log.Timber

@SubPage("圆形进度条")
class CircleSeekBarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circle_seek_bar)

        val seekBar = findViewById<CircleSeekBar>(R.id.cs_seek)
        with(seekBar){
            setMax(100)
        }
        seekBar.setOnUserSelectProgressListener {
            Timber.e("User has select progress, the percentage is ${seekBar.touchPercent}")
        }
    }
}