package com.newbiechen.leaflodingviewdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;

/**
 * Created by PC on 2016/9/16.
 */
public class LeafLoadingView extends View {
    private static final String TAG = "LeafLoadingView";
    // 淡白色
    private static final int WHITE_COLOR = 0xfffde399;
    // 橙色
    private static final int ORANGE_COLOR = 0xffffa800;

    // 用于控制绘制的进度条距离左／上／下的距离
    private static final int LEFT_MARGIN = 13;
    // 用于控制绘制的进度条距离右的距离
    private static final int RIGHT_MARGIN = 25;

    // 中等振幅大小
    private static final int MIDDLE_AMPLITUDE = 13;
    // 不同类型之间的振幅差距
    private static final int AMPLITUDE_DISPARITY = 5;
    //总进度
    private static final int TOTAL_PROGRESS = 100;
    // 叶子飘动一个周期所花的时间
    private static final long LEAF_FLOAT_TIME = 3000;
    // 叶子旋转一周需要的时间
    private static final long LEAF_ROTATE_TIME = 2000;
    //创建叶子的容器
    private final List<Leaf> mLeafList = new ArrayList<>();

    private final LeafFactory mLeafFactory = new LeafFactory();
    private final Paint mProgressPaint = new Paint();
    private final Paint mBgPaint = new Paint();
    private Bitmap mBitmapBackground;
    private Bitmap mBitmapLeaf;
    private Context mContext;
    //整个View的大小
    private int mViewWidth;
    private int mViewHeight;
    //图片的总尺寸
    private int mBitmapBgWidth;
    private int mBitmapBgHeight;
    //半圆的半径
    private int mBgRadius;
    //当前进度
    private int mCurrentProgress;
    //进度条的尺寸
    private int mProgressWidth;
    private int mProgressHeight;
    //叶子图片的大小
    private int mLeafWidth;
    private int mLeafHeight;

    // 中等振幅大小
    private int mMiddleAmplitude = MIDDLE_AMPLITUDE;
    // 振幅差
    private int mAmplitudeDisparity = AMPLITUDE_DISPARITY;
    //叶子飘动用的一个周期
    private long mLeafFloatTime = LEAF_FLOAT_TIME;
    //叶子旋转一圈用的时间
    private long mLeafRotateTime = LEAF_ROTATE_TIME;

   /* private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mCurrentProgress < 100){
                invalidate();
                sendEmptyMessageDelayed(0,200);
            }
        }
    };*/

    public LeafLoadingView(Context context) {
        this(context,null);
    }

