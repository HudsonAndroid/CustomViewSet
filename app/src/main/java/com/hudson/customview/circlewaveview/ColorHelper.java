package com.hudson.customview.circlewaveview;

/**
 * Created by Hudson on 2019/1/20.
 */
public class ColorHelper {

    /**
     * 获取对应颜色的透明色
     * @param resColor
     * @return 透明色
     */
    public static int getTransparentColor(int resColor){
        return resColor & 0xffffff;
    }

    /**
     * 根据占比，来获取渐变色对应的颜色
     * 思路来源：系统渐变色动画
     * @param fraction 占比0.0-1.0之间
     * @param startColor 起始颜色
     * @param endColor 终止颜色
     * @return
     */
    public static int evaluate(float fraction, int startColor, int endColor) {
        int startA = (startColor >> 24) & 0xff;
        int startR = (startColor >> 16) & 0xff;
        int startG = (startColor >> 8) & 0xff;
        int startB = startColor & 0xff;
        int endA = (endColor >> 24) & 0xff;
        int endR = (endColor >> 16) & 0xff;
        int endG = (endColor >> 8) & 0xff;
        int endB = endColor & 0xff;
        return ((startA + (int)(fraction * (endA - startA))) << 24) |
                ((startR + (int)(fraction * (endR - startR))) << 16) |
                ((startG + (int)(fraction * (endG - startG))) << 8) |
                ((startB + (int)(fraction * (endB - startB))));
    }

    /**
     * start-middle 0-0.5  ---->fraction*2 --->0-1
     * middle-end  0.5-1.0  ---> (fraction-0.5)*2 --->0-1
     * 前半部分占0.5，后半部分占0.5
     * @param fraction
     * @param startColor
     * @param middleColor
     * @param endColor
     * @return
     */
    public static int evaluate(float fraction,int startColor,int middleColor,int endColor){
        if(fraction<=0.5){
            return evaluate(fraction*2,startColor,middleColor);
        }else{
            return evaluate((fraction-0.5f)*2,middleColor,endColor);
        }
    }

    /**
     * 有n个中间颜色
     * @param fraction
     * @param colors
     * @return
     */
    public static int evaluate(float fraction,int[] colors){
        int length = colors.length;
        if(length ==0){
            throw new IllegalStateException("the colors is null!");
        }
        if(length == 1){
            return colors[0];//返回原色
        }
        if(length == 2){
            return evaluate(fraction,colors[0],colors[1]);
        }
        //计算出每两个颜色占用的比值
        float eachFraction = 1.0f / (length -1);
        int index = (int)(fraction / eachFraction);//注意不需要四舍五入
        if(index == length-1){
            index = length-2;
        }
        return evaluate((fraction - index*eachFraction)*(length-1),colors[index],colors[index+1]);
    }
}
