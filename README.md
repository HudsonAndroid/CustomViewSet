# 自定义控件(Android)合集
各个自定义控件按各个文件夹分类存储。注意：部分自定义控件携带自定义属性，在/res/values/attrs.xml中寻找相关属性
## [1.数学统计图自定义控件](/app/src/main/java/com/hudson/customview/diagramview)
### [表格型（目前仅实现了柱形图）统计图](/app/src/main/java/com/hudson/customview/diagramview/table)
![动画展示](/app/src/main/java/com/hudson/customview/diagramview/table/resources/show.gif)
### [扇形统计图](/app/src/main/java/com/hudson/customview/diagramview/percentview)
![动画展示](/app/src/main/java/com/hudson/customview/diagramview/percentview/resources/show.gif)
## [2.手势锁](/app/src/main/java/com/hudson/customview/gesturelock)
![动画展示](/app/src/main/java/com/hudson/customview/gesturelock/resources/show.gif)
## [3.歌词播放器自定义控件](https://github.com/HudsonAndroid/NewLyricsView)
备注：该自定义控件目前还有一个问题就是横向播放歌词的方式下，一句歌词的竖直方向展示可能会超标（超出屏幕边界），导致出现问题。当时（代码是之前写的）有
想到的方案是：检测歌词整体结构是否是中文的，如果是才这样展示，并在超标的情况下分行。由于后面没有继续处理下去，因此留存在这里。

被[NewDongLing](https://github.com/HudsonAndroid/NewDongLing)使用，效果详见该项目。
## [4.圆形SeekBar](/app/src/main/java/com/hudson/customview/circleseekbar)
![动画展示](/app/src/main/java/com/hudson/customview/circleseekbar/resources/show.gif)
## [5.步骤进度条](/app/src/main/java/com/hudson/customview/stepprogressbar)
![动画展示](/app/src/main/java/com/hudson/customview/stepprogressbar/resources/show.gif)
## [6.数值选择器](/app/src/main/java/com/hudson/customview/valueselector)
![动画展示](/app/src/main/java/com/hudson/customview/valueselector/resources/show.gif)
## [7.WrapLayout](/app/src/main/java/com/hudson/customview/wraplayout)
<img src="/app/src/main/java/com/hudson/customview/wraplayout/resources/display.png" width="320" alt="图片展示"/>

## [8.圆形音频律动控件](/app/src/main/java/com/hudson/customview/circlewaveview)
使用方见[NewDongLing](https://github.com/HudsonAndroid/NewDongLing)
## [9.滚轮自定义控件](https://github.com/HudsonAndroid/WheelView)