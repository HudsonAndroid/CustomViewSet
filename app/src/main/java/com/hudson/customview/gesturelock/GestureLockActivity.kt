package com.hudson.customview.gesturelock

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.hudson.customview.R

class GestureLockActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gesture_lock)

        val lock = findViewById<GestureLock>(R.id.lock)
        lock.setOnGestureLockListener(object : GestureLock.OnGestureLockListener {
            override fun onGesturePasswdCorrect() {
                Toast.makeText(this@GestureLockActivity, "密码正确", Toast.LENGTH_LONG).show()
            }

            override fun onGesturePasswdWrong(times: Int) {
                Toast.makeText(this@GestureLockActivity, "密码错误", Toast.LENGTH_LONG).show()
            }

            override fun onTimesOver() {}
        })
    }

    companion object{
        fun start(from: Context) {
            from.startActivity(Intent(from, GestureLockActivity::class.java));
        }
    }
}