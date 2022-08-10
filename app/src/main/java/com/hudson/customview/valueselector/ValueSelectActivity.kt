package com.hudson.customview.valueselector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import com.hudson.customview.R
import com.hudson.register.annotation.SubPage

@SubPage("百分比值选择控件")
class ValueSelectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_value_select)
    }
}