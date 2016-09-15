package com.newbiechen.pieviewdemo;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private PieView mPieView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPieView = (PieView) findViewById(R.id.main_pieView_percent);
        List<Number> numbers = new ArrayList<>();
        numbers.add(20);
        numbers.add(30);
        numbers.add(50);
        mPieView.addDatas(numbers);
    }
}
