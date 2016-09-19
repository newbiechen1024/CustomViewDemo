package com.newbiechen.bazierquad;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by PC on 2016/9/18.
 */
public class BazierQuadView extends View {

    private static final int ORIGIN_DISTANCE = 200;
    private static final int ORIGIN_RADIUS = 15;

    private final Paint mPointPaint = new Paint();
    private final Paint mLinePaint = new Paint();
    private final Paint mPathPaint = new Paint();
    //Bazier的三个点
    private Point mStartPoint;
    private Point mControlPoint;
    private Point mEndPoint;

    private int mViewWidth;
    private int mViewHeight;

    public BazierQuadView(Context context) {
        this(context,null);
    }

    public BazierQuadView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BazierQuadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWidget();
    }

    private void initWidget(){
        mPointPaint.setAntiAlias(true);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setColor(Color.GRAY);

        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(Color.GRAY);

        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setColor(Color.RED);
        mPathPaint.setStrokeWidth(4);

        //初始化点
        mStartPoint = new Point();
        mStartPoint.x = -ORIGIN_DISTANCE;
        mStartPoint.y = 0;
        mControlPoint = new Point();
        mControlPoint.x = 0;
        mControlPoint.y = -ORIGIN_DISTANCE;
        mEndPoint = new Point();
        mEndPoint.x = ORIGIN_DISTANCE;
        mEndPoint.y = 0;
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
        canvas.translate(mViewWidth/2,mViewHeight/2);
        drawBaizerCurve(canvas);
        drawPointAndLine(canvas);
    }

    private void drawBaizerCurve(Canvas canvas){
        //绘制曲线
        Path path = new Path();
        //起始点是moveTo()
        path.moveTo(mStartPoint.x,mStartPoint.y);
        //然后使用quadTo()确定剩下的两个点
        path.quadTo(mControlPoint.x,mControlPoint.y,mEndPoint.x,mEndPoint.y);
        canvas.drawPath(path, mPathPaint);
    }

    private void drawPointAndLine(Canvas canvas){
        //绘制点
        canvas.drawCircle(mStartPoint.x,mStartPoint.y,ORIGIN_RADIUS,mPointPaint);
        canvas.drawCircle(mControlPoint.x,mControlPoint.y,ORIGIN_RADIUS,mPointPaint);
        canvas.drawCircle(mEndPoint.x,mEndPoint.y,ORIGIN_RADIUS,mPointPaint);
        //绘制线
        canvas.drawLine(mStartPoint.x,mStartPoint.y,mControlPoint.x,mControlPoint.y,mLinePaint);
        canvas.drawLine(mControlPoint.x,mControlPoint.y,mEndPoint.x,mEndPoint.y,mLinePaint);

    }


    private class Point {
        int x;
        int y;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                //为什么要减去中点。因为绘制的时候将坐标移到了中点，但是鼠标点击的时候的坐标点是View的左上角的点。
                //所以为了点的统一
                mControlPoint.x = x - mViewWidth/2;
                mControlPoint.y = y - mViewHeight/2;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }


}
