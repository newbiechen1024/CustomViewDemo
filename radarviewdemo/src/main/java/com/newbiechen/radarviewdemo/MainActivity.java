package com.newbiechen.radarviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    String[] texts = {"a","b","c","d","e","f"};
    int [] scores = {30,40,50,60,70,80};

    private RadarView mRadarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRadarView = (RadarView) findViewById(R.id.main_radar_view);
        mRadarView.setRadarScore(scores);
        mRadarView.setRadarText(texts);
    }
}
