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
import com.ihealth.facecheckinapp.R;
import com.ihealth.retrofit.ApiUtil;
import com.ihealth.retrofit.Constants;
import com.ihealth.utils.FaceDetectExtendManager;
import com.ihealth.utils.SharedPreferenceUtil;
import com.ihealth.views.CheckItemSelectDialog;
import com.ihealth.views.PirntAllDepartmentDialog;
import com.ihealth.views.PrintContentDialog;
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

    private static final int REQUEST_CODE_INIT_STATE = 2001;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
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

    // private TextView tvDetectHospitalTitle;
    /*private TextView tvDetectResultTitle;
    private TextView tvDetectResultName;
    private TextView tvDetectResultMobile;
    private TextView tvDetectResultIdCard;

    private Button btnDetectContinueSigning;*/
    private TextView tvDetectNextSigningTimer;

    private TexturePreviewView previewView;
    private boolean mDetectStopped = false;

    private FaceDetectManager faceDetectManager;
    private DetectRegionProcessor cropProcessor = new DetectRegionProcessor();
    private TextureView mTextureView;
    // private RecyclerView mRecyclerview;
    private List<Bitmap> mList = new ArrayList<>();
    // private RecyAdapter mRecyAdapter;
    private Handler mHandler = new Handler();
    // private LinearLayoutManager mLayoutManager;

    private BaseDialog dialogReLogin;
    private BaseDialog dialogMessage;
    private BaseDialog dialogChooseRole;
    private BaseDialog dialogChooseOutpatient;

    private BluetoothPrinter printer;
    private TextView bleStatus;


    private int mFrameIndex = 0;
    private int mRound = 2;

    private CountDownTimer timer;

    // private int mSearchFailTimes = 0;

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
        mContext = this;
        initComponents();
        initCameraView();
        faceDetectManager = new FaceDetectManager(this);


        initView();
//        getAppointmentInfo();
        initBluetooth();

//        Intent intent = new Intent(mContext, RegisterPatientActivity.class);
//        startActivityForResult(intent,REQUEST_CODE_INIT_STATE);

    }

    private void initBluetooth() {
//        printer = new BluetoothPrinter(this, new PrinterStatusResponse() {
//            @Override
//            public void onStatusChange(BluetoothPrinterStatus status) {
//                switch (status){
////                    case OPEN: {bleStatus.setText("蓝牙已开启"); break;}
////                    case CLOSED:{bleStatus.setText("蓝牙已关闭");break;}
////
////                    case SEARCHING: {bleStatus.setText("搜索打印机");break;}
////                    case SEARCHING_CANCELED:{bleStatus.setText("搜索已取消");break;}
////                    case SEARCHING_STOPPED:{bleStatus.setText("搜索已结束");break;}
////                    case CONNECTED:{bleStatus.setText("打印机就绪");break;}
////                    case DISCONNECTED:{bleStatus.setText("打印机已断开");break;}
////                    case CONNECTING:{bleStatus.setText("连接打印机");break;}
////                    case CONNECT_FAIL: {bleStatus.setText("连接失败");break;}
////                    default:bleStatus.setText(status.toString());
//                }
//
//            }
//        });
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // Android M Permission check
//            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
//                return;
//            }
//        }
//        printer.searchAndConnect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    printer.searchAndConnect();
                }
                break;
        }
    }


    private void initComponents() {
        dialogReLogin = new BaseDialog(mContext);
        dialogMessage = new BaseDialog(mContext);
        dialogChooseOutpatient = new BaseDialog(mContext);
        dialogChooseRole = new BaseDialog(mContext);
    }

    /**
     * 初始化view
     */
    private void initView() {
        previewView = (TexturePreviewView) findViewById(R.id.activity_detect_texture_preview_view);
        mTextureView = (TextureView) findViewById(R.id.activity_detect_texture_view);
        backImageview.setBackground(getResources().getDrawable(R.drawable.back_white_iv));
        commonHeaderTitle.setTextColor(getResources().getColor(R.color.colorFFFFFF));
        tvDetectNextSigningTimer = (TextView) findViewById(R.id.tv_detect_next_signing_timer);
        commonHeaderLayout.setBackgroundColor(mContext.getResources().getColor(R.color.color1D1D1D));
       faceDetectSuperManager = new FaceDetectExtendManager(this,previewView,mTextureView,tvDetectNextSigningTimer,mHandler);

    }

    private void initCameraView() {
        mHandler = new InnerHandler(this);
        mHandler.sendEmptyMessageDelayed(MSG_INITVIEW, 200);
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
    protected void onRestart() {
        super.onRestart();
//        faceDetectSuperManager.start();
//        mDetectStopped = true;
//        int size = mList.size();
//        for (int i = 0; i < size; i++) {
//            Bitmap bmp = mList.get(i);
//            if (bmp != null && !bmp.isRecycled()) {
//                bmp.recycle();
//            }
//        }
//        mList.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Log.i("onResume", "onResume: mDetectStopped = " + mDetectStopped);
        if (mDetectStopped) {
            mDetectStopped = false;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialogReLogin = null;
        dialogMessage = null;
        dialogChooseOutpatient = null;
        dialogChooseRole = null;
//        printer.destroy();

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

    private void getAppointmentInfo() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("patientId", "5ab4a677db7e8e31401c9f89");
//        requestMap.put("patientId", SharedPreferenceUtil.getStringTypeSharedPreference(mContext, Constants.SP_NAME_PATIENT_INFOS, Constants.SP_KEY_PATIENT_ID));
        Gson gson = new Gson();
        String jsonStr = gson.toJson(requestMap, HashMap.class);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonStr);
        ApiUtil.getAppointmentCall(mContext, requestBody).enqueue(new Callback<AppointmentsBean>() {
            @Override
            public void onResponse(Call<AppointmentsBean> call, Response<AppointmentsBean> response) {
                Log.i("getAppointmentInfo", "onResponse: " + response.body());
                AppointmentsBean appointmentsBean = response.body();
                if (appointmentsBean != null) {
//                    new CheckItemSelectDialog(DetectActivity.this, appointmentsBean);
//                    new CheckItemSelectDialog(DetectActivity.this,appointmentsBean);
//                    tackleWithResponds(responseMessage, "");
                    new PrintContentDialog(DetectActivity.this,appointmentsBean);
//                    new PirntAllDepartmentDialog(DetectActivity.this,appointmentsBean);
                } else {
//                    showReLoginDialog("系统认证失败，请重新登录。");
                }
            }

            @Override
            public void onFailure(Call<AppointmentsBean> call, Throwable t) {
                Log.i("getAppointmentInfo", "onFailure: " + t);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_INIT_STATE:
                mList.clear();
//                resetDisplayContents();
                break;
            default:
                break;
        }

    }
}
