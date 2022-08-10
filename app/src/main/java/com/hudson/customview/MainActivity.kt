package com.hudson.customview

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.hudson.apt.CustomViewSetPageRegister
import com.hudson.customview.wraplayout.WrapLayout


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val parent = findViewById<WrapLayout>(R.id.wrap_container)

        val pages = CustomViewSetPageRegister().pages
        pages.keys.forEach { clazz ->
            parent.addView(Button(this).apply {
                text = pages[clazz]
                setOnClickListener {
                    startActivity(Intent(this@MainActivity, clazz))
                }
            })
        }
    }
}