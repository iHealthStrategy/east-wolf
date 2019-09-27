package com.ihealth.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.ihealth.BaseActivity;
import com.ihealth.BaseDialog;
import com.ihealth.bean.AddUserRequestBean;
import com.ihealth.bean.FaceDetectResultByPhone;
import com.ihealth.bean.ResponseMessageBean;
import com.ihealth.bean.UserInfo;
import com.ihealth.events.FinshDetectRegisterAndResultEvent;
import com.ihealth.events.FinshDetectRegisterSelectTypeAndResultEvent;
import com.ihealth.events.FinshRegisterAndResultEvent;
import com.ihealth.events.FinshRegisterSelectTypeAndResultEvent;
import com.ihealth.facecheckinapp.R;
import com.ihealth.retrofit.ApiUtil;
import com.ihealth.retrofit.Constants;
import com.ihealth.utils.BundleKeys;
import com.ihealth.utils.ConstantArguments;
import com.ihealth.utils.LoadingProgressBar;
import com.ihealth.utils.SharedPreferenceUtil;
import com.ihealth.utils.TextInfosCheckUtil;
import com.ihealth.views.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.HashMap;
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
 * 注册新患者信息的activity
 * Created by Wangyuxu on 2019/09/23.
 */

public class RegisterPatientActivity extends BaseActivity {
    private static final String TAG = "RegisterActivity";
    private static final String MSG_UPDATE = "RegisterActivity";
    @BindView(R.id.common_header_title)
    TextView commonHeaderTitle;
    @BindView(R.id.common_header_back_layout)
    LinearLayout commonHeaderBackLayout;
    @BindView(R.id.activity_register_telephone_et)
    EditText activityRegisterTelephoneEt;
    @BindView(R.id.activity_register_telephone_dele_iv)
    ImageView activityRegisterTelephoneDeleIv;
    @BindView(R.id.activity_register_name_et)
    EditText activityRegisterNameEt;
    @BindView(R.id.activity_register_name_iv)
    ImageView activityRegisterNameIv;
    @BindView(R.id.activity_register_idcard_et)
    EditText activityRegisterIdcardEt;
    @BindView(R.id.activity_register_idcard_iv)
    ImageView activityRegisterIdcardIv;
    @BindView(R.id.tv_title_one)
    TextView tvTitleOne;
    @BindView(R.id.time_line_one)
    View timeLineOne;
    @BindView(R.id.ll_time_line_one)
    LinearLayout llTimeLineOne;
    @BindView(R.id.tv_time_line_two)
    TextView tvTimeLineTwo;
    @BindView(R.id.tv_title_two)
    TextView tvTitleTwo;
    @BindView(R.id.time_line_two)
    View timeLineTwo;
    @BindView(R.id.ll_time_line_two)
    LinearLayout llTimeLineTwo;
    @BindView(R.id.tv_time_line_three)
    TextView tvTimeLineThree;
    @BindView(R.id.tv_title_three)
    TextView tvTitleThree;
    @BindView(R.id.time_line_three)
    View timeLineThree;
    @BindView(R.id.ll_time_line_three)
    LinearLayout llTimeLineThree;
    @BindView(R.id.tv_time_line_four)
    TextView tvTimeLineFour;
    @BindView(R.id.tv_title_four)
    TextView tvTitleFour;
    @BindView(R.id.ll_time_line_four)
    LinearLayout llTimeLineFour;
    @BindView(R.id.btn_detect_new_user_step_2_next)
    Button btnDetectNewUserStep2Next;
    @BindView(R.id.btn_detect_new_user_step_3_previous)
    Button btnDetectNewUserStep3Previous;
    @BindView(R.id.btn_detect_new_user_step_3_next)
    Button btnDetectNewUserStep3Next;
    @BindView(R.id.btn_detect_new_user_step_3_skip)
    Button btnDetectNewUserStep3Skip;
    @BindView(R.id.btn_detect_new_user_step_4_previous)
    Button btnDetectNewUserStep4Previous;
    @BindView(R.id.btn_detect_new_user_step_4_next)
    Button btnDetectNewUserStep4Next;
    @BindView(R.id.vf_register_new_user_infos)
    ViewFlipper vfRegisterNewUserInfos;

