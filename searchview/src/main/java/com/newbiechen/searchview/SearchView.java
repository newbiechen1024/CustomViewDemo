package com.newbiechen.searchview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by PC on 2016/9/21.
 */
public class SearchView extends View {
    private static final String TAG = "SearchView";
    //圆环的大小
    private static final int SMALL_CIRCLE_RADIUS = 25;
    private static final int BIG_CIRCLE_RADIUS = 50;
    //放大镜消失的动画时长
    private static final int SEARCH_ANIM = 2000;
    //大圆环加载一圈使用的事件
    private static final int CIRCLE_ANIM = 2000;

    private final Paint mSearchPaint = new Paint();
    private final Paint mCirclePaint = new Paint();

    private final Path mSearchPath = new Path();
    private final Path mCirclePath = new Path();

    private final PathMeasure mPathMeasure = new PathMeasure();

    private final ValueAnimator mSearchAnimator = new ValueAnimator();
    private final ValueAnimator mCircleAnimator = new ValueAnimator();
    private final ValueAnimator mEndAnimator = new ValueAnimator();

    private int mViewWidth;
    private int mViewHeight;

    private float mSearchCurrentLoc;
    private float mCircleCurveStartLoc;
    private float mCircleCurveEndLoc;
    private State mState = State.NONE;

