package com.hudson.customview.circlewaveview.wave;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by hpz on 2019/1/23.
 */
public interface IDrawEntity {

    /**
     * 绘制
     * @param canvas 画布
     * @param paint 画笔
     */
    void draw(Canvas canvas, Paint paint);
}
