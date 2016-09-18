package com.newbiechen.radarviewdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 2016/9/18.
 */
public class RadarView extends View {
    private static final int TRANSLUCENT_BLUE = 0x993F51B5;
    //绘制个数
    private static final int ORIGIN_SHAPE_COUNT = 5;
    //多边形点的个数，由于是六边形，则为6
    private static final int POINT_COUNT = 6;
    //点的半径
    private static final int POINT_RADIUS = 10;
    //第一个形状的半径
    private static final int ORIGIN_RADIUS = 70;
    //各个形状的距离
    private static final int ORIGIN_DISTANCE=  50;
    //文字显示的距离
    private static final int ORIGIN_TEXT_DISTANCE = 15;
    //总值的大小
    private static final int TOTAL_COUNT = 100;

    private Context mContext;
    private final List<String> mTextList = new ArrayList<>();
    private final List<Integer> mScoreList = new ArrayList<>();

    //绘制蜘网图的画笔
    private final Paint mSpiderPaint = new Paint();
    //文字的画笔
    private final Paint mTextPaint = new Paint();
    //绘制点
    private final Paint mPointPaint = new Paint();
    //绘制属性区域
    private final Paint mAreaPaint = new Paint();
    //View的尺寸
    private int mViewWidth;
    private int mViewHeight;

    private int mRadius = ORIGIN_RADIUS;
    private int mDistance = ORIGIN_DISTANCE;
    private int mCount = ORIGIN_SHAPE_COUNT;
    private int mTextDistance = ORIGIN_TEXT_DISTANCE;
    //最大半径
    private int mMaxRadius;
    //多边形的弧度
    private double mRadian;

    public RadarView(Context context) {
        this(context,null);
    }

    public RadarView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initWidget();
    }

    private void initWidget(){
        mSpiderPaint.setAntiAlias(true);
        mSpiderPaint.setStyle(Paint.Style.STROKE);
        mSpiderPaint.setColor(Color.BLACK);

        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(20);
        mTextPaint.setColor(Color.BLACK);

        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setColor(Color.BLUE);

        mAreaPaint.setStyle(Paint.Style.FILL);
        mAreaPaint.setColor(TRANSLUCENT_BLUE);

        //设置六边形的旋转角
        mRadian = 2 * Math.PI / POINT_COUNT;

        //为了兼容不同分辨率
        mRadius = dp2px(mRadius);
        mDistance = dp2px(mDistance);
        mTextDistance = dp2px(mTextDistance);
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
        //画布转移到View的中点
        canvas.translate(mViewWidth/2,mViewHeight/2);
        //绘制六边形的形状
        drawShape(canvas);
        //绘制网的线
        drawShapeLine(canvas);
        //绘制点和面
        drawPointAndArea(canvas);
    }

    private void drawShape(Canvas canvas){
        //设置绘制的距离
        int radius = mRadius;
        Path spiderPath = new Path();
        //绘制形状的个数
        for (int i=0; i< mCount; ++i){
            //重置Path
            spiderPath.reset();
            //设置六边形的半径
            radius = mRadius + mDistance * i;
            //绘制六边形的各个点
            for (int j=0; j<POINT_COUNT; ++j){
                float x = (float) (radius * Math.cos(mRadian * j));
                float y = (float) (radius * Math.sin(mRadian * j));
                if (j == 0){
                    //设置初始绘制点
                    spiderPath.moveTo(radius,0);
                }
                else {
                    spiderPath.lineTo(x,y);
                }
            }
            //合并
            spiderPath.close();
            canvas.drawPath(spiderPath,mSpiderPaint);
        }
        //获取最大半径
        mMaxRadius = radius;

        //绘制文字
        /*************未完成，完整的文字位置(根据不同象限来判断 - -，不做了)***************/
        for (int i=0; i<POINT_COUNT; ++i){
            float width = mTextPaint.measureText(mTextList.get(i));

            float x = (float) ((radius+mTextDistance) * Math.cos(mRadian * i));
            float y = (float) ((radius+mTextDistance) * Math.sin(mRadian * i));
            canvas.drawText(mTextList.get(i),x,y+width/2,mTextPaint);
        }
    }

    private void drawShapeLine(Canvas canvas){
        //获取最大的Radius,mCount-1是因为mRadius就是已经算在mCount中了
        int maxRadius = mRadius + mDistance * (mCount-1);
        for (int i=0; i<POINT_COUNT; ++i){
            //旋转画布
            canvas.rotate((float) Math.toDegrees(mRadian));
            canvas.drawLine(0,0, maxRadius,0,mSpiderPaint);
        }
    }

    private void drawPointAndArea(Canvas canvas){
        Path path = new Path();
        for (int i=0; i<POINT_COUNT; ++i){
            //获取旋转角
            double radian = mRadian * i;
            //获取百分比
            float percent = (float) mScoreList.get(i) / TOTAL_COUNT;
            //获取radius
            float radius = mMaxRadius * percent;
            //获取x,y
            float x = (float) (radius * Math.cos(radian));
            float y = (float) (radius * Math.sin(radian));
            //绘制路径
            if (i == 0){
                path.moveTo(x,y);
            }
            else {
                path.lineTo(x,y);
            }
            //绘制点
            canvas.drawCircle(x,y,dp2px(POINT_RADIUS),mPointPaint);
        }
        //绘制面
        canvas.drawPath(path,mAreaPaint);
    }

    private int dp2px(int dp){
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        float density = metrics.density;
        return (int) (dp * density + 0.5f);
    }

    /*****************************公共的方法************************************************/
    public void setRadarScore(List<Integer> scoreList){
        mScoreList.clear();
        //只取前六个
        for(int i=0; i<POINT_COUNT; ++i){
            mScoreList.add(scoreList.get(i));
        }
    }

    public void setRadarScore(int[] scores){
        mScoreList.clear();
        //只取前六个
        for(int i=0; i<POINT_COUNT; ++i){
            mScoreList.add(scores[i]);
        }
    }

    public void setRadarText(List<String> textList){
        mTextList.clear();
        //只取前六个
        for(int i=0; i<POINT_COUNT; ++i){
            mTextList.add(textList.get(i));
        }
        invalidate();
    }

    public void setRadarText(String[] texts){
        mTextList.clear();
        //只取前六个
        for(int i=0; i<POINT_COUNT; ++i){
            mTextList.add(texts[i]);
        }
        invalidate();
    }
}
