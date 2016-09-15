package com.newbiechen.pieviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.renderscript.Type;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by PC on 2016/9/15.
 */
public class PieView extends View {
    //默认半径的大小
    private static final int DEFAULT_RADIUS = 200;
    //默认的颜色
    private static final int DEFAULT_COLOR = android.R.color.holo_red_light;
    //可切换的颜色
    private static final int [] PAINT_COLORS = {android.R.color.darker_gray,android.R.color.holo_blue_light,
            android.R.color.holo_green_light};

    private Context mContext;
    private Paint mPaint;

    private final List<Number> mDataList = new ArrayList<>();

    private int mViewWidth;
    private int mViewHeight;
    private int mRadius = DEFAULT_RADIUS;
    //步骤一：
    public PieView(Context context) {
        this(context,null);
    }

    public PieView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public PieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initStyleable(attrs);
        initWidget();
    }
    //步骤六
    private void initStyleable(AttributeSet attrs){
        //获取自定义的半径
        TypedArray a = mContext.obtainStyledAttributes(attrs,R.styleable.PieView);
        mRadius = a.getDimensionPixelSize(R.styleable.PieView_radius,0);
    }

    //步骤四：
    private void initWidget(){
        mPaint = new Paint();
        //默认笔触为red
        mPaint.setColor(getResources().getColor(DEFAULT_COLOR));
        //抗锯齿
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    //步骤三：获取View的长和宽
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
    }

    //绘制图形
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //首先判断一下Radius
        if (mRadius == 0){
            mRadius = Math.min(mViewWidth,mViewHeight)/2;
        }
        //将画布移到中心位置
        canvas.translate(mViewWidth/2,mViewHeight/2);

        if (mDataList.size() == 0){
            //如果没有百分比数据，就显示默认的圆
            showNormalCircle(canvas);
        }
        else {
            //显示按照百分比制作的圆
            showPercentCircle(canvas);
        }
    }

    private void showNormalCircle(Canvas canvas){
        canvas.drawCircle(0,0,mRadius,mPaint);
    }

    private void showPercentCircle(Canvas canvas){
        int total = 0;
        //获取总数
        for (Number number : mDataList){
            total += number.intValue();
        }
        RectF rectF = new RectF(-mRadius,-mRadius,mRadius,mRadius);
        int startAngle = 0;
        for (int i=0; i< mDataList.size(); ++i){
            //获取百分比
            float percent = mDataList.get(i).floatValue() / total;
            //根据百分比，获取角度
            int angle = (int) (360 * percent);
            //切换颜色
            mPaint.setColor(getResources().getColor(PAINT_COLORS[i%3]));
            //绘制图形
            canvas.drawArc(rectF,startAngle,angle,true,mPaint);
            startAngle +=angle;
        }
    }

    //设置View的大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMeasureSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMeasureSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec);

        int resultWidth = 0;
        int resultHeight = 0;
        if (widthMeasureMode == MeasureSpec.AT_MOST &&
                heightMeasureMode == MeasureSpec.AT_MOST){
            resultWidth = dp2px(DEFAULT_RADIUS * 2);
            resultHeight = dp2px(DEFAULT_RADIUS * 2);
        }
        else if (widthMeasureMode  == MeasureSpec.EXACTLY &&
                heightMeasureMode == MeasureSpec.AT_MOST){
            resultWidth = widthMeasureSize;
            resultHeight = dp2px(DEFAULT_RADIUS * 2);
        }
        else if (widthMeasureMode == MeasureSpec.AT_MOST &&
                heightMeasureMode == MeasureSpec.EXACTLY){
            resultWidth = dp2px(DEFAULT_RADIUS * 2);
            resultHeight = heightMeasureSize;
        }
        else {
            resultWidth = widthMeasureSize;
            resultHeight = heightMeasureSize;
        }
        setMeasuredDimension(resultWidth,resultHeight);

    }

    private int dp2px(int dp){
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        float density = metrics.density;
        return  (int) (dp * density +0.5f);
    }

    /**********************公共的方法***************************************/

    public void addDatas(List<Number> dataList){
        mDataList.addAll(dataList);
    }

    public void addData(Number number){
        mDataList.add(number);
        postInvalidate();
    }
}
