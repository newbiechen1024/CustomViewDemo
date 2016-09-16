package com.newbiechen.commonviewdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.newbiechen.commonviewdemo.R;

/**
 * Created by PC on 2016/9/16.
 */
public class ScaleRecView extends View {
    private static final int DEFAULT_SIZE = 300;
    private static final int DEFAULT_STROKE_WIDTH = 20;
    private static final int DEFAULT_COLOR = Color.BLACK;

    private Paint mPaint;
    private Context mContext;

    private int mColor = DEFAULT_COLOR;
    private int mSquareSize;

    private int mViewWidth;
    private int mViewHeight;

    public ScaleRecView(Context context) {
        this(context,null);
    }

    public ScaleRecView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScaleRecView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initStyleable(attrs);
        initWidget();
    }

    private void initStyleable(AttributeSet attrs){
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.ScaleRecView);
        mColor = a.getColor(R.styleable.ScaleRecView_square_color,DEFAULT_COLOR);
        mSquareSize = a.getDimensionPixelSize(R.styleable.ScaleRecView_square_size,0);
    }

    private void initWidget(){
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
        mPaint.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMeasureSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMeasureSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec);

        int realWidth = 0;
        int realHeight = 0;

        if (widthMeasureMode == MeasureSpec.AT_MOST &&
                heightMeasureMode == MeasureSpec.AT_MOST){
            realWidth = DEFAULT_SIZE;
            realHeight = DEFAULT_SIZE;
        }
        else if (widthMeasureMode == MeasureSpec.EXACTLY &&
                heightMeasureMode == MeasureSpec.AT_MOST){
            realWidth = widthMeasureSize;
            realHeight = DEFAULT_SIZE;
        }
        else if (widthMeasureMode == MeasureSpec.AT_MOST
                && heightMeasureMode == MeasureSpec.EXACTLY){
            realWidth = DEFAULT_SIZE;
            realHeight = heightMeasureSize;
        }
        else {
            realWidth = widthMeasureSize;
            realHeight = heightMeasureSize;
        }

        setMeasuredDimension(realWidth, realHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int px = mViewWidth/2;
        int py = mViewHeight/2;
        //平移
        canvas.translate(px,py);

        if (mSquareSize == 0){
            mSquareSize = (Math.min(mViewWidth,mViewHeight)-100)/2;
        }

        Rect rect = new Rect(-mSquareSize,-mSquareSize,mSquareSize,mSquareSize);
        for (int i=1; i<20; ++i){
            //设置边缘大小
            mPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH - i);
            //缩放
            canvas.scale(0.9f,0.9f);
            //原理，是对Rect进行缩放，之后再绘制。不应该叫做画布缩放。
            //根据可叠加性，Canvas记住了当前的缩放比例，当下次绘制的时候，会先对被绘制对象进行缩放
            canvas.drawRect(rect,mPaint);
        }
    }
}