    public LeafLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LeafLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initWidget();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
    }

    private void initWidget(){
        //初始化加载进度条的画笔
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.FILL);
        mProgressPaint.setColor(ORANGE_COLOR);
        //初始化设置进度条背景的画笔
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(WHITE_COLOR);

        initBitmap();
        mLeafList.addAll(mLeafFactory.generateLeaves());
    }

    /**
     * 初始化背景框
     */
    private void initBitmap(){
        mBitmapBackground = BitmapFactory.decodeResource(getResources(),R.mipmap.leaf_kuang);
        //边框的总高度和宽度
        mBitmapBgWidth = mBitmapBackground.getWidth();
        mBitmapBgHeight = mBitmapBackground.getHeight();
        //内部半圆的半径
        mBgRadius = (mBitmapBgHeight -2 * LEFT_MARGIN)/2;
        //内部区域的总宽度
        mProgressWidth = mBitmapBgWidth - LEFT_MARGIN - RIGHT_MARGIN;
        mProgressHeight = mBitmapBgHeight - LEFT_MARGIN * 2;

        //设置叶子的图片
        mBitmapLeaf = BitmapFactory.decodeResource(getResources(),R.mipmap.leaf);
        mLeafWidth = mBitmapLeaf.getWidth();
        mLeafHeight = mBitmapLeaf.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将画布移到绘制中心
        int px = (mViewWidth - mBitmapBgWidth)/2;
        int py = (mViewHeight - mBitmapBgHeight)/2;
        canvas.translate(px,py);
        /**开始进度条*/
        drawProgress(canvas);
        /****开始绘制背景**********/
        drawBackground(canvas);

        //当完成时停止重绘
        if (mCurrentProgress > 0 && mCurrentProgress <= 100){
            postInvalidate();
        }

    }

    /**
     * 绘制进度条的全部效果
     * @param canvas
     */
    private void drawProgress(Canvas canvas){
        //首先移动到绘制区（移动到边框内）,根据叠加性
        canvas.translate(LEFT_MARGIN,LEFT_MARGIN);
        //获取当前进度的百分比
        float percent = (float) mCurrentProgress/ TOTAL_PROGRESS;

        //获取当前进度的宽度
        float currentWidth = mProgressWidth * percent;
        //获取剩余半径的宽度
        float remainWidth = mBgRadius - currentWidth;
        //设置绘制范围
        RectF rectF = new RectF(0,0,mBgRadius*2,mBgRadius*2);

        canvas.drawArc(rectF,90,180,false, mBgPaint);
        canvas.drawRect(mBgRadius, 0, (mProgressWidth - mBgRadius), mProgressHeight, mBgPaint);

        /*********绘制叶子***********/
        drawLeaves(canvas);
        //是否达到了半径
        if (remainWidth >= 0){
            //绘制半圆
            drawSemiCircleProgress(canvas,rectF,remainWidth);
        }
        else {
            //绘制矩形
            drawRectProgress(canvas,rectF,currentWidth);
        }
    }

    /**
     * 绘制进度条的半圆区域
     * @param canvas       画布
     * @param rectF        选择绘制区域
     * @param remainWidth  需要绘制的区域的长度
     */
    private void drawSemiCircleProgress(Canvas canvas,RectF rectF,float remainWidth){
        //切换成角度
        int angle = (int) Math.toDegrees(Math.acos(remainWidth/mBgRadius));
        int startAngle = 180 - angle;
        int sweepAngle = 2 * angle;

        canvas.drawArc(rectF,startAngle,sweepAngle,false, mProgressPaint);
    }

    /**
     * 绘制半圆+矩形区域
     * @param canvas          画布
     * @param rectF           绘制的区域
     * @param currentWidth    当前进度的宽度
     */
    private void drawRectProgress(Canvas canvas,RectF rectF,float currentWidth) {
        canvas.drawArc(rectF, 90, 180, true, mProgressPaint);
        if (currentWidth <= mProgressWidth) {
            /**开始绘制矩形*/
            RectF rectArea = new RectF(mBgRadius, 0, (currentWidth - mBgRadius), mProgressHeight);
            canvas.drawRect(rectArea, mProgressPaint);
        } else {
            canvas.drawRect(mBgRadius, 0, (mProgressWidth - mBgRadius), mProgressHeight, mProgressPaint);
        }
    }

    private void drawLeaves(Canvas canvas){
        Iterator<Leaf> leafIterator = mLeafList.iterator();
        //首先获取叶子，并判断是否能够绘制
        long currentSysTime = System.currentTimeMillis();
        //开始绘制
        while(leafIterator.hasNext()){
            //获取叶子
            Leaf leaf = leafIterator.next();
            //判断是否绘制
            if (currentSysTime > leaf.startTime && leaf.startTime != 0){
                //计算Leaf的位置信息
                setUpLeftLocation(leaf,currentSysTime);
                canvas.save();
                //设置旋转角度
                int rotateAngle = getRotateAngle(leaf,currentSysTime);
                //开始旋转
                Matrix matrix = new Matrix();
                matrix.postTranslate(leaf.x,leaf.y);
                matrix.postRotate(rotateAngle,leaf.x+mLeafWidth/2,leaf.y+mLeafHeight/2);
                canvas.drawBitmap(mBitmapLeaf,matrix,null);
                canvas.restore();
            }
        }
    }

    /**
     * 设置叶子的位置信息
     */
    private void setUpLeftLocation(Leaf leaf,long currentTime){
        long intervalTime = currentTime - leaf.startTime;
        /*****判断是否过期，如果过期了重新设定时间*/
        mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
        if (intervalTime < 0) {
            return;
        } else if (intervalTime > mLeafFloatTime) {
            leaf.startTime = System.currentTimeMillis()
                    + new Random().nextInt((int) mLeafFloatTime);
        }
        float fraction = (float) intervalTime / mLeafFloatTime;
        //X的位置
        leaf.x = Math.max(0,mProgressWidth - mProgressWidth*fraction - mLeafWidth);
        //y的位置
        leaf.y = getLeftY(leaf);
    }

    /**
     * 设置叶子的高度
     */
    private float getLeftY(Leaf leaf){
        //首先设置振幅
        int amplitude = 0;
        switch (leaf.startType){
            case LITTLE:
                amplitude = mMiddleAmplitude - mAmplitudeDisparity;
                break;
            case MIDDLE:
                amplitude = mMiddleAmplitude;
                break;
            case HIGH:
                amplitude = mMiddleAmplitude + mAmplitudeDisparity;
                break;
        }
        //设置w参数
        double w = 2 * Math.PI / mProgressWidth;
        //根据公式y = A*Sin(w*x + α)+h 获取Y值
        return (float) (amplitude * Math.sin(w * leaf.x)+mBgRadius);
    }

    /**
     * 设置叶子的旋转角度
     * */
    private int getRotateAngle(Leaf leaf, long currentTime){
        long intervalTime = currentTime - leaf.startTime;
        //根据旋转的百分比设置叶子的旋转角度
        float fraction = (float) intervalTime/mLeafRotateTime;
        int angle = (int) (360 * fraction);
        //设置旋转的大小
        int rotateAngle = leaf.rotateDirection == 0 ?
                                    leaf.rotateAngle + angle : leaf.rotateAngle - angle;
        return rotateAngle;
    }

    //设置背景边框
    private void drawBackground(Canvas canvas){
        //将从内部绘制区域，转移到中心点
        canvas.translate(-LEFT_MARGIN,-LEFT_MARGIN);
        //绘制背景框
        canvas.drawBitmap(mBitmapBackground,0,0,null);
    }
    /**
     * 叶子类
     * */
    public class Leaf {
        //绘制的位置
        float x,y;
        //振幅
        StartType startType;
        //旋转的角度
        int rotateAngle;
        //旋转的方向
        int rotateDirection;
        //开始的时间
        long startTime;

    }

    //绘制的幅度
    public enum StartType{
        LITTLE,MIDDLE,HIGH
    }

    /**
     * 叶子工厂
     * */
    public class LeafFactory{
        //最大数其实是可以控制的
        private int MAX_LEAFS = 6;
        private final Random random = new Random();

        /**************创建叶子的工厂方法*****************/
        public Leaf generateLeaf(){
            long currentSysTime = System.currentTimeMillis();
            Leaf leaf = new Leaf();
            /**首先设置振幅类型*/
            //获取枚举的数量
            StartType [] startTypes = StartType.values();
            //随机设置振幅
            leaf.startType = startTypes[random.nextInt(startTypes.length)];
            //设置旋转方向
            leaf.rotateDirection = random.nextInt(2);
            //设置旋转角度
            leaf.rotateAngle = random.nextInt(360);
            //设置叶子的展示时间
            int showTime = random.nextInt((int)(LEAF_FLOAT_TIME * 1.5));
            leaf.startTime = (currentSysTime + showTime);
            return leaf;
        }

        public List<Leaf> generateLeaves(int count){
            List<Leaf> leafList = new ArrayList<>();
            for(int i=0; i<count; ++i){
                leafList.add(generateLeaf());
            }
            return leafList;
        }

        public List<Leaf> generateLeaves(){
            return generateLeaves(MAX_LEAFS);
        }
    }
    /***************************公共方法*****************************************/

    /**
     * 设置当前的进度
     * @param currentProgress
     */
    public void setProgress(int currentProgress){
        if (currentProgress > 100){
            mCurrentProgress = 100;
        }
        else {
            mCurrentProgress = currentProgress;
            postInvalidate();
        }
    }

    public void setLeafFloatTime(long leafFloatTime){
        mLeafFloatTime = leafFloatTime;
    }

    public void setLeafRotateTime(long leafRotateTime){
        mLeafRotateTime = leafRotateTime;
    }

    public void setMiddleAmplitude(int middleAmplitude){
        mMiddleAmplitude = middleAmplitude;
    }

    public void setAmplitudeDisparity(int amplitudeDisparity){
        mAmplitudeDisparity = amplitudeDisparity;
    }
}
