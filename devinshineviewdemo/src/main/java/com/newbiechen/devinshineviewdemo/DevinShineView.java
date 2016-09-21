package com.newbiechen.devinshineviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 2016/9/20.
 */
public class DevinShineView extends View {
    //默认的颜色
    private static final int DEFAULT_BAZIER_COLOR = 0xFFF77B7B;
    //初始圆的半径
    private static final int CIRCLE_RADIUS = 60;
    //控制点的距离
    private static final float BAZIER_CIRCLE = 0.552284749831f;
    private static final float CONTROL_DISTANCE = CIRCLE_RADIUS * BAZIER_CIRCLE;
    //起始点与控制点的数量
    private static final int START_POINT_COUNT = 4;
    private static final int CONTROL_POINT_COUNT = 8;
    //设置特效需要移动的距离
    private static final int EFFECT_DISTANCE = 40;
    //设置动画的时长
    private static final long ANIM_TIME = 1500;

    //设置四个起始点。同样按照左上右下的顺序排列
    private static final float [] START_POINT_X = {-CIRCLE_RADIUS,0,CIRCLE_RADIUS,0};
    private static final float [] START_POINT_Y = {0,-CIRCLE_RADIUS,0,CIRCLE_RADIUS};
    //设置控制点
    //将其分为四分，左上圆弧的两个控制点为第一步分（然后按照左上、右上、右下、左下的规则）
    private static final float [] CONTROL_POINT_X = {-CIRCLE_RADIUS,-CONTROL_DISTANCE,CONTROL_DISTANCE,CIRCLE_RADIUS,CIRCLE_RADIUS,CONTROL_DISTANCE,-CONTROL_DISTANCE,-CIRCLE_RADIUS};
    private static final float [] CONTROL_POINT_Y = {-CONTROL_DISTANCE,-CIRCLE_RADIUS,-CIRCLE_RADIUS,-CONTROL_DISTANCE,CONTROL_DISTANCE,CIRCLE_RADIUS,CIRCLE_RADIUS,CONTROL_DISTANCE};

    private List<Point> mStartPointList = new ArrayList<>();
    private List<Point> mControlPointList = new ArrayList<>();

    private final Paint mBaizerPaint = new Paint();

    private int mBaizerColor = DEFAULT_BAZIER_COLOR;

    private int mViewWidth;
    private int mViewHeight;

    private long mAnimTime = ANIM_TIME;
    private long startTime = System.currentTimeMillis();

    private boolean isStart = false;

    public DevinShineView(Context context) {
        this(context,null);
    }

