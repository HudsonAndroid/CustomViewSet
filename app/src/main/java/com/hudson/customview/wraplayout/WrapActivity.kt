package com.hudson.customview.wraplayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hudson.customview.R
import com.hudson.register.annotation.SubPage

@SubPage("类似Flutter的Wrap控件")
class WrapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wrap)
    }
}