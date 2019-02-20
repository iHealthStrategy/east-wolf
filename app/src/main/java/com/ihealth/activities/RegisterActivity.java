package com.ihealth.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.ihealth.BaseActivity;
import com.ihealth.BaseDialog;
import com.ihealth.bean.AddUserRequestBean;
import com.ihealth.bean.ResponseMessageBean;
import com.ihealth.bean.UserInfo;
import com.ihealth.facecheckinapp.R;
import com.ihealth.retrofit.ApiUtil;
import com.ihealth.utils.TextInfosCheckUtil;

import java.lang.ref.WeakReference;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 新用户注册
 *
 * @author liyanwen
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    //private static final int MSG_INITVIEW = 1001;
    //private PreviewView registerPreviewView;
    //private TextureView mRegisterTextureView;
    //private boolean mDetectStopped = false;

    //private FaceDetectManager faceDetectManager;
    //private DetectRegionProcessor cropProcessor = new DetectRegionProcessor();

    private Handler mHandler = new Handler();
    private Context mContext;

    //private Paint paint = new Paint();
    //private int mRound = 2;

    TextView tvRegisterHeader;
    TextView tvRegisterTitle;
    ViewFlipper vfNewUserInfos;

    //ImageView ivUserPhotoPreivew;
    //Button btnNewUserStep1TakePhoto;

    Button btnNewUserStep2Previous;
    Button btnNewUserStep2Next;

    Button btnNewUserStep3Previous;
    Button btnNewUserStep3Skip;
    Button btnNewUserStep3Next;

    Button btnNewUserStep4Previous;
    Button btnNewUserStep4Done;

    EditText etvNewUserName;
    EditText etvNewUserIdCard;
    EditText etvNewUserMobile;

    String mFaceBase64Image = "";

    boolean isUserNameValid = false;
    boolean isUserIdCardValid = true;
    boolean isUserMobileValid = false;

    CountDownTimer timer;

    //Bitmap mFace;
    //FaceFilter.TrackedModel mTrackedModel;
    //boolean isFaceMeetCriteria = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mContext = this;
        //faceDetectManager = new FaceDetectManager(this);

        initData();
        initView();
        initListeners();

        mHandler = new InnerHandler(this);
        // mHandler.sendEmptyMessageDelayed(MSG_INITVIEW, 200);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mDetectStopped) {
