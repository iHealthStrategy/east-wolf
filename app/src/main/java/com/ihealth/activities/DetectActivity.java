/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.ihealth.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.baidu.aip.face.DetectRegionProcessor;

import com.baidu.aip.face.TexturePreviewView;
import com.ihealth.BaseActivity;
import com.ihealth.Printer.BluetoothPrinter;
import com.ihealth.events.FinshDetectRegisterAndResultEvent;
import com.ihealth.events.FinshDetectRegisterSelectTypeAndResultEvent;
import com.ihealth.events.FinshRegisterAndResultEvent;
import com.ihealth.events.FinshRegisterSelectTypeAndResultEvent;
import com.ihealth.facecheckin.R;
import com.ihealth.utils.FaceDetectExtendManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
