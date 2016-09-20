package com.newbiechen.bazierloveviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 2016/9/20.
 */
public class BazierLoveView extends View {
    //点的半径
    private static final int POINT_RADIUS = 10;
    //初始圆的半径
    private static final int CIRCLE_RADIUS = 150;
    //控制点的数量
    private static final int CONTROL_POINT_COUNT = 8;
    //八个点与起始点的距离
    private static final int CONTROL_DISTANCE = 75;

    //将其分为四分，左上圆弧的两个控制点为第一步分（然后按照左上、右上、右下、左下的规则）
    private static final int [] CONTROL_POINT_X = {-CIRCLE_RADIUS,-CONTROL_DISTANCE,CONTROL_DISTANCE,CIRCLE_RADIUS,CIRCLE_RADIUS,CONTROL_DISTANCE,-CONTROL_DISTANCE,-CIRCLE_RADIUS};
    private static final int [] CONTROL_POINT_Y = {-CONTROL_DISTANCE,-CIRCLE_RADIUS,-CIRCLE_RADIUS,-CONTROL_DISTANCE,CONTROL_DISTANCE,CIRCLE_RADIUS,CIRCLE_RADIUS,CONTROL_DISTANCE};

    private final List<Point> mStartPointList = new ArrayList<>();
    private final List<Point> mControlPointList = new ArrayList<>();
    private final Path mBaizerPath = new Path();
    /***************初始化贝塞尔曲线的点************************/
    private Point mLeftStartPoint;
    private Point mTopStartPoint;
    private Point mRightStartPoint;
    private Point mBelowStartPoint;

    private Point mLTLeftControlPoint;
    private Point mLTTopControlPoint;
    private Point mRTTopControlPoint;
    private Point mRTRightControlPoint;
    private Point mRBRightControlPoint;
    private Point mRBBelowControlPoint;
    private Point mLBBelowControlPoint;
    private Point mLBLeftControlPoint;

    private final Paint mStartPointPaint = new Paint();
    private final Paint mControlPointPaint = new Paint();
    private final Paint mLinePaint = new Paint();
    private final Paint mBazierPaint = new Paint();

    private boolean isDrawPointAndLine = true;
    private int mViewWidth;
    private int mViewHeight;

    public BazierLoveView(Context context) {
        this(context,null);
    }

