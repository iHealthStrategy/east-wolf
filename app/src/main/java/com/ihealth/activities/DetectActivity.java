/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.ihealth.activities;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.aip.ImageFrame;
import com.baidu.aip.face.CameraImageSource;
import com.baidu.aip.face.DetectRegionProcessor;
import com.baidu.aip.face.FaceDetectManager;
import com.baidu.aip.face.FaceFilter;
import com.baidu.aip.face.PreviewView;
import com.baidu.aip.face.camera.ICameraControl;
import com.baidu.aip.face.camera.PermissionCallback;
import com.baidu.aip.fl.widget.BrightnessTools;
import com.baidu.idl.facesdk.FaceInfo;
import com.google.gson.Gson;
import com.ihealth.BaseActivity;
import com.ihealth.BaseDialog;
import com.ihealth.bean.ResponseMessageBean;
import com.ihealth.facecheckinapp.R;
import com.ihealth.retrofit.ApiUtil;
import com.ihealth.retrofit.Constants;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final int MSG_INITVIEW = 1001;
    private static final int MSG_REFRESH_TITLE = 1002;
    private Context mContext;

    private TextView tvDetectResultTitle;
    private TextView tvDetectResultName;
    private TextView tvDetectResultMobile;
    private TextView tvDetectResultIdCard;

    private Button btnDetectContinueSigning;
    private TextView tvDetectNextSigningTimer;

    private PreviewView previewView;
    private ImageView closeIv;
    private boolean mDetectStopped = false;

    private FaceDetectManager faceDetectManager;
    private DetectRegionProcessor cropProcessor = new DetectRegionProcessor();
    private int mScreenW;
    private int mScreenH;
    private TextureView mTextureView;
    private Paint paint = new Paint();
    // private RecyclerView mRecyclerview;
    private List<Bitmap> mList = new ArrayList<>();
    // private RecyAdapter mRecyAdapter;
    private Handler mHandler = new Handler();
    // private LinearLayoutManager mLayoutManager;

    private int mFrameIndex = 0;
    private int mRound = 2;

    private CountDownTimer timer;

    private int mSearchFailTimes = 0;

    private DETECT_STATES detectStates;

    private enum DETECT_STATES {
        /**
         * 等待签到
         */
        WAITING_FOR_SIGNING,
        /**
         * 正在签到
         */
        SIGNING,
        /**
         * 签到失败：人脸识别失败-用户未找到（错误code: 1001）
         */
        SIGN_FAILED_USER_NOT_FOUND,
        /**
         * 签到失败：人脸识别失败-用户找到不匹配（错误code: 1002）
         */
        SIGN_FAILED_USER_NOT_MATCH,
        /**
         * 签到失败：人脸识别失败-其他错误（错误code: 1003）
         */
        SIGN_FAILED_OTHER_REASONS,
        /**
         * 签到成功
         */
        SIGN_SUCCEEDED,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        mContext = this;
        faceDetectManager = new FaceDetectManager(this);
        initScreen();
        initView();
        mHandler = new InnerHandler(this);
        mHandler.sendEmptyMessageDelayed(MSG_INITVIEW, 200);
        // initRecy();
    }

    /**
     * 获取屏幕参数
     */
    private void initScreen() {
        WindowManager manager = getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        mScreenW = outMetrics.widthPixels;
        mScreenH = outMetrics.heightPixels;
        mRound = getResources().getDimensionPixelSize(R.dimen.round);
    }

    /**
     * 初始化view
     */
    private void initView() {
        previewView = (PreviewView) findViewById(R.id.preview_view);
        mTextureView = (TextureView) findViewById(R.id.texture_view);

        tvDetectResultTitle = (TextView) findViewById(R.id.tv_detect_title);
        tvDetectResultName = (TextView) findViewById(R.id.tv_detect_result_name_content);
        tvDetectResultMobile = (TextView) findViewById(R.id.tv_detect_result_mobile_content);
        tvDetectResultIdCard = (TextView) findViewById(R.id.tv_detect_result_id_card_content);

        btnDetectContinueSigning = (Button) findViewById(R.id.btn_detect_continue_signing);
        tvDetectNextSigningTimer = (TextView) findViewById(R.id.tv_detect_next_signing_timer);

        btnDetectContinueSigning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDetectNextSigningTimer.setText("<< 等待签到 >>");
                timer.cancel();
                mList.clear();
                resetDisplayContents();
            }
        });

        mTextureView.setOpaque(false);
        // mRecyclerview = (RecyclerView) findViewById(R.id.recyclerview);

        // 不需要屏幕自动变黑。
        mTextureView.setKeepScreenOn(true);

        final CameraImageSource cameraImageSource = new CameraImageSource(this);
        cameraImageSource.setPreviewView(previewView);

        faceDetectManager.setImageSource(cameraImageSource);
        faceDetectManager.setOnFaceDetectListener(new FaceDetectManager.OnFaceDetectListener() {
            @Override
            public void onDetectFace(final int retCode, FaceInfo[] infos, ImageFrame frame) {
                // Log.i("onDetectFace", "onDetectFace: retCode = "+ retCode+",infos = "+infos);
                if (
                        detectStates != DETECT_STATES.SIGN_SUCCEEDED
                                && detectStates != DETECT_STATES.SIGN_FAILED_USER_NOT_MATCH
                                && detectStates != DETECT_STATES.SIGN_FAILED_USER_NOT_FOUND
                                && detectStates != DETECT_STATES.SIGN_FAILED_OTHER_REASONS
                ) {
                    if (retCode == 0) {
                        detectStates = DETECT_STATES.SIGNING;
                        mHandler.sendEmptyMessageDelayed(MSG_REFRESH_TITLE, 200);
                    } else {
                        detectStates = DETECT_STATES.WAITING_FOR_SIGNING;
                        mHandler.sendEmptyMessageDelayed(MSG_REFRESH_TITLE, 200);
                    }
                }
            }
        });
        faceDetectManager.setOnTrackListener(new FaceFilter.OnTrackListener() {
            @Override
            public void onTrack(final FaceFilter.TrackedModel trackedModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showFrame(trackedModel);
                    }
                });

            }
        });

        cameraImageSource.getCameraControl().setPermissionCallback(new PermissionCallback() {
            @Override
            public boolean onRequestPermission() {
                ActivityCompat.requestPermissions(DetectActivity.this,
                        new String[]{Manifest.permission.CAMERA}, 100);
                return true;
            }
        });


        ICameraControl control = cameraImageSource.getCameraControl();
        control.setPreviewView(previewView);
        // 设置检测裁剪处理器
        faceDetectManager.addPreProcessor(cropProcessor);

        int orientation = getResources().getConfiguration().orientation;
        boolean isPortrait = (orientation == Configuration.ORIENTATION_PORTRAIT);

        if (isPortrait) {
            previewView.setScaleType(PreviewView.ScaleType.FIT_WIDTH);
        } else {
            previewView.setScaleType(PreviewView.ScaleType.FIT_HEIGHT);
        }

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        cameraImageSource.getCameraControl().setDisplayOrientation(rotation);
        setCameraType(cameraImageSource);
        closeIv = (ImageView) findViewById(R.id.closeIv);
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initBrightness();
        initPaint();
    }

    /**
     * 设置相机亮度，不够200自动调整亮度到200
     */
    private void initBrightness() {
        int brightness = BrightnessTools.getScreenBrightness(DetectActivity.this);
        if (brightness < 200) {
            BrightnessTools.setBrightness(this, 200);
        }
    }

    /**
     * 启动人脸检测
     */
    private void start() {
        RectF newDetectedRect = new RectF(0, 0, mScreenW, mScreenH);
        cropProcessor.setDetectedRect(newDetectedRect);
        faceDetectManager.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        faceDetectManager.stop();
        mDetectStopped = true;
        int size = mList.size();
        for (int i = 0; i < size; i++) {
            Bitmap bmp = mList.get(i);
            if (bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
            }
        }
        mList.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDetectStopped) {
            faceDetectManager.start();
            mDetectStopped = false;
        }
        mHandler.postDelayed(searchFaceRunnable, 10);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
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
                    activity.start();
                    break;
                case MSG_REFRESH_TITLE:
                    setDisplayElements();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
    }

    /**
     * 绘制人脸框。
     *
     * @param model 追踪到的人脸
     */
    private void showFrame(FaceFilter.TrackedModel model) {
        Canvas canvas = mTextureView.lockCanvas();
        if (canvas == null) {
            return;
        }
        // 清空canvas
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (model != null) {
            FaceInfo info = model.getInfo();
            model.getImageFrame().retain();
            RectF rectCenter = new RectF(info.mCenter_x - 2 - info.mWidth * 1 / 2,
                    info.mCenter_y - 2 - info.mWidth * 1 / 2,
                    info.mCenter_x + 2 + info.mWidth * 1 / 2,
                    info.mCenter_y + 2 + info.mWidth * 1 / 2);
            previewView.mapFromOriginalRect(rectCenter);
            // 绘制框
            paint.setStrokeWidth(mRound);
            paint.setAntiAlias(true);
            canvas.drawRect(rectCenter, paint);

            if (model.meetCriteria()) {
                // 符合检测要求，绘制绿框
                paint.setColor(Color.GREEN);
            }
            final Bitmap face = model.cropFace();
            if (face != null) {
                mList.add(face);
                if (mList.size() == 5) {
                    mHandler.postDelayed(searchFaceRunnable, 100);
                }
            }
        }
        mTextureView.unlockCanvasAndPost(canvas);
    }

    Runnable searchFaceRunnable = new Runnable() {
        @Override
        public void run() {
            if (mList.size() > 0) {
                Bitmap uploadedFace = mList.get(mList.size() - 1);
                final String base64Image = convertImageToBase64String(uploadedFace);

                Map<String, String> requestMap = new HashMap<>();

                requestMap.put("base64Image", base64Image);
                requestMap.put("hospitalId", "chaoyang");

                Gson gson = new Gson();
                String jsonStr = gson.toJson(requestMap, HashMap.class);

                final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonStr);

                ApiUtil.searchFaceCall(mContext, requestBody).enqueue(new Callback<ResponseMessageBean>() {
                    @Override
                    public void onResponse(Call<ResponseMessageBean> call, Response<ResponseMessageBean> response) {
                        Log.i("searchFaceRunnable", "onResponse: response = " + response.body());
                        ResponseMessageBean responseMessage = response.body();
                        if (responseMessage != null) {
                            switch (responseMessage.getResultStatus()) {
                                case Constants.FACE_RESPONSE_CODE_SUCCESS:

                                    detectStates = DETECT_STATES.SIGN_SUCCEEDED;
                                    setDisplayElements();

                                    ResponseMessageBean.resultContent resultContent = responseMessage.getResultContent();
                                    String name = resultContent.getNickname();
                                    String originMobile = resultContent.getPhoneNumber();
                                    String mobile = originMobile.substring(0, 3) + "****" + originMobile.substring(7, 11);
                                    tvDetectResultName.setText(name);
                                    tvDetectResultMobile.setText(mobile);
                                    String originIdCard = resultContent.getIdCard();
                                    if (!originIdCard.isEmpty()) {
                                        String idCard = originIdCard.substring(0, 6) + "********" + originIdCard.substring(originIdCard.length() - 4);
                                        tvDetectResultIdCard.setText(idCard);
                                    } else {
                                        tvDetectResultIdCard.setText("--");
                                    }
                                    startCountDownTimer();
                                    break;
                                case Constants.FACE_RESPONSE_CODE_ERROR_SEARCH_USER_NOT_FOUND:
                                    detectStates = DETECT_STATES.SIGN_FAILED_USER_NOT_FOUND;
                                    setDisplayElements();

                                    startRegisterActivity(base64Image);
                                    resetDisplayContents();
                                    break;
                                case Constants.FACE_RESPONSE_CODE_ERROR_SEARCH_USER_FOUND_NOT_MATCH:
                                    detectStates = DETECT_STATES.SIGN_FAILED_USER_NOT_MATCH;
                                    setDisplayElements();

                                    mSearchFailTimes++;
                                    startCountDownTimer();
                                    if (mSearchFailTimes == 3) {
                                        startRegisterActivity(base64Image);
                                        mSearchFailTimes = 0;
                                    }
                                    break;
                                case Constants.FACE_RESPONSE_CODE_ERROR_ADD_USER_OTHER_ERRORS:
                                    detectStates = DETECT_STATES.SIGN_FAILED_OTHER_REASONS;
                                    setDisplayElements();

                                    startCountDownTimer();
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            showMessageDialog("系统认证失败，请重新登录。");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseMessageBean> call, Throwable t) {
                        Log.i("searchFaceRunnable", "onFailure: t = " + t);
                    }
                });

            }

        }
    };

    private static String convertImageToBase64String(Bitmap imageBitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (null != imageBitmap) {
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            byte[] byteArray = stream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } else {
            return null;
        }
    }

    private void startCountDownTimer() {
        timer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvDetectNextSigningTimer.setText("<< " + (millisUntilFinished / 1000 + 1) + " 秒后继续签到 >>");
            }

            @Override
            public void onFinish() {
                tvDetectNextSigningTimer.setText("<< 等待签到 >>");
                timer.cancel();
                mList.clear();
                resetDisplayContents();
            }
        }.start();
    }

    private void resetDisplayContents() {
        detectStates = DETECT_STATES.WAITING_FOR_SIGNING;
        setDisplayElements();

        tvDetectResultIdCard.setText("--");
        tvDetectResultName.setText("--");
        tvDetectResultMobile.setText("--");
    }

