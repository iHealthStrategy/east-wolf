package com.ihealth.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
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
import com.ihealth.activities.DetectActivity;
import com.ihealth.activities.RegisterPatientActivity;
import com.ihealth.activities.RegisterResultActivity;
import com.ihealth.bean.AppointmentsBean;
import com.ihealth.bean.ResponseMessageBean;
import com.ihealth.facecheckinapp.R;
import com.ihealth.retrofit.ApiUtil;
import com.ihealth.retrofit.Constants;
import com.ihealth.views.FaceDetectResultDialog;
import com.ihealth.views.PirntAllDepartmentDialog;
import com.ihealth.views.PrintContentDialog;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ihealth.activities.DetectActivity.MSG_INITVIEW;
import static com.ihealth.retrofit.Constants.FACE_RESPONSE_CODE_ERROR_SEARCH_USER_NOT_FOUND;

public class FaceDetectExtendManager {

    private TextView mResultTV;
    private Handler mHandler;
    private int mScreenW;
    private int mScreenH;
    private DETECT_STATES detectStates;
    private CountDownTimer timer;

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
         * 签到失败：已经签到过，重复签到（错误code: 4001）
         */
        SIGN_FAILED_ALREADY_SIGNED_IN,
        /**
         * 签到成功
         */
        SIGN_SUCCEEDED,
    }

    private int mRound = 2;
    private Context mContext;
    private TexturePreviewView mPreviewView;
    private TextureView mTextureView;
    private FaceDetectManager faceDetectManager;
    private DetectRegionProcessor cropProcessor = new DetectRegionProcessor();
    private Paint paint = new Paint();
    // private RecyclerView mRecyclerview;
    private List<Bitmap> mList = new ArrayList<>();

    public FaceDetectExtendManager() {
    }

    public FaceDetectExtendManager(Context context, TexturePreviewView previewView, TextureView textureView, TextView detectResultTv, Handler handler) {
        faceDetectManager = new FaceDetectManager(context);
        mContext = context;
        mPreviewView = previewView;
        mTextureView = textureView;
        mHandler = handler;
        mResultTV = detectResultTv;
        initArguments();
    }

    /**
     * 获取屏幕参数
     */
    private void initScreen() {
        WindowManager manager = ((Activity) mContext).getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        mScreenW = outMetrics.widthPixels;
        mScreenH = outMetrics.heightPixels;
        mRound = mContext.getResources().getDimensionPixelSize(R.dimen.round);
    }

    /**
     * 初始化view
     */
    private void initArguments() {
        initScreen();
        mTextureView.setOpaque(false);
        // mRecyclerview = (RecyclerView) findViewById(R.id.recyclerview);

        // 不需要屏幕自动变黑。
        mTextureView.setKeepScreenOn(true);

        final CameraImageSource cameraImageSource = new CameraImageSource(mContext);
        cameraImageSource.setPreviewView(mPreviewView);

        faceDetectManager.setImageSource(cameraImageSource);

        faceDetectManager.setOnFaceDetectListener(new FaceDetectManager.OnFaceDetectListener() {
            @Override
            public void onDetectFace(final int retCode, FaceInfo[] infos, ImageFrame frame) {

                if (retCode == 0) {
//                        mHandler.sendEmptyMessageDelayed(MSG_INITVIEW, 200);
                    mHandler.sendEmptyMessageDelayed(DetectActivity.MSG_REFRESH_TITLE, 200);
                } else {
                    mHandler.sendEmptyMessageDelayed(DetectActivity.MSG_REFRESH_TITLE, 200);
                }
            }
        });
        faceDetectManager.setOnTrackListener(new FaceFilter.OnTrackListener() {
            @Override
            public void onTrack(final FaceFilter.TrackedModel trackedModel) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
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
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{Manifest.permission.CAMERA}, 100);
                return true;
            }
        });

        ICameraControl control = cameraImageSource.getCameraControl();
        control.setPreviewView(mPreviewView);
        // 设置检测裁剪处理器
        faceDetectManager.addPreProcessor(cropProcessor);

        int orientation = mContext.getResources().getConfiguration().orientation;
        boolean isPortrait = (orientation == Configuration.ORIENTATION_PORTRAIT);

        if (isPortrait) {
            mPreviewView.setScaleType(PreviewView.ScaleType.FIT_WIDTH);
        } else {
            mPreviewView.setScaleType(PreviewView.ScaleType.FIT_HEIGHT);
        }

        int rotation = ((Activity) mContext).getWindowManager().getDefaultDisplay().getRotation();
        cameraImageSource.getCameraControl().setDisplayOrientation(rotation);
        setCameraType(cameraImageSource);
        initBrightness();
        initPaint();
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

    /**
     * 设置相机亮度，不够200自动调整亮度到200
     */
    private void initBrightness() {
        int brightness = BrightnessTools.getScreenBrightness((Activity) mContext);
        if (brightness < 200) {
            BrightnessTools.setBrightness((Activity) mContext, 200);
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
            RectF rectCenter = new RectF(1.8f * info.mCenter_x - 2 - info.mWidth * 7 / 10,
                    9 * info.mCenter_y / 10 - 2 - info.mWidth * 7 / 10,
                    1.8f * info.mCenter_x + 2 + info.mWidth * 7 / 10,
                    9 * info.mCenter_y / 10 + 2 + info.mWidth * 7 / 10);
            mPreviewView.mapFromOriginalRect(rectCenter);
            // 绘制框
            paint.setStrokeWidth(mRound);
            paint.setAntiAlias(true);
            canvas.drawRect(rectCenter, paint);

            if (model.meetCriteria()) {
                // 符合检测要求，绘制绿框
                paint.setColor(Color.GREEN);
            }
            final Bitmap face = model.cropFace();
            if (face != null && mList.size() < 10) {
                mList.add(face);
                if (mList.size() == 10) {
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
                requestMap.put("hospitalId",
                        SharedPreferenceUtil.getStringTypeSharedPreference(mContext, Constants.SP_NAME_HOSPITAL_INFOS, Constants.SP_KEY_HOSPITAL_GROUP_ID)
                );

                Gson gson = new Gson();
                String jsonStr = gson.toJson(requestMap, HashMap.class);

                final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonStr);
                ApiUtil.searchFaceCall(mContext, requestBody).enqueue(new Callback<ResponseMessageBean>() {
                    @Override
                    public void onResponse(Call<ResponseMessageBean> call, Response<ResponseMessageBean> response) {
                        // Log.i("searchFaceRunnable", "onResponse: response = " + response.body());
                        ResponseMessageBean responseMessage = response.body();
                        Log.e("123", responseMessage.toString());

                        if (responseMessage != null) {
                            handleFaceResult(responseMessage, base64Image);
//                            tackleWithResponds(responseMessage, base64Image);
                        } else {

//                            showReLoginDialog("系统认证失败，请重新登录");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseMessageBean> call, Throwable t) {
                        Log.i("123", "onFailure: t = " + t);
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

    private void handleFaceResult(final ResponseMessageBean responseMessage, final String base64Image) {
        final AppointmentsBean appointmentsBean = responseMessage.getResultContent();
        final AppointmentsBean.Patient patient = appointmentsBean.getPatient();
        final FaceDetectResultDialog dialog = new FaceDetectResultDialog(mContext, patient, base64Image);

        dialog.setOnFirstAndSecondClicker(new FaceDetectResultDialog.OnFirstAndSecondClicker() {
            @Override
            public void onFirstClick() {
                firstBtnResult(responseMessage);
            }

            @Override
            public void onSecondClick() {
                //无论如何状态，第二个按钮都是打印小条
                if (appointmentsBean != null && appointmentsBean.getPatient() != null) {
                    String patientType = appointmentsBean.getPatient().getPatientType();
                    if (patientType.equals("GTZH")) {
                        new PrintContentDialog(mContext, appointmentsBean).setOnPriterClicker(new PrintContentDialog.OnPriterClicker() {
                            @Override
                            public void onPriterClick() {
                                Toast.makeText(mContext, R.string.priter_sucess_toast, Toast.LENGTH_SHORT).show();
                                ((Activity) mContext).finish();
                            }

                            @Override
                            public void onCancel() {
                                reFaceDetect();
                            }
                        });
                    } else {
                        new PirntAllDepartmentDialog(mContext, appointmentsBean).setOnPriterClicker(new PirntAllDepartmentDialog.OnPriterClicker() {
                            @Override
                            public void onPriterClick() {
                                Toast.makeText(mContext, R.string.priter_sucess_toast, Toast.LENGTH_SHORT).show();
                                ((Activity) mContext).finish();
                            }

                            @Override
                            public void onCancel() {
                                reFaceDetect();
                            }
                        });
                    }
                }

            }

            @Override
            public void onNewPatientClick() {
                Intent intent = new Intent(mContext, RegisterPatientActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(BundleKeys.BASE64IMAGE, base64Image);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }

            @Override
            public void onReFaceDetectClick() {
                startThreeTimeFliter();
            }
        });
        switch (responseMessage.getResultStatus()) {
            case Constants.FACE_RESPONSE_CODE_SUCCESS://识别成功，直接打印 0
                dialog.setData(ConstantArguments.DETECT_RESULT_SUCESS_SIGN_PREPARE_CLINIC);
                break;
            case FACE_RESPONSE_CODE_ERROR_SEARCH_USER_NOT_FOUND://跳转添加新用户，直接打印 1001
            case Constants.FACE_RESPONSE_CODE_ERROR_SEARCH_USER_FOUND_NOT_MATCH://  1002 1003  3001
            case Constants.FACE_RESPONSE_CODE_ERROR_SEARCH_OTHER_ERRORS:
                Intent intent = new Intent(mContext, RegisterPatientActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(BundleKeys.BASE64IMAGE, base64Image);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
                break;
            case Constants.FACE_RESPONSE_CODE_ERROR_DETECT_USER_FACE_INVALID://重新扫脸  3001
                dialog.setData(ConstantArguments.DETECT_RESULT_FAILED);
                startThreeTimeFliter();

                break;
            case Constants.FACE_RESPONSE_CODE_ERROR_ADD_USER_USER_NOT_EXIST://添加用户失败 2001 2002
            case Constants.FACE_RESPONSE_CODE_ERROR_ADD_USER_OTHER_ERRORS:
//                dialog.setData(ConstantArguments.DETECT_RESULT_FAILED);
                break;
            case Constants.FACE_RESPONSE_CODE_ERROR_ALREADY_SIGNED_IN://重复签到，打印就诊小条 4001
                dialog.setData(ConstantArguments.DETECT_RESULT_SUCESS_SIGN_MORE_TIME);
                break;

            case Constants.FACE_RESPONSE_CODE_ERROR_NEED_CONTACT_CDE://4002签到错误，请联系照护师
                dialog.setData(ConstantArguments.DETECT_RESULT_SUCESS_NOT_SUBSCRIBE_ADD_CLINIC);
                break;

            case Constants.FACE_RESPONSE_CODE_ERROR_SHOULD_CHECK_CERTAIN_DAY://4003签到错误，共同照护患者不在当天
                dialog.setData(ConstantArguments.DETECT_RESULT_SUCESS_NOT_SUBSCRIBE_ADD_CLINIC);
                break;

            case Constants.FACE_RESPONSE_CODE_ERROR_OTHER_REASONS://4004其他签到错误类型，直接提示，联系照护师
                dialog.setData(ConstantArguments.DETECT_RESULT_SUCESS_NOT_SUBSCRIBE_ADD_CLINIC);
                break;

            default:
                break;
//        }
        }
    }

    private void startThreeTimeFliter() {
        //以下代码是重新拍照3次超过核验次数，版本不做
//                String dateKetString = DateUtils.getFormatDateStringByFormat(DateUtils.getCurrentSystemDate(), DateUtils.DATE_FORMAT_yyyymmdd);
//                String faceTime = SharedPreferenceUtil.getStringTypeSharedPreference(mContext, SharedPreferenceUtil.SP_FACE_DETECT_TIME, dateKetString);
//                int times = 1;
//                if (!"".equals(faceTime)) {
//                    times = Integer.parseInt(faceTime);
//                }
////                if (times < 3) {
//                times++;

//                detectStates = DETECT_STATES.SIGN_FAILED_USER_NOT_MATCH;
//                SharedPreferenceUtil.editSharedPreference(mContext, SharedPreferenceUtil.SP_FACE_DETECT_TIME, dateKetString, times + "");
//                } else {
//
//                    Intent intentToTimes = new Intent(mContext, RegisterResultActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable(BundleKeys.APPOINTMENTSBEAN, appointmentsBean);
//                    bundle.putInt(BundleKeys.REGISTER_RESULT_STATUS, ConstantArguments.REGISTER_FAILED_TO_TIMES);
//                    intentToTimes.putExtras(bundle);
//                    mContext.startActivity(intentToTimes, bundle);
//                }

    }

    private void firstBtnResult(ResponseMessageBean responseMessage) {
        int status =responseMessage.getResultStatus();
        switch (status) {
            case Constants.FACE_RESPONSE_CODE_ERROR_SEARCH_USER_FOUND_NOT_MATCH://重新扫脸  1002 1003  3001
            case Constants.FACE_RESPONSE_CODE_ERROR_SEARCH_OTHER_ERRORS:
            case Constants.FACE_RESPONSE_CODE_ERROR_DETECT_USER_FACE_INVALID:
            case Constants.FACE_RESPONSE_CODE_ERROR_ALREADY_SIGNED_IN://重复签到，打印就诊小条 4001
                reFaceDetect();
                break;
            case Constants.FACE_RESPONSE_CODE_ERROR_ADD_USER_USER_NOT_EXIST://添加用户失败 2001 2002
            case Constants.FACE_RESPONSE_CODE_ERROR_ADD_USER_OTHER_ERRORS:
                break;

            case Constants.FACE_RESPONSE_CODE_ERROR_NEED_CONTACT_CDE://4002签到错误，请联系照护师
            case Constants.FACE_RESPONSE_CODE_ERROR_SHOULD_CHECK_CERTAIN_DAY://4003签到错误，共同照护患者不在当
            case Constants.FACE_RESPONSE_CODE_ERROR_OTHER_REASONS://4004其他签到错误类型，直接提示，联系照护师
                Intent intentToTimes = new Intent(mContext, RegisterResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(BundleKeys.REGISTER_RESULT_STATUS, ConstantArguments.REGISTER_FAILED_ADD_CLINIC);

                intentToTimes.putExtras(bundle);
                mContext.startActivity(intentToTimes, bundle);
                break;

            default:
                break;
        }
    }

    /**
     * 启动人脸检测
     */
    public void start() {
        RectF newDetectedRect = new RectF(0, 0, mScreenW, mScreenH);
        cropProcessor.setDetectedRect(newDetectedRect);
        faceDetectManager.start();
    }

    public void stop() {
        faceDetectManager.stop();
        int size = mList.size();
        for (int i = 0; i < size; i++) {
            Bitmap bmp = mList.get(i);
            if (bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
            }
        }
        mList.clear();
    }

    public void reFaceDetect() {
        mList.clear();
        mHandler.sendEmptyMessageDelayed(MSG_INITVIEW, 200);

    }
}