    public BazierLoveView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BazierLoveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWidget();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
    }

    private void initWidget(){
        //四个起始点的画笔
        mStartPointPaint.setStyle(Paint.Style.STROKE);
        mStartPointPaint.setStrokeWidth(2);
        mStartPointPaint.setColor(Color.GRAY);

        //控制点的画笔
        mControlPointPaint.setStyle(Paint.Style.FILL);
        mControlPointPaint.setColor(Color.GRAY);

        //连接线的画笔
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(Color.GRAY);

        //三阶贝塞尔曲线的画笔
        mBazierPaint.setStyle(Paint.Style.STROKE);
        mBazierPaint.setStrokeWidth(4);
        mBazierPaint.setColor(Color.RED);

        //初始化点
        initStartPoint();
        initControlPoint();

    }

    private void initStartPoint(){
        /*这是举得反例- -，可以对比controlPoint的方法*/
        mLeftStartPoint = new Point();
        mLeftStartPoint.x = -CIRCLE_RADIUS;
        mLeftStartPoint.y = 0;
        mStartPointList.add(mLeftStartPoint);

        mTopStartPoint = new Point();
        mTopStartPoint.x = 0;
        mTopStartPoint.y = -CIRCLE_RADIUS;
        mStartPointList.add(mTopStartPoint);

        mRightStartPoint = new Point();
        mRightStartPoint.x = CIRCLE_RADIUS;
        mRightStartPoint.y = 0;
        mStartPointList.add(mRightStartPoint);

        mBelowStartPoint = new Point();
        mBelowStartPoint.x = 0;
        mBelowStartPoint.y = CIRCLE_RADIUS;
        mStartPointList.add(mBelowStartPoint);
    }

    private void initControlPoint(){

        for (int i=0; i<CONTROL_POINT_COUNT; ++i){
            Point point = new Point();
            point.x = CONTROL_POINT_X[i];
            point.y = CONTROL_POINT_Y[i];
            mControlPointList.add(point);
        }
        //给八个点赋值
        mLTLeftControlPoint = mControlPointList.get(0);
        mLTTopControlPoint = mControlPointList.get(1);
        mRTTopControlPoint = mControlPointList.get(2);
        mRTRightControlPoint = mControlPointList.get(3);
        mRBRightControlPoint = mControlPointList.get(4);
        mRBBelowControlPoint = mControlPointList.get(5);
        mLBBelowControlPoint = mControlPointList.get(6);
        mLBLeftControlPoint = mControlPointList.get(7);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mViewWidth/2,mViewHeight/2);
        if (isDrawPointAndLine){
            drawStartPoint(canvas);
            drawControlPoint(canvas);
            drawConnectLine(canvas);
        }
        drawBazierCurve(canvas);
    }

    private void drawStartPoint(Canvas canvas){
        /*其实使用数组更方便，但是不能体现面向对象的特性。*/
        /*还有其他渐变的方法，比如说使用List等，为了演示，写最清晰易懂的方式*/
        canvas.drawCircle(mLeftStartPoint.x,mLeftStartPoint.y,POINT_RADIUS,mStartPointPaint);
        canvas.drawCircle(mTopStartPoint.x,mTopStartPoint.y,POINT_RADIUS,mStartPointPaint);
        canvas.drawCircle(mRightStartPoint.x,mRightStartPoint.y,POINT_RADIUS,mStartPointPaint);
        canvas.drawCircle(mBelowStartPoint.x,mBelowStartPoint.y,POINT_RADIUS,mStartPointPaint);
    }

    private void drawControlPoint(Canvas canvas){
        for (int i=0; i<mControlPointList.size(); ++i){
            final Point paint = mControlPointList.get(i);
            canvas.drawCircle(paint.x,paint.y,POINT_RADIUS,mControlPointPaint);
        }
    }

    private void drawConnectLine(Canvas canvas){
        //左边开始
        canvas.drawLine(mLeftStartPoint.x,mLeftStartPoint.y, mLTLeftControlPoint.x,
                mLTLeftControlPoint.y,mLinePaint);
        canvas.drawLine(mLeftStartPoint.x,mLeftStartPoint.y, mLBLeftControlPoint.x,
                mLBLeftControlPoint.y,mLinePaint);

        canvas.drawLine(mTopStartPoint.x,mTopStartPoint.y,mLTTopControlPoint.x,
                mLTTopControlPoint.y,mLinePaint);
        canvas.drawLine(mTopStartPoint.x,mTopStartPoint.y,mRTTopControlPoint.x,
                mRTTopControlPoint.y,mLinePaint);

        canvas.drawLine(mRightStartPoint.x,mRightStartPoint.y,mRTRightControlPoint.x,
                mRTRightControlPoint.y,mLinePaint);
        canvas.drawLine(mRightStartPoint.x,mRightStartPoint.y,mRBRightControlPoint.x,
                mRBRightControlPoint.y,mLinePaint);

        canvas.drawLine(mBelowStartPoint.x,mBelowStartPoint.y,mRBBelowControlPoint.x,
                mRBBelowControlPoint.y,mLinePaint);
        canvas.drawLine(mBelowStartPoint.x,mBelowStartPoint.y,mLBBelowControlPoint.x,
                mLBBelowControlPoint.y,mLinePaint);
    }

    private void drawBazierCurve(Canvas canvas){
        //这里其实也可以使用for循环
        mBaizerPath.moveTo(mLeftStartPoint.x,mLeftStartPoint.y);
        mBaizerPath.cubicTo(mLTLeftControlPoint.x,mLTLeftControlPoint.y,
                mLTTopControlPoint.x,mLTTopControlPoint.y,mTopStartPoint.x,mTopStartPoint.y);
        canvas.drawPath(mBaizerPath,mBazierPaint);
        mBaizerPath.reset();

        mBaizerPath.moveTo(mTopStartPoint.x,mTopStartPoint.y);
        mBaizerPath.cubicTo(mRTTopControlPoint.x,mRTTopControlPoint.y,
                mRTRightControlPoint.x,mRTRightControlPoint.y,mRightStartPoint.x,mRightStartPoint.y);
        canvas.drawPath(mBaizerPath,mBazierPaint);
        mBaizerPath.reset();

        mBaizerPath.moveTo(mRightStartPoint.x,mRightStartPoint.y);
        mBaizerPath.cubicTo(mRBRightControlPoint.x,mRBRightControlPoint.y,
                mRBBelowControlPoint.x,mRBBelowControlPoint.y,mBelowStartPoint.x,mBelowStartPoint.y);
        canvas.drawPath(mBaizerPath,mBazierPaint);
        mBaizerPath.reset();

        mBaizerPath.moveTo(mBelowStartPoint.x,mBelowStartPoint.y);
        mBaizerPath.cubicTo(mLBBelowControlPoint.x,mLBBelowControlPoint.y,mLBLeftControlPoint.x,
                mLBLeftControlPoint.y,mLeftStartPoint.x,mLeftStartPoint.y);
        canvas.drawPath(mBaizerPath,mBazierPaint);
        mBaizerPath.reset();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX() - mViewWidth/2;
        int y = (int) event.getY() - mViewHeight/2;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                //只有当控制点显示的时候，才存在点击事件
                if (isDrawPointAndLine){
                    List<Point> pointList = new ArrayList<>();
                    pointList.addAll(mControlPointList);
                    pointList.addAll(mStartPointList);
                    //进行判断，点击到了哪个点
                    for(int i=0; i< pointList.size(); ++i){
                        Point point = pointList.get(i);
                        int left = point.x-POINT_RADIUS*3;
                        int top = point.y-POINT_RADIUS*3;
                        int right = point.x+POINT_RADIUS*3;
                        int below = point.y+POINT_RADIUS*3;
                        Rect rect = new Rect(left,top,right
                                ,below);
                        if (rect.contains(x,y)){
                            point.x = x;
                            point.y = y;
                        }
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    private class Point {
        int x;
        int y;
    }

    public void restart(){
        mStartPointList.clear();
        mControlPointList.clear();

        initStartPoint();
        initControlPoint();
        invalidate();
    }

    public void isDrawPointAndLine(boolean isDraw){
        isDrawPointAndLine = isDraw;
        invalidate();
    }

}
