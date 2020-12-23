package com.hudson.customview.circlewaveview.wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hpz on 2019/1/22.
 */
public class CircleWaveView extends SurfaceView implements SurfaceHolder.Callback,Runnable{
    private static final float RADIUS_START = 500f;
    private static final float WAVE_WIDTH = 2f;
    private SurfaceHolder mSurfaceHolder;
    private Canvas mCanvas;
    private final List<CircleWave> mWaves = new ArrayList<>();
    //子线程标志位
    private boolean mIsDrawing;
    private Paint mPaint;
    private Thread mThread;


    public CircleWaveView(Context context) {
        this(context, null);
    }

    public CircleWaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleWaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);//注册回调方法
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(WAVE_WIDTH);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {//创建
        mIsDrawing = true;
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {//改变

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {//销毁
        mIsDrawing = false;
        CircleWave.cleanRecycleList();
    }

    @Override
    public void run() {
        while (mIsDrawing){
            draw();
        }
    }

    /**
     * 创建波浪
     */
    public void createCenterWave(int radius,int color){
        CircleWave wave = CircleWave.obtain(getWidth()/2,getHeight()/2,RADIUS_START,Color.RED);
        mWaves.add(wave);
    }

    protected void draw(){
        try{
            mCanvas = mSurfaceHolder.lockCanvas();
            mCanvas.drawColor(Color.WHITE);
            CircleWave wave;
            for (int i = 0; i < mWaves.size(); i++) {
                wave = mWaves.get(i);
                if(wave.isUnused()){
                    mWaves.remove(wave);
                    i--;
                }else{
                    wave.draw(mCanvas,mPaint);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            Surface surface = mSurfaceHolder.getSurface();
            if(mCanvas != null && surface != null&& surface.isValid()){
                try{
                    //释放canvas，并提交绘制结果
                    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