//    /**
//     * 展示对话框
//     *
//     * @param
//     */
//    private void showRegisteredResultDialog(String dialogContent) {
//        final BaseDialog dialogRegisteredSucceeded = new BaseDialog(mContext);
//        View view;
//        view = LayoutInflater.from(mContext).inflate(R.layout.fragment_dialog_register_success, null);
//
//        final TextView tvDialogContent = (TextView) view.findViewById(R.id.tv_dialog_content);
//        tvDialogContent.setText(dialogContent);
//
//        (view.findViewById(R.id.btn_dialog_back_immediately)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogRegisteredSucceeded.dismiss();
//            }
//        });
//
//        final TextView tvDialogBackCounter = (TextView) view.findViewById(R.id.btn_dialog_counter_back);
//        timer = new CountDownTimer(3000,1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                tvDialogBackCounter.setText(( millisUntilFinished/1000 +1)  + " 秒后关闭");
//            }
//
//            @Override
//            public void onFinish() {
//                dialogRegisteredSucceeded.dismiss();
//            }
//        }.start();
//
//        dialogRegisteredSucceeded.setContentView(view);
//        dialogRegisteredSucceeded.setCancelable(true);
//        dialogRegisteredSucceeded.show();
//    }
//

    /**
     * 展示对话框
     *
     * @param
     */
    private void showMessageDialog(String dialogContent) {
        final BaseDialog dialogMessage = new BaseDialog(mContext);
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.fragment_dialog_common, null);

        final TextView tvDialogContent = (TextView) view.findViewById(R.id.tv_common_dialog_content);
        tvDialogContent.setText(dialogContent);

        final TextView btnDialogOk = (TextView) view.findViewById(R.id.btn_dialog_retry);
        btnDialogOk.setText("登录");
        btnDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMessage.dismiss();
                mContext.startActivity(new Intent(mContext, LoginActivity.class));
                Activity activity = (Activity) mContext;
                activity.finish();

            }
        });

        dialogMessage.setContentView(view);
        dialogMessage.setCancelable(false);
        dialogMessage.show();
    }

    private void startRegisterActivity(String base64Image) {
        Bundle bundle = new Bundle();
        bundle.putString("new_user_image", base64Image);
        Intent intent = new Intent(mContext, RegisterActivity.class);
        intent.putExtra("data_from_detect_activity", bundle);
        startActivity(intent);
    }


    /**
     * 初始化recycleView画截图得到的人脸图像
     */
    private void initRecy() {
//        mRecyAdapter = new RecyAdapter(this);
//
//        mLayoutManager = new LinearLayoutManager(DetectActivity.this,
//                LinearLayoutManager.HORIZONTAL, true);
//        // mRecyclerview.setLayoutManager(mLayoutManager);
//        mLayoutManager.setStackFromEnd(true);
//        // mRecyclerview.setAdapter(mRecyAdapter);
    }

    private void setCameraType(CameraImageSource cameraImageSource) {
        // TODO 选择使用前置摄像头
        cameraImageSource.getCameraControl().setCameraFacing(ICameraControl.CAMERA_FACING_FRONT);

        // TODO 选择使用usb摄像头
        //  cameraImageSource.getCameraControl().setCameraFacing(ICameraControl.CAMERA_USB);
        // 如果不设置，人脸框会镜像，显示不准
        //  previewView.getTextureView().setScaleX(-1);

        // TODO 选择使用后置摄像头
        // cameraImageSource.getCameraControl().setCameraFacing(ICameraControl.CAMERA_FACING_BACK);
        // previewView.getTextureView().setScaleX(-1);
    }

    private void setDisplayElements() {
        int displayColor = getResources().getColor(android.R.color.darker_gray);
        String titleText = "欢迎您";
        String countDownTimerText = "<< 欢迎您 >>";
        switch (detectStates) {
            case WAITING_FOR_SIGNING:
                displayColor = getResources().getColor(android.R.color.holo_orange_dark);
                titleText = "等待签到...";
                tvDetectNextSigningTimer.setText("<< 等待签到 >>");
                break;
            case SIGNING:
                displayColor = getResources().getColor(android.R.color.holo_blue_light);
                titleText = "正在签到...";
                tvDetectNextSigningTimer.setText("<< 正在签到 >>");
                break;
            case SIGN_FAILED_USER_NOT_FOUND:
            case SIGN_FAILED_USER_NOT_MATCH:
            case SIGN_FAILED_OTHER_REASONS:
                displayColor = getResources().getColor(android.R.color.holo_red_light);
                titleText = "签到失败！请重试。";
                break;
            case SIGN_SUCCEEDED:
                displayColor = getResources().getColor(android.R.color.holo_green_light);
                titleText = "签到成功！谢谢！";
                break;
            default:
                break;
        }
        tvDetectResultTitle.setTextColor(displayColor);
        tvDetectResultTitle.setText(titleText);
        tvDetectNextSigningTimer.setBackgroundColor(displayColor);
    }
}
