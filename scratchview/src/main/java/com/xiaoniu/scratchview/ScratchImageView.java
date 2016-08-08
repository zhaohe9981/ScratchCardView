package com.xiaoniu.scratchview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by 小牛冲冲冲 on 2016/8/4.
 * Email:zhao_zhaohe@163.com
 */
public class ScratchImageView extends ImageView {

    private static final String TAG = ScratchImageView.class.getSimpleName();
    public ScratchImageView(Context context) {
        super(context);
        initView();
    }

    public ScratchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ScratchImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
    /**刮刮卡外面的蒙层*/
    private Bitmap outBitmap = null;
    private Paint mPaint = null;
    private Path mPath = null;
    private Canvas mCanvas;
    /**刮刮卡接口回调*/
    private ScratchListener listener = null;
    public void setScratchListener(ScratchListener listener) {
        this.listener = listener;
    }

    /**
     * 刮刮卡区域的宽高
     */
    private int width,height;
    /**
     * 是否刮卡完成的标志
     */
    private boolean isComplete = false;
    /***
     * 是否已经发出回调
     */
    private boolean isCallBak = false;
    /**初始化画笔和path*/
    private void initView(){

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setAlpha(0);
        mPaint.setStrokeWidth(50);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);

        mPath  = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(outBitmap);
        mCanvas.drawColor(Color.GRAY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isComplete){
            canvas.drawBitmap(outBitmap,0,0,null);
            mCanvas.drawPath(mPath, mPaint);
        }else if (!isCallBak){
            listener.scratchSuccess(ScratchImageView.this);
            isCallBak = true;
        }
    }

    private float mLastX = 0;
    private float mLastY = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();

        switch (action){
            case MotionEvent.ACTION_DOWN:
                mPath.reset();
                mPath.moveTo(x, y);
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(mLastX - x);
                float dy = Math.abs(mLastY - y);
                if (dx>=3||dy>=3){
                    mPath.lineTo(x,y);
                    mLastX = x;
                    mLastY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isComplete)
                    new Thread(check).start();
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }


    /**
     * 检查 刮刮卡 刮掉的区域，然后回调
     */
    Runnable check = new Runnable() {
        @Override
        public void run() {
            int scratchArea = 0;//挂掉区域的像素数
            int allArea = width*height;//刮刮卡所有的像素数

            int [] allPix = new int[allArea];
            outBitmap.getPixels(allPix,0,width,0,0,width,height);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (allPix[i+j*width] == 0){
                        scratchArea++;
                    }
                }
            }

            if(scratchArea >0&& allArea > 0){
                float percent = scratchArea*100/allArea;
                if (percent >= 70 && listener != null) {
                    Log.i(TAG, "百分比为：" + percent + "%");
                    isComplete = true;
                    postInvalidate();
                }
            }

        }
    };

}
