package com.ihealth.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.ihealth.BaseActivity;
import com.ihealth.facecheckinapp.R;

public class SplashStartActivity extends BaseActivity implements View.OnClickListener {

    Context mContext;
    FloatingActionButton floatingActionButton;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_start);
        mContext = this;
        initView();
        initListeners();
        initTimer();
    }


    private void initView() {
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fabtn_login_next_step);
    }

    private void initListeners() {
        floatingActionButton.setOnClickListener(this);
    }

    private void initTimer() {
        timer = new CountDownTimer(2000,1000){
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(mContext,LoginActivity.class);
                startActivity(intent);
                Activity activity = (Activity) mContext;
                activity.finish();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null!=timer){
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabtn_login_next_step:
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                this.finish();
                break;
            default:
                break;
        }
    }
}
