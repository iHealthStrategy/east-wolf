/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.ihealth;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
   // private Button btnMain2;
  //  private Button btnMainLogout;

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
       // btnMain2 = (Button) findViewById(R.id.btn_main_2);
       // btnMainLogout = (Button) findViewById(R.id.btn_main_log_out);
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("login_bundle");
        if (null != bundle) {
            String logoUrl = bundle.getString("login_info_hospitalLogoImage");
            if (!TextUtils.isEmpty(logoUrl)) {
                Glide.with(mContext).load(logoUrl).into(ivMainHospitalLogo);
            }
            String hospitalDepartmentName = "内分泌科";
            tvMainHospitalDepartmentName.setText(hospitalDepartmentName);
        } else {
            String logoUrl = SharedPreferenceUtil.getStringTypeSharedPreference(mContext, Constants.SP_NAME_HOSPITAL_INFOS, Constants.SP_KEY_HOSPITAL_LOGO_URL);
            if (!TextUtils.isEmpty(logoUrl)) {
                Glide.with(mContext).load(logoUrl).into(ivMainHospitalLogo);
            }
            String hospitalDepartmentName = "内分泌科";
            tvMainHospitalDepartmentName.setText(hospitalDepartmentName);
        }
    }

    private void addListener() {
        btnMainFacialCheckIn.setOnClickListener(this);
       // btnMain2.setOnClickListener(this);
      //  btnMainLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            switch (v.getId()) {
                case R.id.btn_main_facial_check_in:
                    // TODO 实时人脸检测
                    Intent itDetect = new Intent(MainActivity.this, DetectActivity.class);
                    startActivity(itDetect);
                    break;
               /* case R.id.btn_main_2:
                    Toast.makeText(mContext, "敬请期待!", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_main_log_out:
                    showCommonDialog();
                    break;*/
                default:
                    break;
            }
        }
    }

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

}
