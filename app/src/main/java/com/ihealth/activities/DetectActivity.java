/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.ihealth.activities;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.aip.ImageFrame;
import com.baidu.aip.face.CameraImageSource;
import com.baidu.aip.face.DetectRegionProcessor;
import com.baidu.aip.face.FaceDetectManager;
import com.baidu.aip.face.FaceFilter;
import com.baidu.aip.face.PreviewView;
import com.baidu.aip.face.TexturePreviewView;
import com.baidu.aip.face.camera.ICameraControl;
import com.baidu.aip.face.camera.PermissionCallback;
import com.baidu.aip.fl.widget.BrightnessTools;
import com.baidu.idl.facesdk.FaceInfo;
import com.google.gson.Gson;
import com.ihealth.BaseActivity;
import com.ihealth.BaseDialog;
import com.ihealth.Printer.PrintAllDepartContentUtils;
import com.ihealth.bean.AppointmentsBean;
import com.ihealth.Printer.BluetoothPrinter;
import com.ihealth.bean.AppointmentsBean;
import com.ihealth.bean.ResponseMessageBean;
import com.ihealth.events.FinshDetectRegisterAndResultEvent;
import com.ihealth.events.FinshDetectRegisterSelectTypeAndResultEvent;
import com.ihealth.events.FinshRegisterAndResultEvent;
import com.ihealth.events.FinshRegisterSelectTypeAndResultEvent;
import com.ihealth.facecheckinapp.R;
import com.ihealth.retrofit.ApiUtil;
import com.ihealth.retrofit.Constants;
import com.ihealth.utils.FaceDetectExtendManager;
import com.ihealth.utils.SharedPreferenceUtil;
import com.ihealth.views.CheckItemSelectDialog;
import com.ihealth.views.PirntAllDepartmentDialog;
import com.ihealth.views.PrintContentDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 实时检测人脸框并把检测到得人脸图片绘制在屏幕上，每10帧截图一张。
 * Intent intent = new Intent(MainActivity.this, DetectActivity.class);
 * startActivity(intent);
 */
public class DetectActivity extends BaseActivity {

    public static final int MSG_INITVIEW = 1001;
    public static final int MSG_REFRESH_TITLE = 1002;

    @BindView(R.id.common_header_title)
    TextView commonHeaderTitle;
    @BindView(R.id.common_header_back_layout)
    LinearLayout commonHeaderBackLayout;
    @BindView(R.id.common_header_layout)
    RelativeLayout commonHeaderLayout;

    @BindView(R.id.activity_detect_preview_view_rl)
    RelativeLayout activityDetectPreviewViewRl;
    @BindView(R.id.back_imageview)
    ImageView backImageview;
    private Context mContext;

    private TextView tvDetectNextSigningTimer;
    private TexturePreviewView previewView;
    private boolean mDetectStopped = false;
    private DetectRegionProcessor cropProcessor = new DetectRegionProcessor();
    private TextureView mTextureView;
     private Handler mHandler = new Handler();
    private BluetoothPrinter printer;
    private FaceDetectExtendManager faceDetectSuperManager;

    @OnClick(R.id.common_header_back_layout)
    public void onViewClicked() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mContext = this;
        initCameraView();
        initView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        previewView = (TexturePreviewView) findViewById(R.id.activity_detect_texture_preview_view);
        mTextureView = (TextureView) findViewById(R.id.activity_detect_texture_view);
        backImageview.setBackground(getResources().getDrawable(R.drawable.back_white_iv));
        commonHeaderTitle.setTextColor(getResources().getColor(R.color.colorFFFFFF));
        commonHeaderTitle.setText("共同照护内分泌全科室人脸识别");
        tvDetectNextSigningTimer = (TextView) findViewById(R.id.tv_detect_next_signing_timer);
        commonHeaderLayout.setBackgroundColor(mContext.getResources().getColor(R.color.color1D1D1D));

        faceDetectSuperManager = new FaceDetectExtendManager(this, previewView, mTextureView, tvDetectNextSigningTimer, mHandler);

    }

    private void initCameraView() {
        mHandler = new InnerHandler(this);
        mHandler.sendEmptyMessageDelayed(MSG_INITVIEW, 200);
    }

    @Override
    protected void onStop() {
        super.onStop();
        faceDetectSuperManager.stop();
        mDetectStopped = true;

    }



    @Override
    protected void onResume() {
        super.onResume();
        // Log.i("onResume", "onResume: mDetectStopped = " + mDetectStopped);
        if (mDetectStopped) {
            mDetectStopped = false;
            faceDetectSuperManager.start();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private class InnerHandler extends Handler {
        private WeakReference<DetectActivity> mWeakReference;

        public InnerHandler(DetectActivity activity) {
            super();
            this.mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mWeakReference == null || mWeakReference.get() == null) {
                return;
            }
            DetectActivity activity = mWeakReference.get();
            if (activity == null) {
                return;
            }
            if (msg == null) {
                return;

            }
            switch (msg.what) {
                case MSG_INITVIEW:
                    faceDetectSuperManager.start();
                    break;
                case MSG_REFRESH_TITLE:
//                    faceDetectSuperManager.setDisplayElements("");
                    break;
                default:
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FinshDetectRegisterAndResultEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FinshDetectRegisterSelectTypeAndResultEvent event) {
        finish();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FinshRegisterAndResultEvent event){
        faceDetectSuperManager.reFaceDetect();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FinshRegisterSelectTypeAndResultEvent event){
        faceDetectSuperManager.reFaceDetect();
    }
}
