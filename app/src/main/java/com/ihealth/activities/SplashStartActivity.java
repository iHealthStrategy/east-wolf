package com.ihealth.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.ihealth.BaseActivity;
import com.ihealth.MainActivity;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.facecheckin.R;
import com.ihealth.retrofit.Constants;
import com.ihealth.utils.SharedPreferenceUtil;

import java.io.IOException;
import java.io.InputStream;

public class SplashStartActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SplashStartActivity";
    Context mContext;
    FloatingActionButton floatingActionButton;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_start);
        mContext = this;
        initView();
        initData();
        initListeners();
        initTimer();
    }


    private void initView() {
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fabtn_login_next_step);
    }

    private void initData(){
        try {
            //Please apply for authorization on the server and download the. PEM file,
            //then put it in the assets folder and modify the corresponding name to call the following method
            //When ispass replays true, it indicates that the authentication has passed
            InputStream is = mContext.getAssets().open("com_ihealth_facecheckin_android.pem");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            boolean isPass = iHealthDevicesManager.getInstance().sdkAuthWithLicense(buffer);
            Toast.makeText(this,"isPass:" + isPass,Toast.LENGTH_LONG).show();
            if (isPass) {

            } else {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                String token = SharedPreferenceUtil.getStringTypeSharedPreference(mContext, Constants.SP_NAME_AUTHORIZATION, Constants.SP_KEY_TOKEN);
                Intent intent;
                // Log.i(TAG, "onFinish: token = "+token);
                if (TextUtils.isEmpty(token)){
                    intent = new Intent(mContext,LoginActivity.class);
                } else {
                    intent = new Intent(mContext, MainActivity.class);
                }
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
