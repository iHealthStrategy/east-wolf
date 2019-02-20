/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.ihealth;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;

import com.ihealth.activities.DetectActivity;
import com.ihealth.activities.RegisterActivity;
import com.ihealth.activities.TrackActivity;
import com.ihealth.facecheckinapp.R;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Button btnMainFacialCheckIn;
    private Button btnMain2;
    private Button btnMain3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        addListener();
    }

    private void initView() {
        btnMainFacialCheckIn = (Button) findViewById(R.id.btn_main_facial_check_in);
        btnMain2 = (Button) findViewById(R.id.btn_main_2);
        btnMain3 = (Button) findViewById(R.id.btn_main_3);
    }

    private void addListener() {
        btnMainFacialCheckIn.setOnClickListener(this);
        btnMain2.setOnClickListener(this);
        btnMain3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            return;
        }

        switch (v.getId()) {
            case R.id.btn_main_facial_check_in:
                // TODO 实时人脸检测
                Intent itDetect = new Intent(MainActivity.this, DetectActivity.class);
                startActivity(itDetect);
                break;
            case R.id.btn_main_2:
                Intent itTrack = new Intent(MainActivity.this, TrackActivity.class);
                startActivity(itTrack);
                break;
            case R.id.btn_main_3:
                Intent itAttr = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(itAttr);
                break;
            default:
                break;
        }

    }
}
