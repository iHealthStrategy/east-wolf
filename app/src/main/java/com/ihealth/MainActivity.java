/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.ihealth;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ihealth.activities.DetectActivity;
import com.ihealth.activities.LoginActivity;
import com.ihealth.facecheckinapp.R;
import com.ihealth.retrofit.Constants;
import com.ihealth.utils.SharedPreferenceUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext;
    private ImageView ivMainHospitalLogo;
    private TextView tvMainHospitalDepartmentName;

    private Button btnMainFacialCheckIn;
    private Button btnMain2;
    private Button btnMainLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        initView();
        initData();
        addListener();
    }


    private void initView() {
        ivMainHospitalLogo = (ImageView) findViewById(R.id.iv_main_hospital_logo);
        tvMainHospitalDepartmentName = (TextView) findViewById(R.id.tv_main_hospital_department_name);

        btnMainFacialCheckIn = (Button) findViewById(R.id.btn_main_facial_check_in);
        btnMain2 = (Button) findViewById(R.id.btn_main_2);
        btnMainLogout = (Button) findViewById(R.id.btn_main_log_out);
    }

    private void initData(){
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("login_bundle");
        if (null != bundle){
            String logoUrl = bundle.getString("login_info_hospitalLogoImage");
            if (!TextUtils.isEmpty(logoUrl)){
                Glide.with(mContext).load(logoUrl).into(ivMainHospitalLogo);
            }
            String hospitalDepartmentName = "内分泌科";
            tvMainHospitalDepartmentName.setText(hospitalDepartmentName);
        } else {
            String logoUrl = SharedPreferenceUtil.getStringTypeSharedPreference(mContext, Constants.SP_NAME_HOSPITAL_INFOS, Constants.SP_KEY_HOSPITAL_LOGO_URL);
            if (!TextUtils.isEmpty(logoUrl)){
                Glide.with(mContext).load(logoUrl).into(ivMainHospitalLogo);
            }
            String hospitalDepartmentName = "内分泌科";
            tvMainHospitalDepartmentName.setText(hospitalDepartmentName);
        }
    }

    private void addListener() {
        btnMainFacialCheckIn.setOnClickListener(this);
        btnMain2.setOnClickListener(this);
        btnMainLogout.setOnClickListener(this);
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
//                Intent itTrack = new Intent(MainActivity.this, TrackActivity.class);
//                startActivity(itTrack);
                break;
            case R.id.btn_main_log_out:
                SharedPreferenceUtil.clearSharedPreference(mContext, Constants.SP_NAME_AUTHORIZATION);
                SharedPreferenceUtil.clearSharedPreference(mContext, Constants.SP_NAME_HOSPITAL_INFOS);
                Intent itLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(itLogin);
                this.finish();
                break;
            default:
                break;
        }

    }
}
