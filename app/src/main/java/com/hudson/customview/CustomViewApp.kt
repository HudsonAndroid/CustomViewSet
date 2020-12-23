package com.hudson.customview

import android.app.Application
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