    public SearchView(Context context) {
        this(context,null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWidget();
    }

    private void initWidget(){
        mSearchPaint.setStyle(Paint.Style.STROKE);
        mSearchPaint.setStrokeWidth(8);
        mSearchPaint.setColor(Color.BLUE);
        mSearchPaint.setAntiAlias(true);
        //将截取的曲线起始于末尾部分设为圆角
        mSearchPaint.setStrokeCap(Paint.Cap.ROUND);

        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(8);
        mCirclePaint.setColor(getResources().getColor(R.color.colorAccent));
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        setUpValueAnimator();
    }

    private void setUpValueAnimator(){
        //放大镜的长度
        final float searchLength = (float)(2*Math.PI*SMALL_CIRCLE_RADIUS + (BIG_CIRCLE_RADIUS - SMALL_CIRCLE_RADIUS));
        //大圆的长度
        final float bigCircleLength = (float) (2 * Math.PI * BIG_CIRCLE_RADIUS);

        mSearchAnimator.setDuration(SEARCH_ANIM);
        mSearchAnimator.setInterpolator(new LinearInterpolator());
        mSearchAnimator.setFloatValues(searchLength);
        mSearchAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSearchCurrentLoc = (float) animation.getAnimatedValue();
                //启动加载动画
                if (mSearchCurrentLoc == searchLength){
                    mState = State.LOADING;
                    //因为结束的时候停止旋转，所以启动的时候要开启旋转
                    mCircleAnimator.setRepeatCount(-1);
                    mCircleAnimator.start();
                }
                invalidate();
            }
        });

        //设置圆圈的旋转
        mCircleAnimator.setDuration(CIRCLE_ANIM);
        mCircleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        //去掉曲线的最大值的圆的长度
        mCircleAnimator.setFloatValues(bigCircleLength * (270.0f/360));
        mCircleAnimator.setRepeatMode(ValueAnimator.RESTART);
        mCircleAnimator.setRepeatCount(-1);
        mCircleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            //曲线改变长度的距离：圆环135度所对应的长度
            float speedLength = (float) (bigCircleLength * (135.0/360));
            //曲线扩大的长度:圆环90度所对应的长度
            float expandLength = bigCircleLength * (90.0f/360);
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                mCircleCurveStartLoc = (float) animation.getAnimatedValue();
                //-5是防止防止fraction结束的时候为0的情况
                float fraction = ((mCircleCurveStartLoc-5) % speedLength)/ speedLength;

                if (mCircleCurveStartLoc <= speedLength){
                    //百分比
                    mCircleCurveEndLoc = mCircleCurveStartLoc + expandLength * fraction;
                }
                else{
                    mCircleCurveEndLoc = mCircleCurveStartLoc + expandLength;
                    //8表示给曲线弄一个小尾巴
                    mCircleCurveStartLoc = mCircleCurveStartLoc + expandLength * fraction;
                }
                //结束加载
                if (mState == State.END){
                    mCircleAnimator.setRepeatCount(0);
                    mEndAnimator.start();
                }
                invalidate();
            }
        });

        //重新显示放大镜
        mEndAnimator.setDuration(SEARCH_ANIM);
        mEndAnimator.setInterpolator(new LinearInterpolator());
        mEndAnimator.setFloatValues(searchLength);
        mEndAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSearchCurrentLoc = (float) animation.getAnimatedValue();
                //结束绘制回复原状
                if (mSearchCurrentLoc == searchLength){
                    mState = State.NONE;
                }
                invalidate();
            }
        });

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
        //绘制路径
        drawPath();


        if (mState == State.NONE){
            //将路径画在画板上
            canvas.drawPath(mSearchPath,mSearchPaint);
        }
        else if (mState == State.START){
            //隐藏路径动画
            hideSearchPath(canvas);
        }
        else if (mState == State.LOADING || mCircleAnimator.isRunning()){
            //展示加载动画
            showCircleLoading(canvas);
        }
        else if (mState == State.END){
            showSearchPath(canvas);
        }
    }

    private void drawPath(){
        float [] pos = new float[2];
        mSearchPath.reset();
        // 放大镜圆环
        RectF oval1 = new RectF(-SMALL_CIRCLE_RADIUS, -SMALL_CIRCLE_RADIUS,
                SMALL_CIRCLE_RADIUS, SMALL_CIRCLE_RADIUS);
        mSearchPath.addArc(oval1, 45, 359.9f);
        //绘制最外层的圆环
        RectF oval2 = new RectF(-BIG_CIRCLE_RADIUS,-BIG_CIRCLE_RADIUS,
                BIG_CIRCLE_RADIUS,BIG_CIRCLE_RADIUS);
        //反向绘制圆环
        mCirclePath.addArc(oval2, 45, -359.9f);

        //获取放大镜的把手在大圆环的位置
        mPathMeasure.setPath(mCirclePath,false);
        mPathMeasure.getPosTan(0,pos,null);
        //连接到大圆环
        mSearchPath.lineTo(pos[0],pos[1]);
    }

    private void hideSearchPath(Canvas canvas){
        //绘制搜索图
        Path relPath = new Path();
        mPathMeasure.setPath(mSearchPath,false);
        mPathMeasure.getSegment(mSearchCurrentLoc,mPathMeasure.getLength(),relPath,true);
        canvas.drawPath(relPath,mSearchPaint);
    }

    private void showCircleLoading(Canvas canvas){
        //绘制加载的圆
        Path relPath = new Path();
        mPathMeasure.setPath(mCirclePath,false);
        mPathMeasure.getSegment(mCircleCurveStartLoc, mCircleCurveEndLoc,relPath,true);
        canvas.drawPath(relPath,mCirclePaint);
    }

    private void showSearchPath(Canvas canvas){
        Path relPath = new Path();
        mPathMeasure.setPath(mSearchPath,false);
        mPathMeasure.getSegment(mPathMeasure.getLength()-mSearchCurrentLoc,mPathMeasure.getLength(),relPath,true);
        canvas.drawPath(relPath,mSearchPaint);
    }

    private enum State{
        NONE,
        START,
        LOADING,
        END
    }

    /*****************公共的方法**************************/
    //开始搜索
    public void startSearch(){
        if(mState == State.NONE){
            mState = State.START;
            mSearchAnimator.start();
        }
    }

    //搜索完成，停止搜索
    public void finishSearch(){
        if(mState == State.LOADING){
            mState = State.END;
        }
    }
}
