package com.hudson.customview

import android.app.Activity
import android.app.Application
import android.content.Intent
import timber.log.Timber

/**
 * Created by Hudson on 2020/12/23.
 */
class CustomViewApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // 配置Timber
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }
}

// 打开指定Activity
inline fun <reified T: Activity> Activity.startTargetPage() {
    startActivity(Intent(this, T::class.java))
}