package com.hudson.customview.circlewaveview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Hudson on 2019/1/23.
 */
public class CircleVisibleView extends RelativeLayout {
//    private CircleWaveView mWaveView;
    private CircleVisibleMusicView mMusicView;
    private int mLastData = -1;
    
    public CircleVisibleView(Context context) {
        this(context, null);
    }

    public CircleVisibleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleVisibleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        mWaveView = new CircleWaveView(context);
        mMusicView = new CircleVisibleMusicView(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(CENTER_IN_PARENT);
//        addView(mWaveView, params);
        addView(mMusicView,params);
        mMusicView.setCakeCount(100);
        mMusicView.setColors(new int[]{0xFFDD001B,0xFFBBCF00,0xFF1F7246});
        mMusicView.setCakeCount(100);
        mMusicView.setMainAlpha(90);
        mMusicView.setSecondAlpha(40);
        mMusicView.setCakeMinHeight(10);
        mMusicView.setRotateColor(true);
    }

    public void updateVisualizer(byte[] fft) {
        int[] model = new int[fft.length / 2 + 1];
//        model[0] =  Math.abs(fft[0]);
        for (int i = 0, j = 0; j < 100;) {
            model[j] = (int) Math.hypot(fft[i], fft[i + 1]);
            i += 2;
            j++;
        }
//        if(mLastData >=4 && model[2] - mLastData > mLastData*3/2){
//            mWaveView.createCenterWave(mMusicView.getSpaceRadius(),mMusicView.getCurColor());
//        }
        mLastData = model[2];
//        Log.e("hudson","数据"+model[3]);
        mMusicView.setDatas(model);
    }

}
