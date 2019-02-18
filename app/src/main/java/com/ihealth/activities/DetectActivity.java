/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.ihealth.activities;


import android.Manifest;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

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
import com.ihealth.bean.AddUserBean;
import com.ihealth.bean.AddUserRequestBean;
import com.ihealth.bean.UserInfo;
import com.ihealth.facecheckinapp.R;
import com.ihealth.retrofit.ApiUtil;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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
    private PreviewView previewView;
    private ImageView closeIv;
    private boolean mDetectStoped = false;

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

    private enum detectState {
        /**
         * 录入人脸
         */
        RECORDING_FACE,
        /**
         * 录入人脸失败
         */
        RECORD_FACE_FAILED,
        /**
         * 录入人脸成功
         */
        RECORD_FACE_SUCCEEDED,
        /**
         * 录入人脸失败重试
         */
        RECORD_FACE_FAILED_TRY_AGAIN,
        /**
         * 识别人脸
         */
        RECOGNISING_FACE,
        /**
         * 识别人脸成功
         */
        RECOGNIZE_FACE_SUCCEEDED,
        /**
         * 识别人脸失败重试
         */
        RECOGNIZE_FACE_FAILED_TRY_AGAIN,
        /**
         * 识别人脸失败新用户创建
         */
        RECOGNIZE_FACE_FAILED_NEW_USER,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
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
        mDetectStoped = true;
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
        if (mDetectStoped) {
            faceDetectManager.start();
            mDetectStoped = false;
        }
        mHandler.postDelayed(scrollRunnable, 10);

    }

    private static class InnerHandler extends Handler {
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
            RectF rectCenter = new RectF(info.mCenter_x - 2 - info.mWidth * 3 / 5,
                    info.mCenter_y - 2 - info.mWidth * 3 / 5,
                    info.mCenter_x + 2 + info.mWidth * 3 / 5,
                    info.mCenter_y + 2 + info.mWidth * 3 / 5);
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
            //  final Bitmap face =ImageUtil.bitmapFromArgb(model.getImageFrame());
            if (face != null) {
//                    int size = mList.size();
//                    // 释放一些，以防止太多
//                    if (size >= 6) {
//                        Bitmap bmp = mList.get(size - 6);
//                        if (bmp != null) {
//                            bmp.recycle();
//                            Log.d("liujinhui", "recycle size is:" + size);
//                            bmp = null;
//                        }
//                    }
                mList.add(face);
                // Log.d("liujinhui", "add face ok");
                if (mList.size() == 5){
                    mHandler.postDelayed(scrollRunnable, 100);
                }

                // mFrameIndex = 0;
            }
//            mFrameIndex++;
//            Log.d("liujinhui", "add face index is:" + mFrameIndex);
//            if (mFrameIndex == 10) {
//                final Bitmap face = model.cropFace();
//                //  final Bitmap face =ImageUtil.bitmapFromArgb(model.getImageFrame());
//                if (face != null) {
////                    int size = mList.size();
////                    // 释放一些，以防止太多
////                    if (size >= 6) {
////                        Bitmap bmp = mList.get(size - 6);
////                        if (bmp != null) {
////                            bmp.recycle();
////                            Log.d("liujinhui", "recycle size is:" + size);
////                            bmp = null;
////                        }
////                    }
//                    mList.add(face);
//                    Log.d("liujinhui", "add face ok");
//                    mHandler.postDelayed(scrollRunnable, 100);
//                    // mFrameIndex = 0;
//                }
//            }
        }
        mTextureView.unlockCanvasAndPost(canvas);
    }


    Runnable scrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (mList.size()>0){
                Bitmap uploadedFace = mList.get(0);
                String base64Image = convertImageToBase64String(uploadedFace);
                Log.i("scrollRunnable", "run: base64Image = "+base64Image);

                AddUserRequestBean addUserRequestBean = new AddUserRequestBean();
                addUserRequestBean.setBase64Image(base64Image);
                addUserRequestBean.setUserInfo(new UserInfo("17703941614","曹大帅","372328199109080614"));
                addUserRequestBean.setHospitalId("chaoyang");

                Gson gson = new Gson();
                String jsonStr = gson.toJson(addUserRequestBean, AddUserRequestBean.class);

                // Log.i("kkkkk", "run: jsonStr"+jsonStr);
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonStr);

                ApiUtil.addUserCall(requestBody).enqueue(new Callback<AddUserBean>() {
                    @Override
                    public void onResponse(Call<AddUserBean> call, Response<AddUserBean> response) {
                        Log.i("scrollRunnable", "onResponse: "+ response.body().getResultContent());
                        if (response.isSuccessful()){

                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Call<AddUserBean> call, Throwable t) {
                        Log.i("scrollRunnable", "onFailure: "+t.toString());
                    }
                });


//            MultipartBody.Builder builder = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("hospitalId","012345");
//            RequestBody image = RequestBody.create(MediaType.parse("multipart/form-data"),uploadedFace);

//            int count = mRecyAdapter.getItemCount();
//            int curIndex = count - 1;
//            // mRecyclerview.scrollToPosition(curIndex);
//            mRecyAdapter.setDatas(mList);
//            // mRecyclerview.invalidate();
//            //  Log.d("liujinhui", "in runnuable data size is:" + mList.size());
            }

        }
    };

    private static String convertImageToBase64String(Bitmap imageBitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (null != imageBitmap){
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            byte[] byteArray = stream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        }else {
            return null;
        }
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
}