//            faceDetectManager.start();
//            mDetectStopped = false;
//        }
//        mHandler.postDelayed(addUserRunnable, 10);
    }

    private static class InnerHandler extends Handler {
        private WeakReference<RegisterActivity> mWeakReference;

        public InnerHandler(RegisterActivity activity) {
            super();
            this.mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mWeakReference == null || mWeakReference.get() == null) {
                return;
            }
            RegisterActivity activity = mWeakReference.get();
            if (activity == null) {
                return;
            }
            if (msg == null) {
                return;

            }
//            switch (msg.what) {
//                case MSG_INITVIEW:
//                    activity.start();
//                    break;
//                default:
//                    break;
//            }
        }
    }

    private void initData(){
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data_from_detect_activity");
        mFaceBase64Image = bundle.getString("new_user_image","");
        Log.i("initData", "initData: " + mFaceBase64Image);
    }

    private void initView() {

        tvRegisterHeader = (TextView) findViewById(R.id.tv_register_header);
        tvRegisterTitle = (TextView) findViewById(R.id.tv_register_title);

        vfNewUserInfos = (ViewFlipper) findViewById(R.id.vf_register_new_user_infos);
        vfNewUserInfos.setAutoStart(false);
        vfNewUserInfos.setDisplayedChild(0);

//        ivUserPhotoPreivew = (ImageView) findViewById(R.id.iv_user_photo_preview);
//        btnNewUserStep1TakePhoto = (Button) findViewById(R.id.btn_detect_new_user_step_1_take_photo);

        btnNewUserStep2Previous = (Button) findViewById(R.id.btn_detect_new_user_step_2_previous);
        btnNewUserStep2Next = (Button) findViewById(R.id.btn_detect_new_user_step_2_next);

        btnNewUserStep3Previous = (Button) findViewById(R.id.btn_detect_new_user_step_3_previous);
        btnNewUserStep3Skip = (Button) findViewById(R.id.btn_detect_new_user_step_3_skip);
        btnNewUserStep3Next = (Button) findViewById(R.id.btn_detect_new_user_step_3_next);

        btnNewUserStep4Previous = (Button) findViewById(R.id.btn_detect_new_user_step_4_previous);
        btnNewUserStep4Done = (Button) findViewById(R.id.btn_detect_new_user_step_4_done);

        etvNewUserName = (EditText) findViewById(R.id.etv_register_name);
        etvNewUserIdCard = (EditText) findViewById(R.id.etv_register_id_card);
        etvNewUserMobile = (EditText) findViewById(R.id.etv_register_mobile);

//        registerPreviewView = (PreviewView) findViewById(R.id.register_preview_view);
//        mRegisterTextureView = (TextureView) findViewById(R.id.register_texture_view);
        // mRegisterTextureView.setOpaque(false);
        // mRecyclerview = (RecyclerView) findViewById(R.id.recyclerview);

//        // 不需要屏幕自动变黑。
//        mRegisterTextureView.setKeepScreenOn(true);
//
//        final CameraImageSource cameraImageSource = new CameraImageSource(this);
//        cameraImageSource.setPreviewView(registerPreviewView);
//
//        faceDetectManager.setImageSource(cameraImageSource);
//        faceDetectManager.setOnFaceDetectListener(new FaceDetectManager.OnFaceDetectListener() {
//            @Override
//            public void onDetectFace(final int retCode, FaceInfo[] infos, ImageFrame frame) {
//
//            }
//        });
//        faceDetectManager.setOnTrackListener(new FaceFilter.OnTrackListener() {
//            @Override
//            public void onTrack(final FaceFilter.TrackedModel trackedModel) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mTrackedModel = trackedModel;
//                        showFrame(trackedModel);
//                    }
//                });
//
//            }
//        });

//        cameraImageSource.getCameraControl().setPermissionCallback(new PermissionCallback() {
//            @Override
//            public boolean onRequestPermission() {
//                ActivityCompat.requestPermissions(RegisterActivity.this,
//                        new String[]{Manifest.permission.CAMERA}, 100);
//                return true;
//            }
//        });
//
//        ICameraControl control = cameraImageSource.getCameraControl();
//        control.setPreviewView(registerPreviewView);
//        // 设置检测裁剪处理器
//        faceDetectManager.addPreProcessor(cropProcessor);
//
//        int orientation = getResources().getConfiguration().orientation;
//        boolean isPortrait = (orientation == Configuration.ORIENTATION_PORTRAIT);
//
//        if (isPortrait) {
//            registerPreviewView.setScaleType(PreviewView.ScaleType.FIT_WIDTH);
//        } else {
//            registerPreviewView.setScaleType(PreviewView.ScaleType.FIT_HEIGHT);
//        }

//        int rotation = getWindowManager().getDefaultDisplay().getRotation();
////        cameraImageSource.getCameraControl().setDisplayOrientation(rotation);
////        setCameraType(cameraImageSource);
        //initBrightness();
        //initPaint();

    }

    private void initListeners() {
        btnNewUserStep2Next.setOnClickListener(this);
        btnNewUserStep2Previous.setOnClickListener(this);
        btnNewUserStep2Next.setOnClickListener(this);
        btnNewUserStep3Previous.setOnClickListener(this);
        btnNewUserStep3Skip.setOnClickListener(this);
        btnNewUserStep3Next.setOnClickListener(this);
        btnNewUserStep4Previous.setOnClickListener(this);
        btnNewUserStep4Done.setOnClickListener(this);
        //btnNewUserStep1TakePhoto.setOnClickListener(this);

        etvNewUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)){
                    isUserNameValid = true;
                    setButtonState(btnNewUserStep2Next, true);
                } else {
                    isUserNameValid = false;
                    setButtonState(btnNewUserStep2Next, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etvNewUserIdCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)){
                    if ("".equals(TextInfosCheckUtil.IDCardValidate(s.toString()))){
                        isUserIdCardValid = true;
                        setButtonState(btnNewUserStep3Next, true);
                    }else {
                        isUserIdCardValid = false;
                        setButtonState(btnNewUserStep3Next, false);
                    }
                } else {
                    isUserIdCardValid = true;
                    setButtonState(btnNewUserStep3Next, true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etvNewUserMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)){
                    if (TextInfosCheckUtil.checkMobile(s.toString())){
                        isUserMobileValid = true;
                        setButtonState(btnNewUserStep4Done, true);
                    } else {
                        isUserMobileValid = false;
                        setButtonState(btnNewUserStep4Done, false);
                    }
                }else {
                    isUserMobileValid = false;
                    setButtonState(btnNewUserStep4Done, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void setButtonState(Button button, boolean isEnabled){
        button.setEnabled(isEnabled);
        if (isEnabled){
            button.setBackground(getResources().getDrawable(R.drawable.button_round_shape_enabled));
        } else {
            button.setBackground(getResources().getDrawable(R.drawable.button_round_shape_disabled));
        }
    }

    /**
     * 设置相机亮度，不够200自动调整亮度到200
     */
//    private void initBrightness() {
//        int brightness = BrightnessTools.getScreenBrightness(RegisterActivity.this);
//        if (brightness < 200) {
//            BrightnessTools.setBrightness(this, 200);
//        }
//    }

    /**
     * 启动人脸检测
     */
//    private void start() {
//        Log.i("start", "start entered");
//        RectF newDetectedRect = new RectF(0, 0, R.dimen.layout_size_220dp, R.dimen.layout_size_220dp);
//        cropProcessor.setDetectedRect(newDetectedRect);
//        faceDetectManager.start();
//    }

    @Override
    protected void onStop() {
        super.onStop();
        //faceDetectManager.stop();
        //mDetectStopped = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null!= timer){
            timer.cancel();
            timer = null;
        }
    }

    private void changeTitle() {
        int index = vfNewUserInfos.getDisplayedChild();
        switch (index) {
//            case 0:
//                tvRegisterHeader.setText("建立患者信息（1/4）");
//                tvRegisterTitle.setText("请录入患者人脸数据");
//                break;
            case 0:
                tvRegisterHeader.setText("建立患者信息（1/3）");
                tvRegisterTitle.setText("请录入患者姓名");
                break;
            case 1:
                tvRegisterHeader.setText("建立患者信息（2/3）");
                tvRegisterTitle.setText("请录入患者身份证号(选填)");
                break;
            case 2:
                tvRegisterHeader.setText("建立患者信息（3/3）");
                tvRegisterTitle.setText("请录入患者手机号");
                break;
            default:
                tvRegisterHeader.setText("建立患者信息");
                tvRegisterTitle.setText("请录入患者信息");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_detect_new_user_step_1_take_photo:
//                vfNewUserInfos.setDisplayedChild(0);
//                changeTitle();
//                if (isFaceMeetCriteria) {
//                    mFace = mTrackedModel.cropFace();
//                    if (mFace != null) {
//                        ivUserPhotoPreivew.setImageBitmap(mFace);
//                    }
//                } else {
//
//                }
//                break;
//            case R.id.btn_detect_new_user_step_2_previous:
//                vfNewUserInfos.setDisplayedChild(0);
//                changeTitle();
//                break;
            case R.id.btn_detect_new_user_step_2_next:
                vfNewUserInfos.setDisplayedChild(1);
                changeTitle();
                break;
            case R.id.btn_detect_new_user_step_3_previous:
                vfNewUserInfos.setDisplayedChild(0);
                changeTitle();
                break;
            case R.id.btn_detect_new_user_step_3_next:
                vfNewUserInfos.setDisplayedChild(2);
                changeTitle();
                break;
            case R.id.btn_detect_new_user_step_3_skip:
                vfNewUserInfos.setDisplayedChild(2);
                changeTitle();
                etvNewUserIdCard.setText("");
                setButtonState(btnNewUserStep3Next, false);
                break;
            case R.id.btn_detect_new_user_step_4_previous:
                vfNewUserInfos.setDisplayedChild(1);
                changeTitle();
                break;
            case R.id.btn_detect_new_user_step_4_done:
                if (!mFaceBase64Image.isEmpty()) {
                    Log.d("4_done", "onClick: entered!");
                    mHandler.postDelayed(addUserRunnable, 100);
                }
                break;
            default:
                changeTitle();
                break;
        }
    }

    /**
     * 初始化画笔
     */
//    private void initPaint() {
//        paint.setColor(Color.GREEN);
//        paint.setStyle(Paint.Style.STROKE);
//    }

    /**
     * 绘制人脸框。
     *
     * @param model 追踪到的人脸
     */
//    private void showFrame(FaceFilter.TrackedModel model) {
//        Canvas canvas = mRegisterTextureView.lockCanvas();
//        if (canvas == null) {
//            return;
//        }
//        // 清空canvas
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//
//        if (model != null) {
//            FaceInfo info = model.getInfo();
//            model.getImageFrame().retain();
//            RectF rectCenter = new RectF(info.mCenter_x - 2 - info.mWidth * 1 / 2,
//                    info.mCenter_y - 2 - info.mWidth * 1 / 2,
//                    info.mCenter_x + 2 + info.mWidth * 1 / 2,
//                    info.mCenter_y + 2 + info.mWidth * 1 / 2);
//            registerPreviewView.mapFromOriginalRect(rectCenter);
//            // 绘制框
//            paint.setStrokeWidth(mRound);
//            paint.setAntiAlias(true);
//            canvas.drawRect(rectCenter, paint);
//
//            if (model.meetCriteria()) {
//                // 符合检测要求，绘制绿框
//                paint.setColor(Color.GREEN);
//                isFaceMeetCriteria = true;
//            } else {
//                isFaceMeetCriteria = false;
//            }
//        }
//        mRegisterTextureView.unlockCanvasAndPost(canvas);
//    }

    Runnable addUserRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i("addUserRunnable", "run: base64Image = " + mFaceBase64Image);

            String nickname = etvNewUserName.getText().toString();
            String phoneNumber = etvNewUserMobile.getText().toString();
            String idCard = etvNewUserIdCard.getText().toString();

            AddUserRequestBean addUserRequestBean = new AddUserRequestBean();
            addUserRequestBean.setBase64Image(mFaceBase64Image);
            addUserRequestBean.setUserInfo(new UserInfo(phoneNumber, nickname, idCard));
            addUserRequestBean.setHospitalId("chaoyang");

            Gson gson = new Gson();
            String jsonStr = gson.toJson(addUserRequestBean, AddUserRequestBean.class);

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonStr);

            ApiUtil.addUserCall(mContext, requestBody).enqueue(new Callback<ResponseMessageBean>() {
                @Override
                public void onResponse(Call<ResponseMessageBean> call, Response<ResponseMessageBean> response) {
                    Log.i("addUserRunnable", "onResponse: "+ response.body());
                    if (response.isSuccessful()) {
                        ResponseMessageBean responseMessageBean = response.body();
                        int resultStatus = responseMessageBean.getResultStatus();
                        if (resultStatus == 0){
                            showRegisteredResultDialog("恭喜您！签到成功！");
                        }
                    } else {
                        showRegisteredResultDialog("很抱歉，签到失败。\n请返回重试。");
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessageBean> call, Throwable t) {
                    Log.i("addUserRunnable", "onFailure: " + t.toString());
                    showRegisteredResultDialog("很抱歉，签到失败。\n请返回重试。");
                }
            });

        }
    };

    /**
     * 展示对话框
     *
     * @param
     */
    private void showRegisteredResultDialog(String dialogContent) {
        final BaseDialog dialogRegisteredSucceeded = new BaseDialog(mContext);
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.fragment_dialog_register_success, null);

        final TextView tvDialogContent = (TextView) view.findViewById(R.id.tv_dialog_content);
        tvDialogContent.setText(dialogContent);

        (view.findViewById(R.id.btn_dialog_back_immediately)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogRegisteredSucceeded.dismiss();
                Activity activity  = (Activity) mContext;
                activity.finish();
            }
        });

        final TextView tvDialogBackCounter = (TextView) view.findViewById(R.id.btn_dialog_counter_back);
        timer = new CountDownTimer(5000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvDialogBackCounter.setText(( millisUntilFinished/1000 +1)  + " 秒后自动返回");
            }

            @Override
            public void onFinish() {
                dialogRegisteredSucceeded.dismiss();
                Activity activity  = (Activity) mContext;
                activity.finish();
            }
        }.start();

        dialogRegisteredSucceeded.setContentView(view);
        dialogRegisteredSucceeded.setCancelable(false);
        dialogRegisteredSucceeded.show();
    }


//    private void setCameraType(CameraImageSource cameraImageSource) {
//        // TODO 选择使用前置摄像头
//        cameraImageSource.getCameraControl().setCameraFacing(ICameraControl.CAMERA_FACING_FRONT);
//
//        // TODO 选择使用usb摄像头
//        //  cameraImageSource.getCameraControl().setCameraFacing(ICameraControl.CAMERA_USB);
//        // 如果不设置，人脸框会镜像，显示不准
//        //  registerPreviewView.getTextureView().setScaleX(-1);
//
//        // TODO 选择使用后置摄像头
//        // cameraImageSource.getCameraControl().setCameraFacing(ICameraControl.CAMERA_FACING_BACK);
//        // registerPreviewView.getTextureView().setScaleX(-1);
//    }

}
