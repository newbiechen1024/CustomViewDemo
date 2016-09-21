package com.newbiechen.devinshineviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity{
    private DevinShineView mDevinShineView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDevinShineView = (DevinShineView) findViewById(R.id.main_devin_shine);
    }

    public void startView(View view){
        mDevinShineView.startShow(true);
    }
}
