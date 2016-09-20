package com.newbiechen.bazierloveviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {
    private BazierLoveView mBazierLoveView;
    private Button mBtnRestart;
    private RadioGroup mRgSelect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBazierLoveView = (BazierLoveView) findViewById(R.id.main_bazierLove);
        mRgSelect = (RadioGroup) findViewById(R.id.main_rg_select);
        mBtnRestart = (Button) findViewById(R.id.main_btn_restart);
        mBtnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重绘
                mBazierLoveView.restart();
                //显示点和线
                RadioButton view = (RadioButton) mRgSelect.getChildAt(0);
                view.setChecked(true);
            }
        });
        mRgSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.main_rb_select_true:
                        mBazierLoveView.isDrawPointAndLine(true);
                        break;
                    case R.id.main_rb_select_false:
                        mBazierLoveView.isDrawPointAndLine(false);
                        break;
                }
            }
        });
    }
}
