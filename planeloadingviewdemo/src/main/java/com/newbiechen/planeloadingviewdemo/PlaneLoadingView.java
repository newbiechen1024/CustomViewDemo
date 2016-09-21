package com.newbiechen.planeloadingviewdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by PC on 2016/9/21.
 */
public class PlaneLoadingView extends View {

    private static final int CIRCLE_RADIUS = 150;
    //飞机旋转的时间
    private static final int ANIM_TIME = 2000;

    private final Paint mCirclePaint = new Paint();
    private Bitmap mPlaneBitmap;

    private int mViewWidth;
    private int mViewHeight;

    private int mPlaneWidth;
    private int mPlaneHeight;

    private float [] pos = new float[2];
    private float [] tan = new float[2];

    private int mCircleRadius = CIRCLE_RADIUS;


    private float mCurrentDistance;
    public PlaneLoadingView(Context context) {
        this(context,null);
    }

    public PlaneLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PlaneLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWidget();
    }

    private void initWidget(){
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(4);
        mCirclePaint.setColor(Color.GRAY);
        mCirclePaint.setAntiAlias(true);
        initPlaneBitmap();
        setUpPlaneAnim();
    }

    /**
     * 获取图片
     */
    private void initPlaneBitmap(){
        //缩小飞机的尺寸
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        //获取飞机的图片
        mPlaneBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.plane,options);
        mPlaneWidth = mPlaneBitmap.getWidth();
        mPlaneHeight = mPlaneBitmap.getHeight();
    }

    private void setUpPlaneAnim(){
        //动画效果
        ValueAnimator animator = new ValueAnimator();
        //持续时间
        animator.setDuration(ANIM_TIME);
        //重复的方式
        animator.setRepeatMode(ValueAnimator.RESTART);
        //一直重复
        animator.setRepeatCount(-1);
        //设置线性插值器
        animator.setInterpolator(new LinearInterpolator());
        //设置圆的周长
        float perimeter = (float) (2.0 * Math.PI * mCircleRadius);
        animator.setFloatValues(perimeter);
        //监听回调
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentDistance= (float)animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
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
        //移动到画布的中心
        canvas.translate(mViewWidth/2,mViewHeight/2);
        //绘制圆
        Path circlePath = new Path();
        circlePath.addCircle(0,0,mCircleRadius, Path.Direction.CW);
        canvas.drawPath(circlePath,mCirclePaint);

        /**
         * 绘制飞机
         */
        //首先获取当前时间，圆的位置
        PathMeasure pathMeasure = new PathMeasure();
        pathMeasure.setPath(circlePath,true);
        //方法一
/*        Matrix matrix = new Matrix();
        pathMeasure.getMatrix(mCurrentDistance,matrix,PathMeasure.POSITION_MATRIX_FLAG|PathMeasure.TANGENT_MATRIX_FLAG);
        matrix.preTranslate(-mPlaneWidth/2,-mPlaneHeight/2);*/
        //方法2
        pathMeasure.getPosTan(mCurrentDistance,pos,tan);
        Matrix matrix = new Matrix();
        //根据x,y的tan值获取圆的切线的角度
        float degree = (float) (Math.atan2(tan[1],tan[0])*180/Math.PI);
        //设置图片的旋转中心
        matrix.postRotate(degree,mPlaneWidth/2,mPlaneHeight/2);
        //设置图片在画布中的位置
        matrix.postTranslate(pos[0]-mPlaneWidth/2,pos[1]-mPlaneHeight/2);
        canvas.drawBitmap(mPlaneBitmap,matrix,null);
    }




}
