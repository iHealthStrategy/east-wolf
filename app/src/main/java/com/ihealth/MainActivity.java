/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.ihealth;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ihealth.activities.BGManualActivity;
import com.ihealth.activities.BGMeasureActivity;
import com.ihealth.activities.DetectActivity;
import com.ihealth.activities.LoginActivity;
import com.ihealth.activities.MeasureResultActivity;
import com.ihealth.communication.control.Bg1Profile;
import com.ihealth.facecheckin.R;
import com.ihealth.retrofit.Constants;
import com.ihealth.utils.DateUtils;
import com.ihealth.utils.SharedPreferenceUtil;
import com.ihealth.views.SameCircleView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.ihealth.communication.control.Bg1Control;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;

public class MainActivity extends BaseActivity  {

    @BindView(R.id.common_header_title)
    TextView commonHeaderTitle;
    @BindView(R.id.common_header_back_layout)
    LinearLayout commonHeaderBackLayout;
    @BindView(R.id.activity_main_time)
    TextView activityMainTime;
    @BindView(R.id.activity_main_date)
    TextView activityMainDate;
    @BindView(R.id.activity_main_start_face_dected)
    SameCircleView activityMainStartFaceDected;
    @BindView(R.id.activity_main_sign_time)
    TextView activityMainSignTime;
    private Context mContext;
    private ImageView ivMainHospitalLogo;
    private Button btnMainFacialCheckIn;
    private LinearLayout close_ll;

    private static final String TAG = "BG1";
    public Bg1Control mBg1Control;

    private boolean isGetStripInBg1 = false;
    private boolean isGetResultBg1 = false;
    private boolean isGetBloodBg1 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;
        initView();
        initData();
        setDateWithTimer();
    }

    private void initView() {
        ivMainHospitalLogo = (ImageView) findViewById(R.id.iv_main_hospital_logo);
        close_ll = (LinearLayout) findViewById(R.id.close_ll);
        close_ll.setVisibility(ImageView.GONE);
        commonHeaderBackLayout.setVisibility(ImageView.GONE);
        btnMainFacialCheckIn = (Button) findViewById(R.id.btn_main_facial_check_in);
        Date temp = DateUtils.getCurrentSystemDate();
        commonHeaderTitle.setText("共同照护内分泌全科室人脸签到");
        activityMainTime.setText(DateUtils.getFormatDateStringByFormat(temp,DateUtils.FORMAT_HH_MM));
        activityMainDate.setText(DateUtils.getFormatDateStringByFormat(temp, DateUtils.FORMAT_YYYYCMMCDD)+" "+DateUtils.getWeekOfDate(temp));

//        registerBroadcast();
        Intent intent = getIntent();
//        String userName = intent.getExtras().getString("userName");
        mDeviceMac = intent.getStringExtra("mac");
        mDeviceName = intent.getStringExtra("type");
        mBg1Control = Bg1Control.getInstance();
        mBg1Control.init(this, "", 0x00FF1304, true);
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("login_bundle");
        if (null != bundle) {
            String logoUrl = bundle.getString("login_info_hospitalLogoImage");
            if (!TextUtils.isEmpty(logoUrl)) {
                Glide.with(mContext).load(logoUrl).into(ivMainHospitalLogo);
            }
        } else {
            String logoUrl = SharedPreferenceUtil.getStringTypeSharedPreference(mContext, Constants.SP_NAME_HOSPITAL_INFOS, Constants.SP_KEY_HOSPITAL_LOGO_URL);
            if (!TextUtils.isEmpty(logoUrl)) {
                Glide.with(mContext).load(logoUrl).into(ivMainHospitalLogo);
            }
        }
    }


    private void setDateWithTimer(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                Date temp = DateUtils.getCurrentSystemDate();
//                activityMainTime.setText(DateUtils.getFormatDateStringByFormat(temp,DateUtils.FORMAT_HH_MM));
//                activityMainDate.setText(DateUtils.getFormatDateStringByFormat(temp, DateUtils.FORMAT_YYYYCMMCDD)+" "+DateUtils.getWeekOfDate(temp));
            }
        }, 0 , 10000);
    }

//    private void addListener() {
//        btnMainFacialCheckIn.setOnClickListener(this);
//    }

//    @Override
//    public void onClick(View v) {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
//        } else {
//            switch (v.getId()) {
//                case R.id.btn_main_facial_check_in:
//                    // TODO 实时人脸检测
//                    Intent itDetect = new Intent(MainActivity.this, DetectActivity.class);
//                    startActivity(itDetect);
//                    break;
//                default:
//                    break;
//            }
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                switch (permissions[0]) {
                    case Manifest.permission.CAMERA:
                        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            Intent itDetect = new Intent(MainActivity.this, DetectActivity.class);
                            startActivity(itDetect);
                        } else {
                            Toast.makeText(this, "您需要授予拍照权限才可使用人脸签到APP，谢谢！", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    default:
                        break;
                }
                break;
            default:
                break;
        }

    }

    /**
     * 展示对话框
     *
     * @param
     */
    private void showCommonDialog() {
        final BaseDialog dialog = new BaseDialog(mContext);
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.fragment_dialog_common, null);

        final ImageView ivCloseDialog = (ImageView) view.findViewById(R.id.iv_dialog_close);
        ivCloseDialog.setVisibility(View.VISIBLE);
        ivCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final TextView tvDialogContent = (TextView) view.findViewById(R.id.tv_common_dialog_content);
        tvDialogContent.setText("确认退出登录吗？");

        final TextView tvDialogOk = (TextView) view.findViewById(R.id.btn_dialog_ok);
        tvDialogOk.setText("是的");
        tvDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SharedPreferenceUtil.clearSharedPreference(mContext, Constants.SP_NAME_AUTHORIZATION);
                SharedPreferenceUtil.clearSharedPreference(mContext, Constants.SP_NAME_HOSPITAL_INFOS);
                Intent itLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(itLogin);
                Activity activity = (Activity) mContext;
                activity.finish();
            }
        });

        final TextView tvDialogCancel = (TextView) view.findViewById(R.id.btn_dialog_cancel);
        tvDialogCancel.setText("取消");
        tvDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.show();
    }

    @OnClick({R.id.common_header_back_layout, R.id.activity_main_start_face_dected})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.common_header_back_layout:
                finish();
                break;
            case R.id.activity_main_start_face_dected:
//                FaceDetectResultDialog dialog1 = new FaceDetectResultDialog(MainActivity.this);
//                dialog1.setData(ConstantArguments.DETECT_RESULT_SUCESS_SIGN_PREPARE_CLINIC);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
                } else {
                    // TODO 实时人脸检测
                    Intent itDetect = new Intent(MainActivity.this, DetectActivity.class);
                    startActivity(itDetect);
                }
        }
    }

}