    private Handler mHandler = new Handler();
    private Context mContext;


    String mFaceBase64Image = "";

    boolean isUserNameValid = false;
    boolean isUserIdCardValid = true;
    boolean isUserMobileValid = false;
    boolean isUserSocialInsuranceValid = false;

    CountDownTimer timer;

    private BaseDialog dialogRegisteredSucceeded;
    private BaseDialog dialogMessage;
    private BaseDialog dialogChooseOutpatient;

    LoadingDialog loadingProgressBar;
    private String base64Image;
    //根据手机号去库里面查询是否有这个人，如果有这个人的话，在adduser的接口之后，是需要展示结果的ui的，如果没有的话，需要跳入选择科室的列表然后再次进行adduser
    private  boolean isHasMyBody =true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mContext = this;

        initComponents();
        initData();
        initView();
        initListeners();
        mHandler = new InnerHandler(this);

    }

    @OnClick({R.id.common_header_back_layout, R.id.activity_register_telephone_dele_iv, R.id.activity_register_name_iv, R.id.activity_register_idcard_iv, R.id.btn_detect_new_user_step_2_previous, R.id.btn_detect_new_user_step_2_next, R.id.btn_detect_new_user_step_3_previous, R.id.btn_detect_new_user_step_3_next, R.id.btn_detect_new_user_step_3_skip, R.id.btn_detect_new_user_step_4_previous, R.id.btn_detect_new_user_step_4_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.common_header_back_layout://页面刷新
                finish();
            case R.id.activity_register_telephone_dele_iv://页面刷新
                activityRegisterTelephoneEt.setText("");
                activityRegisterTelephoneDeleIv.setVisibility(View.GONE);
                break;
            case R.id.activity_register_name_iv://页面刷新
                activityRegisterNameEt.setText("");
                activityRegisterNameIv.setVisibility(View.GONE);
                break;
            case R.id.activity_register_idcard_iv://页面刷新
                activityRegisterIdcardEt.setText("");
                activityRegisterIdcardIv.setVisibility(View.GONE);
                break;
            case R.id.btn_detect_new_user_step_2_previous:
                break;
            case R.id.btn_detect_new_user_step_2_next://填写患者手机号下一步的操作-----动作重要
                vfRegisterNewUserInfos.setDisplayedChild(1);
                changeTitle();
                break;
            case R.id.btn_detect_new_user_step_3_previous://页面刷新
                vfRegisterNewUserInfos.setDisplayedChild(0);
                changeTitle();
                break;
            case R.id.btn_detect_new_user_step_3_next://页面刷新
                vfRegisterNewUserInfos.setDisplayedChild(2);
                changeTitle();
                break;
            case R.id.btn_detect_new_user_step_3_skip://页面刷新
                registerPhone();
                break;
            case R.id.btn_detect_new_user_step_4_previous://页面刷新
                vfRegisterNewUserInfos.setDisplayedChild(1);
                changeTitle();
                break;
            case R.id.btn_detect_new_user_step_4_next://页面刷新
//                vfRegisterNewUserInfos.setDisplayedChild(3);
                String s = activityRegisterIdcardEt.getText().toString().trim();
                if (!TextUtils.isEmpty(s)) {
                    if ("".equals(TextInfosCheckUtil.IDCardValidate(s.toString()))) {
//                        isUserIdCardValid = true;
//                        setButtonState(btnDetectNewUserStep4Next, true);
                    } else {
//                        isUserIdCardValid = false;
//                        setButtonState(btnDetectNewUserStep4Next, false);
                        Toast.makeText(RegisterPatientActivity.this, R.string.txt_register_id_invalid_error, Toast.LENGTH_SHORT).show();
                    }
                }
                registerPhone();
//                changeTitle();

                break;
//            case R.id.btn_detect_new_user_step_6_done:   //最后一步动作要注册用户了----------动作重要
//
//            default:
//                changeTitle();
//                break;
        }
    }


    /*
     *
     * 填写完手机号之后，去后台注册手机号
     * */
    private void registerPhone() {
        Map<String, String> requestMap = new HashMap<>(2);
        requestMap.put("phoneNumber", activityRegisterTelephoneEt.getText().toString());
        Gson gson = new Gson();
        String jsonStr = gson.toJson(requestMap, HashMap.class);
        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonStr);
        ApiUtil.searchUserByPhoneNumberCall(mContext, requestBody).enqueue(new Callback<FaceDetectResultByPhone>() {
            @Override
            public void onResponse(Call<FaceDetectResultByPhone> call, Response<FaceDetectResultByPhone> response) {
                // Log.i(TAG, "onResponse: "+response.body());
                if (response.isSuccessful()) {
                    FaceDetectResultByPhone responseMessageBean = response.body();
                    int resultStatus = responseMessageBean.getResultStatus();
                    if (resultStatus == 0) {//查到了患者
                        FaceDetectResultByPhone.ResultContent resultContent = responseMessageBean.getResultContent();
                        if (!mFaceBase64Image.isEmpty()) {
                            isHasMyBody = true;
                            mHandler.postDelayed(addUserRunnable, 100);
                        }
                    } else {//没有查到了患者
                        FaceDetectResultByPhone.ResultContent resultContent = new FaceDetectResultByPhone.ResultContent();
                        Intent intentToTimes = new Intent(mContext, SelectPatientTypeActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(BundleKeys.ADDUSERREQUESTBEAN, getEtString());
//                        bundle.putString(BundleKeys.BASE64IMAGE, base64Image);
                        intentToTimes.putExtras(bundle);
                        mContext.startActivity(intentToTimes, bundle);
                    }
                } else {
                    showRegisteredResultDialog("注册超时，请返回重试。");
                }
            }

            @Override
            public void onFailure(Call<FaceDetectResultByPhone> call, Throwable t) {
                // Log.i(TAG, "onFailure: "+t);
                Toast.makeText(RegisterPatientActivity.this,R.string.response_error,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class InnerHandler extends Handler {
        private WeakReference<BaseActivity> mWeakReference;

        public InnerHandler(BaseActivity activity) {
            super();
            this.mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mWeakReference == null || mWeakReference.get() == null) {
                return;
            }
            BaseActivity activity = mWeakReference.get();
            if (activity == null) {
                return;
            }
            if (msg == null) {
                return;

            }
        }
    }

    private void initComponents() {
        dialogChooseOutpatient = new BaseDialog(mContext);
        dialogMessage = new BaseDialog(mContext);
        dialogRegisteredSucceeded = new BaseDialog(mContext);
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
//        Bundle bundle = intent.getBundleExtra("data_from_detect_activity");
        mFaceBase64Image=bundle.getString(BundleKeys.BASE64IMAGE);
//        mFaceBase64Image = bundle.getString("new_user_image", "");
        Log.i("initData", "initData: " + mFaceBase64Image);
    }

    private void initView() {
        vfRegisterNewUserInfos.setInAnimation(this, R.anim.bottom_in);
        vfRegisterNewUserInfos.setOutAnimation(this, R.anim.bottom_out);
        vfRegisterNewUserInfos.setAutoStart(false);
        vfRegisterNewUserInfos.setDisplayedChild(0);
        loadingProgressBar = new LoadingDialog(this,"");
        commonHeaderTitle.setText("建立患者信息");


    }

    private void handleInputEt(String content, EditText input, ImageView iv) {
        if (content == null)
            content = "";
        if (content.length() > 0) {
            iv.setVisibility(View.VISIBLE);
        } else {
            iv.setVisibility(View.GONE);
        }
    }


/*
* 手机号，患者姓名 身份证号  的页面刷新  内容跟删除  的逻辑
* */
    private void initListeners() {

        activityRegisterTelephoneEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                handleInputEt(editable.toString(), activityRegisterTelephoneEt, activityRegisterTelephoneDeleIv);
                if (!TextUtils.isEmpty(editable)) {
                    if (TextInfosCheckUtil.checkMobile(editable.toString())) {
                        isUserMobileValid = true;
                        setButtonState(btnDetectNewUserStep2Next, true);
                    } else {
                        isUserMobileValid = false;
                        setButtonState(btnDetectNewUserStep2Next, false);
                        if (!TextInfosCheckUtil.checkLength(editable.toString(), 10))
                            Toast.makeText(RegisterPatientActivity.this, R.string.txt_register_mobile_error, Toast.LENGTH_SHORT).show();

                    }
                } else {
                    isUserMobileValid = false;
                    setButtonState(btnDetectNewUserStep2Next, false);
                }
            }
        });

        activityRegisterNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                handleInputEt(s.toString(), activityRegisterNameEt, activityRegisterNameIv);
                if (!TextUtils.isEmpty(s)) {
                    isUserNameValid = true;
                    setButtonState(btnDetectNewUserStep3Next, true);
                } else {
                    isUserNameValid = false;
                    setButtonState(btnDetectNewUserStep3Next, false);
                }
            }
        });

        activityRegisterIdcardEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                handleInputEt(s.toString(), activityRegisterIdcardEt, activityRegisterIdcardIv);


                if (!TextUtils.isEmpty(s)) {
                    int length = s.toString().trim().length();
                    if(length == 18){
                        setButtonState(btnDetectNewUserStep4Next, true);
                    }else{
                        setButtonState(btnDetectNewUserStep4Next, false);
                    }
                } else {
                    isUserIdCardValid = true;
                    setButtonState(btnDetectNewUserStep4Next, false);
                }
            }
        });
    }

    private void setButtonState(Button button, boolean isEnabled) {
        button.setEnabled(isEnabled);
        if (isEnabled) {
            button.setBackground(getResources().getDrawable(R.drawable.button_round_shape_enabled));
        } else {
            button.setBackground(getResources().getDrawable(R.drawable.button_round_shape_disabled));
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialogChooseOutpatient = null;
        dialogMessage = null;
        dialogRegisteredSucceeded = null;
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        EventBus.getDefault().unregister(this);

    }

    private void changeTitle() {
        int index = vfRegisterNewUserInfos.getDisplayedChild();
        switch (index) {
            case 0:
                chageTimeLineOffColor(1);
                chageTimeLineOffColor(2);
                chageTimeLineOffColor(3);
                break;
            case 1:
                chageTimeLineOnColor(1);
                chageTimeLineOffColor(2);
                chageTimeLineOffColor(3);
                break;
            case 2:
                chageTimeLineOnColor(1);
                chageTimeLineOnColor(2);
                chageTimeLineOffColor(3);
                break;
            case 3:
                chageTimeLineOnColor(1);
                chageTimeLineOnColor(2);
                chageTimeLineOnColor(3);
                break;
            case 4:
                chageTimeLineOnColor(1);
                chageTimeLineOnColor(2);
                chageTimeLineOnColor(3);
                break;
            default:
                chageTimeLineOffColor(1);
                chageTimeLineOffColor(2);
                chageTimeLineOffColor(3);
                break;
        }
    }

    //把timeline的各项设置为选中色
    private void chageTimeLineOnColor(int index) {
        switch (index) {
            case 1:
                tvTitleTwo.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvTimeLineTwo.setTextColor(getResources().getColor(R.color.colorWhite));
                tvTimeLineTwo.setBackground(getResources().getDrawable(R.drawable.shape_timeline_point_on));
                timeLineTwo.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                break;
            case 2:
                tvTitleThree.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvTimeLineThree.setTextColor(getResources().getColor(R.color.colorWhite));
                tvTimeLineThree.setBackground(getResources().getDrawable(R.drawable.shape_timeline_point_on));
                timeLineThree.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                break;
            case 3:
                tvTitleFour.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvTimeLineFour.setTextColor(getResources().getColor(R.color.colorWhite));
                tvTimeLineFour.setBackground(getResources().getDrawable(R.drawable.shape_timeline_point_on));
                break;
            case 4:
                tvTitleFour.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvTimeLineFour.setTextColor(getResources().getColor(R.color.colorWhite));
                tvTimeLineFour.setBackground(getResources().getDrawable(R.drawable.shape_timeline_point_on));
                break;
            default:
                break;
        }
    }

    //把时间轴的线的各项设置为非选中色
    private void chageTimeLineOffColor(int index) {
        switch (index) {
            case 1:
                tvTitleTwo.setTextColor(getResources().getColor(R.color.color_time_line_point_off));
                tvTimeLineTwo.setTextColor(getResources().getColor(R.color.color_time_line_point_off));
                tvTimeLineTwo.setBackground(getResources().getDrawable(R.drawable.shape_timeline_point_off));
                timeLineTwo.setBackgroundColor(getResources().getColor(R.color.color_time_line_point_off));
                break;
            case 2:
                tvTitleThree.setTextColor(getResources().getColor(R.color.color_time_line_point_off));
                tvTimeLineThree.setTextColor(getResources().getColor(R.color.color_time_line_point_off));
                tvTimeLineThree.setBackground(getResources().getDrawable(R.drawable.shape_timeline_point_off));
                timeLineThree.setBackgroundColor(getResources().getColor(R.color.color_time_line_point_off));
                break;
            case 3:
                tvTitleFour.setTextColor(getResources().getColor(R.color.color_time_line_point_off));
                tvTimeLineFour.setTextColor(getResources().getColor(R.color.color_time_line_point_off));
                tvTimeLineFour.setBackground(getResources().getDrawable(R.drawable.shape_timeline_point_off));
                break;
            case 4:
                tvTitleFour.setTextColor(getResources().getColor(R.color.color_time_line_point_off));
                tvTimeLineFour.setTextColor(getResources().getColor(R.color.color_time_line_point_off));
                tvTimeLineFour.setBackground(getResources().getDrawable(R.drawable.shape_timeline_point_off));
                break;
            default:
                break;
        }

    }

    Runnable addUserRunnable = new Runnable() {
        @Override
        public void run() {
            // Log.i("addUserRunnable", "run: base64Image = " + mFaceBase64Image);
            loadingProgressBar.show();


            Gson gson = new Gson();
            String jsonStr = gson.toJson(getEtString(), AddUserRequestBean.class);

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonStr);

            ApiUtil.addUserCall(mContext, requestBody).enqueue(new Callback<ResponseMessageBean>() {
                @Override
                public void onResponse(Call<ResponseMessageBean> call, Response<ResponseMessageBean> response) {
                    // Log.i("addUserRunnable", "onResponse: "+ response.body());
                    loadingProgressBar.hide();
                    if (response.isSuccessful()) {
                        ResponseMessageBean responseMessageBean = response.body();
                        if(isHasMyBody)
                        handleResult(responseMessageBean);
                    } else {
//                        showRegisteredResultDialog("注册超时，请返回重试。");
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessageBean> call, Throwable t) {
                    loadingProgressBar.hide();
                    Toast.makeText(RegisterPatientActivity.this,R.string.response_error,Toast.LENGTH_SHORT).show();
                    // Log.i("addUserRunnable", "onFailure: " + t.toString());
//                    showRegisteredResultDialog("注册超时，请返回重试。");
                }
            });

        }
    };
    private void handleResult(ResponseMessageBean responseMessage) {
        Intent intent = new Intent(this,RegisterResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BundleKeys.APPOINTMENTSBEAN,responseMessage.getResultContent());

        switch (responseMessage.getResultStatus()) {
            case Constants.FACE_RESPONSE_CODE_SUCCESS://识别成功，直接打印 0

                bundle.putInt(BundleKeys.REGISTER_RESULT_STATUS, ConstantArguments.REGISTER_SUCESS);
                break;

            case Constants.FACE_RESPONSE_CODE_ERROR_SEARCH_USER_NOT_FOUND://跳转添加新用户，直接打印 1001

                break;

            case Constants.FACE_RESPONSE_CODE_ERROR_SEARCH_USER_FOUND_NOT_MATCH://重新扫脸  1002 1003  3001
            case Constants.FACE_RESPONSE_CODE_ERROR_SEARCH_OTHER_ERRORS:
            case Constants.FACE_RESPONSE_CODE_ERROR_DETECT_USER_FACE_INVALID:


                break;
            case Constants.FACE_RESPONSE_CODE_ERROR_ADD_USER_USER_NOT_EXIST://添加用户失败 2001 2002
            case Constants.FACE_RESPONSE_CODE_ERROR_ADD_USER_OTHER_ERRORS:
                bundle.putInt(BundleKeys.REGISTER_RESULT_STATUS, ConstantArguments.REGISTER_FAILED);
                break;
            case Constants.FACE_RESPONSE_CODE_ERROR_NEED_CONTACT_CDE://4002签到错误，请联系照护师
            case Constants.FACE_RESPONSE_CODE_ERROR_SHOULD_CHECK_CERTAIN_DAY://4003签到错误，共同照护患者不在当
            case Constants.FACE_RESPONSE_CODE_ERROR_OTHER_REASONS://4004其他签到错误类型，直接提示，联系照护师
                bundle.putInt(BundleKeys.REGISTER_RESULT_STATUS, ConstantArguments.REGISTER_FAILED_ADD_CLINIC);
                break;
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }
    private AddUserRequestBean getEtString(){
        String nickname = activityRegisterNameEt.getText().toString();
        String phoneNumber = activityRegisterTelephoneEt.getText().toString();
        String idCard = activityRegisterIdcardEt.getText().toString();

        AddUserRequestBean addUserRequestBean = new AddUserRequestBean();
        addUserRequestBean.setBase64Image(mFaceBase64Image);
        addUserRequestBean.setUserInfo(new UserInfo(phoneNumber, nickname, idCard));
        addUserRequestBean.setHospitalId(
                SharedPreferenceUtil.getStringTypeSharedPreference(mContext, Constants.SP_NAME_HOSPITAL_INFOS, Constants.SP_KEY_HOSPITAL_GROUP_ID)
        );
        return addUserRequestBean;
    }
    /**
     * 展示对话框
     *
     * @param
     */
    private void showRegisteredResultDialog(String dialogContent) {
        if (null == dialogRegisteredSucceeded) {
            return;
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_dialog_register_success, null);

        final TextView tvDialogContent = (TextView) view.findViewById(R.id.tv_dialog_content);
        tvDialogContent.setText(dialogContent);

        (view.findViewById(R.id.btn_dialog_back_immediately)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogRegisteredSucceeded.dismiss();
                Activity activity = (Activity) mContext;
                activity.finish();
            }
        });

        final TextView tvDialogBackCounter = (TextView) view.findViewById(R.id.btn_dialog_counter_back);
        timer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvDialogBackCounter.setText((millisUntilFinished / 1000 + 1) + " 秒后自动返回");
            }

            @Override
            public void onFinish() {
                dialogRegisteredSucceeded.dismiss();
                Activity activity = (Activity) mContext;
                activity.finish();
            }
        }.start();

        dialogRegisteredSucceeded.setContentView(view);
        dialogRegisteredSucceeded.setCancelable(false);
        if (null != dialogRegisteredSucceeded && !dialogRegisteredSucceeded.isShowing()) {
            dialogRegisteredSucceeded.show();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FinshDetectRegisterAndResultEvent event){
        finish();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FinshDetectRegisterSelectTypeAndResultEvent event){
        finish();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FinshRegisterAndResultEvent event){
        finish();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FinshRegisterSelectTypeAndResultEvent event){
        finish();
    }
}