    public DevinShineView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DevinShineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWidget();
    }

    private void initWidget(){
        //Bazier制作的效果的颜色
        mBaizerPaint.setStyle(Paint.Style.FILL);
        mBaizerPaint.setColor(mBaizerColor);
        mBaizerPaint.setAntiAlias(true);
        mBaizerPaint.setDither(true);
        initPoint();
    }

    private void initPoint(){
        //先初始化start点
        for(int i=0; i<START_POINT_COUNT; ++i){
            Point point = new Point();
            point.x = START_POINT_X[i];
            point.y = START_POINT_Y[i];
            mStartPointList.add(point);
        }

        //初始化控制点
        for (int i=0; i<CONTROL_POINT_COUNT; ++i){
            Point point = new Point();
            point.x = CONTROL_POINT_X[i];
            point.y = CONTROL_POINT_Y[i];
            mControlPointList.add(point);
        }
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
        canvas.translate(CIRCLE_RADIUS,mViewHeight/2);
        drawBazierCurve(canvas);
        if (isStart){
            //从左移到右
            moveBazierCurve();
        }
    }

    private void drawBazierCurve(Canvas canvas){
        Path path = new Path();
        //根据原理推理出的遍历形式
        for (int i=0; i<mStartPointList.size(); ++i){
            Point mFirstPoint = mStartPointList.get(i);
            Point mSecondPoint = mControlPointList.get(i*2);
            Point mThirdPoint = mControlPointList.get(i*2+1);
            Point mForthPoint = mStartPointList.get((i+1)%START_POINT_COUNT);
            if (i == 0){
                path.moveTo(mFirstPoint.x,mFirstPoint.y);
            }
            path.cubicTo(mSecondPoint.x,mSecondPoint.y,mThirdPoint.x,mThirdPoint.y,
                    mForthPoint.x,mForthPoint.y);
        }
        canvas.drawPath(path,mBaizerPaint);
    }

    private void moveBazierCurve(){
        //设定当前的事件
        long currentTime = System.currentTimeMillis();
        //设定时间差与总时间的百分比
        float fraction = (float)(currentTime-startTime) / mAnimTime;
        //设定移动的距离
        float moveDistance = fraction * (mViewWidth - CIRCLE_RADIUS*2);
        //如果在百分比内，则移动
        if (fraction <= 1){
            for (int i=0; i<mStartPointList.size(); ++i){
                Point point = mStartPointList.get(i);
                point.x = START_POINT_X[i] + moveDistance;
            }
            for (int i=0; i<mControlPointList.size(); ++i){
                Point point = mControlPointList.get(i);
                point.x = CONTROL_POINT_X[i] + moveDistance;
            }
            //改变效果
            if(fraction <= 0.25){
                float effectFraction = (float)(currentTime-startTime) / (mAnimTime/4);
                if (effectFraction > 1){
                    effectFraction = 1;
                }
                //设定移动的距离
                float effectMoveDistance = effectFraction * EFFECT_DISTANCE;
                mStartPointList.get(2).x = mStartPointList.get(2).x + effectMoveDistance;
                mControlPointList.get(3).x = mControlPointList.get(3).x + effectMoveDistance;
                mControlPointList.get(4).x = mControlPointList.get(4).x + effectMoveDistance;
            }
            else if (fraction <= 0.5){
                mStartPointList.get(2).x = mStartPointList.get(2).x + EFFECT_DISTANCE;
                mControlPointList.get(3).x = mControlPointList.get(3).x + EFFECT_DISTANCE;
                mControlPointList.get(4).x = mControlPointList.get(4).x + EFFECT_DISTANCE;

                float effectFraction = (float)(currentTime-startTime-mAnimTime/4) / (mAnimTime/4);
                if (effectFraction > 1){
                    effectFraction = 1;
                }
                //设定移动的距离
                float effectMoveDistance = effectFraction * EFFECT_DISTANCE;
                mStartPointList.get(0).x -= effectMoveDistance;
                mControlPointList.get(0).x -= effectMoveDistance;
                mControlPointList.get(7).x -= effectMoveDistance;
            }
            else if (fraction <= 0.75){
                mStartPointList.get(2).x = mStartPointList.get(2).x + EFFECT_DISTANCE;
                mControlPointList.get(3).x = mControlPointList.get(3).x + EFFECT_DISTANCE;
                mControlPointList.get(4).x = mControlPointList.get(4).x + EFFECT_DISTANCE;
                mStartPointList.get(0).x -= EFFECT_DISTANCE;
                mControlPointList.get(0).x -= EFFECT_DISTANCE;
                mControlPointList.get(7).x -= EFFECT_DISTANCE;

                float effectFraction = (float)(currentTime-startTime-mAnimTime/2) / (mAnimTime/4);
                if (effectFraction > 1){
                    effectFraction = 1;
                }
                //设定移动的距离
                float effectMoveDistance = effectFraction * EFFECT_DISTANCE;
                mStartPointList.get(2).x = mStartPointList.get(2).x - effectMoveDistance;
                mControlPointList.get(3).x = mControlPointList.get(3).x - effectMoveDistance;
                mControlPointList.get(4).x = mControlPointList.get(4).x - effectMoveDistance;
            }
            else {
                mStartPointList.get(0).x -= EFFECT_DISTANCE;
                mControlPointList.get(0).x -= EFFECT_DISTANCE;
                mControlPointList.get(7).x -= EFFECT_DISTANCE;

                float effectFraction = (float)(currentTime-startTime-mAnimTime+mAnimTime/4) / (mAnimTime/4);
                if (effectFraction > 1){
                    effectFraction = 1;
                }
                //设定移动的距离
                float effectMoveDistance = effectFraction * EFFECT_DISTANCE;
                mStartPointList.get(0).x += effectMoveDistance;
                mControlPointList.get(0).x += effectMoveDistance;
                mControlPointList.get(7).x += effectMoveDistance;
            }
            invalidate();
        }
    }

    public void startShow(boolean start){
        isStart = start;
        startTime = System.currentTimeMillis();
        invalidate();
    }

    private class Point {
        float x;
        float y;
    }
}
