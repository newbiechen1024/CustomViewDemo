package com.newbiechen.leaflodingviewdemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private LeafLoadingView mLeafProgress;
    private SeekBar mSbAmplitudeDisparity;
    private int mCurrentProgress = 0;
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mCurrentProgress < 40) {
                mCurrentProgress += 3;
                // 随机800ms以内刷新一次
                mHandler.sendEmptyMessageDelayed(0,
                        new Random().nextInt(800));
                mLeafProgress.setProgress(mCurrentProgress);
            } else if(mCurrentProgress <= 100){
                mCurrentProgress += 3;
                // 随机1200ms以内刷新一次
                mHandler.sendEmptyMessageDelayed(0,
                        new Random().nextInt(1200));
                mLeafProgress.setProgress(mCurrentProgress);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLeafProgress = (LeafLoadingView) findViewById(R.id.main_leaf_progress);
        mSbAmplitudeDisparity = (SeekBar) findViewById(R.id.main_sb_amplitude_disparity);
        mSbAmplitudeDisparity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mLeafProgress.setAmplitudeDisparity(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mHandler.sendEmptyMessageDelayed(0,500);
    }
}
