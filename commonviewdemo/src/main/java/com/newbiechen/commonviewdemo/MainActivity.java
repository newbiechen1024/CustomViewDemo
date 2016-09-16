package com.newbiechen.commonviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;

import com.newbiechen.commonviewdemo.view.CheckBoxView;

public class MainActivity extends AppCompatActivity {
    private CheckBoxView mCheckBoxView;
    private Button mBtnUnCheck;
    private Button mBtnCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*        mCheckBoxView = (CheckBoxView) findViewById(R.id.checkbox_view);
        mBtnUnCheck = (Button) findViewById(R.id.uncheck);
        mBtnCheck = (Button) findViewById(R.id.check);
        mBtnUnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckBoxView.unCheck();
            }
        });
        mBtnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckBoxView.check();
            }
        });*/
    }
}
