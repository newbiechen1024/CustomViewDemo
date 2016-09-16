package com.newbiechen.commonviewdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.newbiechen.commonviewdemo.R;

/**
 * Created by PC on 2016/9/16.
 */
public class CheckBoxView extends View {
    private static final String TAG = "CheckBoxView";
    private static final int DEFAULT_COLOR = android.R.color.holo_orange_dark;
    private static final int BITMAP_SHOW_SIZE = 200;
    private static final int ANIM_DURATION = 50;
    private static final int TOTAL_PAGE = 13;
    private static final int ANIM_NULL = 0;
    private static final int ANIM_CHECK = 1;
    private static final int ANIM_UNCHECK = 2;
    private Paint mPaint;
    private Bitmap mShowBitmap;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (anim_status){
                //表示绘制完成
                case ANIM_NULL:
                    break;
                //表示绘制点击
                case ANIM_CHECK:
                    if (mBitmapPage < TOTAL_PAGE-1){
                        mBitmapPage += 1;
                        CheckBoxView.this.invalidate();
                    }
                    else {
                        mBitmapPage = TOTAL_PAGE;
                        anim_status = ANIM_NULL;
                    }
                    break;
                //表示绘制擦除
                case ANIM_UNCHECK:
                    if (mBitmapPage >= 0){
                        mBitmapPage -= 1;
                        CheckBoxView.this.invalidate();
                    }
                    else {
                        mBitmapPage = -1;
                        anim_status = ANIM_NULL;
                    }
                    break;
            }
        }
    };
    private boolean isCheck = false;

    private int mViewWidth;
    private int mViewHeight;
    private int mBitmapSize;
    private int mBitmapPage = -1;
    private int anim_status = ANIM_NULL;
    public CheckBoxView(Context context) {
        this(context,null);
    }

    public CheckBoxView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CheckBoxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWidget();
    }

    private void initWidget(){
        //初始化画笔
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(DEFAULT_COLOR));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        //初始化图片
        mShowBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.checkmark);
        mBitmapSize = mShowBitmap.getHeight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将画布移到中心
        canvas.translate(mViewWidth/2,mViewHeight/2);
        //绘制一个圆
        paintCircle(canvas);
        //绘制圆中的钩
        paintBitmap(canvas);
    }

    private void paintCircle(Canvas canvas){
        int radius = Math.min(mViewWidth,mViewHeight)/2;
        canvas.drawCircle(0,0,radius,mPaint);
    }

    private void paintBitmap(Canvas canvas){
        //设置图片展示区域，和展示图片的区域
        Rect src = new Rect(mBitmapSize * mBitmapPage,
                0,mBitmapSize * (mBitmapPage + 1),mBitmapSize);
        RectF dst = new RectF(-mBitmapSize,-mBitmapSize,
                mBitmapSize, mBitmapSize);
        //绘制图片
        canvas.drawBitmap(mShowBitmap,src,dst,null);
        mHandler.sendEmptyMessageDelayed(0,ANIM_DURATION);
    }

    public void check(){
        //正在绘制中
        if (anim_status == ANIM_NULL && isCheck == false){
            anim_status = ANIM_CHECK;
            isCheck = true;
            mHandler.sendEmptyMessage(0);
        }
    }

    public void unCheck(){
        if (anim_status == ANIM_NULL && isCheck == true){
            anim_status = ANIM_UNCHECK;
            isCheck = false;
            mHandler.sendEmptyMessage(0);
        }
    }
}
