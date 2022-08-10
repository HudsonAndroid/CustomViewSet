package com.hudson.register_annotation_processor

import com.squareup.javapoet.ClassName

/**
 * 页面信息
 * Created by Hudson on 2022/8/10.
 */
data class PageInfo(
    val pageClazz: ClassName,
    val desc: String
)