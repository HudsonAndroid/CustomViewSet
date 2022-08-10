package com.hudson.register.annotation

/**
 * 子页面注解
 * Created by Hudson on 2022/8/10.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class SubPage(
    val desc: String
)
