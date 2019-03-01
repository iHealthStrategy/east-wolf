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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.ihealth.retrofit.Constants;
import com.ihealth.utils.SharedPreferenceUtil;
import com.ihealth.utils.TextInfosCheckUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

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

    private static final String TAG = "RegisterActivity";
    private static final String MSG_UPDATE = "RegisterActivity";

    private Handler mHandler = new Handler();
    private Context mContext;

    TextView tvRegisterHospitalTitle;
    TextView tvRegisterHeader;
    TextView tvRegisterTitle;
    ViewFlipper vfNewUserInfos;

    ImageView ivRegisterHome;

    Button btnNewUserStep2Previous;
    Button btnNewUserStep2Next;

    Button btnNewUserStep3Previous;
    Button btnNewUserStep3Skip;
    Button btnNewUserStep3Next;

    Button btnNewUserStep4Previous;
    Button btnNewUserStep4Done;

    TextInputEditText etvNewUserName;
    TextInputEditText etvNewUserIdCard;
    TextInputEditText etvNewUserMobile;

    TextInputLayout layoutNewUserName;
    TextInputLayout layoutNewUserIdCard;
    TextInputLayout layoutNewUserMobile;

    String mFaceBase64Image = "";

    boolean isUserNameValid = false;
    boolean isUserIdCardValid = true;
    boolean isUserMobileValid = false;

    CountDownTimer timer;

    private BaseDialog dialogRegisteredSucceeded;
    private BaseDialog dialogMessage;
    private BaseDialog dialogChooseOutpatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mContext = this;

        initComponents();
        initData();
        initView();
        initListeners();

        mHandler = new InnerHandler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        }
    }

    private void initComponents(){
        dialogChooseOutpatient = new BaseDialog(mContext);
        dialogMessage = new BaseDialog(mContext);
        dialogRegisteredSucceeded = new BaseDialog(mContext);
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data_from_detect_activity");
        mFaceBase64Image = bundle.getString("new_user_image", "");
        // Log.i("initData", "initData: " + mFaceBase64Image);
    }

    private void initView() {

        tvRegisterHospitalTitle = (TextView) findViewById(R.id.tv_register_hospital_title);
        tvRegisterHospitalTitle.setText(
                SharedPreferenceUtil.getStringTypeSharedPreference(mContext, Constants.SP_NAME_HOSPITAL_INFOS, Constants.SP_KEY_HOSPITAL_FULL_NAME)
                        + "-内分泌科"
        );

        tvRegisterHeader = (TextView) findViewById(R.id.tv_register_header);
        tvRegisterTitle = (TextView) findViewById(R.id.tv_register_title);

        vfNewUserInfos = (ViewFlipper) findViewById(R.id.vf_register_new_user_infos);
        vfNewUserInfos.setInAnimation(this, R.anim.bottom_in);
        vfNewUserInfos.setOutAnimation(this, R.anim.bottom_out);
        vfNewUserInfos.setAutoStart(false);
        vfNewUserInfos.setDisplayedChild(0);

        ivRegisterHome = (ImageView) findViewById(R.id.iv_register_exit);

        btnNewUserStep2Previous = (Button) findViewById(R.id.btn_detect_new_user_step_2_previous);
        btnNewUserStep2Next = (Button) findViewById(R.id.btn_detect_new_user_step_2_next);

        btnNewUserStep3Previous = (Button) findViewById(R.id.btn_detect_new_user_step_3_previous);
        btnNewUserStep3Skip = (Button) findViewById(R.id.btn_detect_new_user_step_3_skip);
        btnNewUserStep3Next = (Button) findViewById(R.id.btn_detect_new_user_step_3_next);

        btnNewUserStep4Previous = (Button) findViewById(R.id.btn_detect_new_user_step_4_previous);
        btnNewUserStep4Done = (Button) findViewById(R.id.btn_detect_new_user_step_4_done);

        etvNewUserName = (TextInputEditText) findViewById(R.id.etv_register_name);
        etvNewUserIdCard = (TextInputEditText) findViewById(R.id.etv_register_id_card);
        etvNewUserMobile = (TextInputEditText) findViewById(R.id.etv_register_mobile);

        layoutNewUserName = (TextInputLayout) findViewById(R.id.layout_register_name);
        layoutNewUserIdCard = (TextInputLayout) findViewById(R.id.layout_register_id_card);
        layoutNewUserMobile = (TextInputLayout) findViewById(R.id.layout_register_mobile);
    }

    private void initListeners() {
        ivRegisterHome.setOnClickListener(this);
        btnNewUserStep2Next.setOnClickListener(this);
        btnNewUserStep2Previous.setOnClickListener(this);
        btnNewUserStep2Next.setOnClickListener(this);
        btnNewUserStep3Previous.setOnClickListener(this);
        btnNewUserStep3Skip.setOnClickListener(this);
        btnNewUserStep3Next.setOnClickListener(this);
        btnNewUserStep4Previous.setOnClickListener(this);
        btnNewUserStep4Done.setOnClickListener(this);

        etvNewUserMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (TextInfosCheckUtil.checkMobile(s.toString())) {
                        isUserMobileValid = true;
                        setButtonState(btnNewUserStep2Next, true);
                        layoutNewUserMobile.setErrorEnabled(false);
                    } else {
                        isUserMobileValid = false;
                        setButtonState(btnNewUserStep2Next, false);
                        layoutNewUserMobile.setError("手机号格式不正确！");
                        layoutNewUserMobile.setErrorEnabled(true);
                    }
                } else {
                    isUserMobileValid = false;
                    setButtonState(btnNewUserStep2Next, false);
                    layoutNewUserMobile.setError("手机号不能为空！");
                    layoutNewUserMobile.setErrorEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etvNewUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    isUserNameValid = true;
                    setButtonState(btnNewUserStep3Next, true);
                    layoutNewUserName.setErrorEnabled(false);
                } else {
                    isUserNameValid = false;
                    setButtonState(btnNewUserStep3Next, false);
                    layoutNewUserName.setError("姓名不能为空！");
                    layoutNewUserName.setErrorEnabled(true);
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
                if (!TextUtils.isEmpty(s)) {
                    if ("".equals(TextInfosCheckUtil.IDCardValidate(s.toString()))) {
                        isUserIdCardValid = true;
                        setButtonState(btnNewUserStep4Done, true);
                        layoutNewUserIdCard.setErrorEnabled(false);
                    } else {
                        isUserIdCardValid = false;
                        setButtonState(btnNewUserStep4Done, false);
                        layoutNewUserIdCard.setError("身份证号无效！请核对！");
                        layoutNewUserIdCard.setErrorEnabled(true);
                    }
                } else {
                    isUserIdCardValid = true;
                    setButtonState(btnNewUserStep4Done, false);
                    layoutNewUserIdCard.setError("身份证号不能为空！");
                    layoutNewUserIdCard.setErrorEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
    protected void onStop() {
        super.onStop();
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
    }

    private void changeTitle() {
        int index = vfNewUserInfos.getDisplayedChild();
        switch (index) {
            case 0:
                tvRegisterHeader.setText("建立患者信息（1/3）");
                tvRegisterTitle.setText("患者手机号");
                break;
            case 1:
                tvRegisterHeader.setText("建立患者信息（2/3）");
                tvRegisterTitle.setText("患者姓名");
                break;
            case 2:
                tvRegisterHeader.setText("建立患者信息（3/3）");
                tvRegisterTitle.setText("患者身份证号");
                break;
            default:
                tvRegisterHeader.setText("建立患者信息");
                tvRegisterTitle.setText("患者信息");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_register_exit:
                this.finish();
                break;
            case R.id.btn_detect_new_user_step_2_next:

                Map<String, String> requestMap = new HashMap<>(2);
                requestMap.put("phoneNumber", etvNewUserMobile.getText().toString());
                Gson gson = new Gson();
                String jsonStr = gson.toJson(requestMap, HashMap.class);
                final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonStr);
                ApiUtil.searchUserByPhoneNumberCall(mContext, requestBody).enqueue(new Callback<ResponseMessageBean>() {
                    @Override
                    public void onResponse(Call<ResponseMessageBean> call, Response<ResponseMessageBean> response) {
                        // Log.i(TAG, "onResponse: "+response.body());
                        if (response.isSuccessful()) {
                            ResponseMessageBean responseMessageBean = response.body();
                            int resultStatus = responseMessageBean.getResultStatus();
                            if (resultStatus == 0) {
                                ResponseMessageBean.resultContent resultContent = responseMessageBean.getResultContent();
                                String phoneNumber = resultContent.getPhoneNumber();
                                String nickname = resultContent.getNickname();
                                String idCard = resultContent.getIdCard();
                                showCommonDialog(phoneNumber, nickname, idCard);
                            } else {
                                // 说明是新患者，直接下一步，建立患者信息
                                vfNewUserInfos.setDisplayedChild(1);
                                changeTitle();
                            }
                        } else {
                            showRegisteredResultDialog("很抱歉，签到失败。\n请返回重试。");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseMessageBean> call, Throwable t) {
                        // Log.i(TAG, "onFailure: "+t);
                    }
                });
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

    Runnable addUserRunnable = new Runnable() {
        @Override
        public void run() {
            // Log.i("addUserRunnable", "run: base64Image = " + mFaceBase64Image);

            String nickname = etvNewUserName.getText().toString();
            String phoneNumber = etvNewUserMobile.getText().toString();
            String idCard = etvNewUserIdCard.getText().toString();

            AddUserRequestBean addUserRequestBean = new AddUserRequestBean();
            addUserRequestBean.setBase64Image(mFaceBase64Image);
            addUserRequestBean.setUserInfo(new UserInfo(phoneNumber, nickname, idCard));
            addUserRequestBean.setHospitalId(
                    SharedPreferenceUtil.getStringTypeSharedPreference(mContext, Constants.SP_NAME_HOSPITAL_INFOS, Constants.SP_KEY_HOSPITAL_GROUP_ID)
            );

            Gson gson = new Gson();
            String jsonStr = gson.toJson(addUserRequestBean, AddUserRequestBean.class);

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonStr);

            ApiUtil.addUserCall(mContext, requestBody).enqueue(new Callback<ResponseMessageBean>() {
                @Override
                public void onResponse(Call<ResponseMessageBean> call, Response<ResponseMessageBean> response) {
                    // Log.i("addUserRunnable", "onResponse: "+ response.body());
                    if (response.isSuccessful()) {
                        ResponseMessageBean responseMessageBean = response.body();
                        tackleWithResponds(responseMessageBean);
                    } else {
                        showRegisteredResultDialog("很抱歉，签到失败。\n请返回重试。");
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessageBean> call, Throwable t) {
                    // Log.i("addUserRunnable", "onFailure: " + t.toString());
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
        if(null==dialogRegisteredSucceeded){
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
        if (null!=dialogRegisteredSucceeded && !dialogRegisteredSucceeded.isShowing()){
            dialogRegisteredSucceeded.show();
        }
    }

    /**
     * 展示对话框
     *
     * @param
     */
    private void showCommonDialog(final String phoneNumber, final String nickname, final String idCard) {
        if(null==dialogMessage){
            return;
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_dialog_common, null);

        final ImageView ivCloseDialog = (ImageView) view.findViewById(R.id.iv_dialog_close);
        ivCloseDialog.setVisibility(View.VISIBLE);
        ivCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMessage.dismiss();
            }
        });

        final TextView tvDialogContent = (TextView) view.findViewById(R.id.tv_common_dialog_content);
        tvDialogContent.setText(
                "请确认如下信息是否正确：\n"
                        + "手机号：" + (TextUtils.isEmpty(phoneNumber) ? "--" : (phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7, 11))) + "\n"
                        + "姓名：" + (TextUtils.isEmpty(nickname) ? "--" : nickname) + "\n"
                        + "身份证号：" + (TextUtils.isEmpty(idCard) ? "--" : (idCard.substring(0, 6) + "********" + idCard.substring(idCard.length() - 4)))
        );

        final TextView tvDialogOk = (TextView) view.findViewById(R.id.btn_dialog_ok);
        tvDialogOk.setText("正确无误");
        tvDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMessage.dismiss();

                // 先把已有信息设定到对应控件上
                etvNewUserName.setText(TextUtils.isEmpty(nickname) ? "" : nickname);
                etvNewUserIdCard.setText(TextUtils.isEmpty(idCard) ? "" : idCard);

                // 如果信息全部完整，那么进行签到
                // 否则需要填写缺失信息
                if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(nickname) && !TextUtils.isEmpty(idCard)) {
                    mHandler.postDelayed(addUserRunnable, 100);
                } else if (TextUtils.isEmpty(nickname)) {
                    vfNewUserInfos.setDisplayedChild(1);
                    changeTitle();
                } else {
                    vfNewUserInfos.setDisplayedChild(2);
                    changeTitle();
                }
            }
        });

        final TextView tvDialogCancel = (TextView) view.findViewById(R.id.btn_dialog_cancel);
        tvDialogCancel.setText("不是我");
        tvDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMessage.dismiss();
                layoutNewUserMobile.setError("请输入您的手机号!");
                layoutNewUserMobile.setErrorEnabled(true);
            }
        });

        dialogMessage.setContentView(view);
        dialogMessage.setCancelable(false);
        if ( null!=dialogMessage && !dialogMessage.isShowing()){
            dialogMessage.show();
        }
    }

    /**
     * 展示共同照护患者选择看诊门诊类型对话框
     *
     * @param
     */
    private void showChooseOutpatientDialog(final String patientId) {
        if(null==dialogChooseOutpatient){
            return;
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_dialog_choose_outpatient, null);

        final ImageView ivCloseDialog = (ImageView) view.findViewById(R.id.iv_dialog_close);
        ivCloseDialog.setVisibility(View.GONE);

        final LinearLayout llChooseHealthCareTeam = (LinearLayout) view.findViewById(R.id.ll_dialog_health_care_team);
        final LinearLayout llChooseOrdinaryOutpatient = (LinearLayout) view.findViewById(R.id.ll_dialog_ordinary_outpatients);
        final RelativeLayout rlChooseHealthCareTeam = (RelativeLayout) view.findViewById(R.id.rl_dialog_health_care_team);
        final RelativeLayout rlChooseOrdinaryOutpatient = (RelativeLayout) view.findViewById(R.id.rl_dialog_ordinary_outpatients);
        final ImageButton ibtChooseHealthCareTeam = (ImageButton) view.findViewById(R.id.ibt_dialog_health_care_team);
        final ImageButton ibtChooseOrdinaryOutpatient = (ImageButton) view.findViewById(R.id.ibt_dialog_ordinary_outpatients);

        View.OnClickListener onChooseHealthCareTeamClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChooseOutpatient.dismiss();
                // checkInOnHealthCareTeamAttendanceState(patientId, true);
                showRegisteredResultDialog("签到失败。请您联系照护师改期或进行其他操作，谢谢。");
            }
        };

        View.OnClickListener onChooseOrdinaryOutpatientClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChooseOutpatient.dismiss();
                checkInOnHealthCareTeamAttendanceState(patientId, false);
            }
        };

        llChooseHealthCareTeam.setOnClickListener(onChooseHealthCareTeamClickListener);
        rlChooseHealthCareTeam.setOnClickListener(onChooseHealthCareTeamClickListener);
        ibtChooseHealthCareTeam.setOnClickListener(onChooseHealthCareTeamClickListener);
        llChooseOrdinaryOutpatient.setOnClickListener(onChooseOrdinaryOutpatientClickListener);
        rlChooseOrdinaryOutpatient.setOnClickListener(onChooseOrdinaryOutpatientClickListener);
        ibtChooseOrdinaryOutpatient.setOnClickListener(onChooseOrdinaryOutpatientClickListener);

        dialogChooseOutpatient.setContentView(view);
        dialogChooseOutpatient.setCancelable(false);
        if (null!=dialogChooseOutpatient && !dialogChooseOutpatient.isShowing()){
            dialogChooseOutpatient.show();
        }
    }

    private void checkInOnHealthCareTeamAttendanceState(String patientId, boolean hasAttendedHealthCareTeam){
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("patientId", patientId);
        requestMap.put("hospitalId",
                SharedPreferenceUtil.getStringTypeSharedPreference(mContext, Constants.SP_NAME_HOSPITAL_INFOS, Constants.SP_KEY_HOSPITAL_GROUP_ID)
        );
        requestMap.put("hasHealthCare", hasAttendedHealthCareTeam);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(requestMap, HashMap.class);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonStr);
        ApiUtil.checkInWithConditionCall(mContext, requestBody).enqueue(new Callback<ResponseMessageBean>() {
            @Override
            public void onResponse(Call<ResponseMessageBean> call, Response<ResponseMessageBean> response) {
                // Log.i("checkInWithConditionCall", "onResponse: "+response.body());
                tackleWithResponds(response.body());
            }

            @Override
            public void onFailure(Call<ResponseMessageBean> call, Throwable t) {
                // Log.i("checkInWithConditionCall", "onFailure: "+t);
            }
        });
    }

    private void tackleWithResponds(ResponseMessageBean responseMessageBean){
        int resultStatus = responseMessageBean.getResultStatus();
        String resultMessage = responseMessageBean.getResultMessage();
        String dialogContents = "";
        switch (resultStatus) {
            case Constants.FACE_RESPONSE_CODE_SUCCESS:
                dialogContents = "恭喜您！签到成功！";
                showRegisteredResultDialog(dialogContents);
                break;

            case Constants.FACE_RESPONSE_CODE_ERROR_ALREADY_SIGNED_IN:
                dialogContents = "您已签到，无需重复签到。请就诊，谢谢。";
                showRegisteredResultDialog(dialogContents);
                break;

            case Constants.FACE_RESPONSE_CODE_ERROR_NEED_CONTACT_CDE:
                dialogContents = "签到失败：" + resultMessage + "。\n请联系照护师，谢谢。";
                showRegisteredResultDialog(dialogContents);
                break;

            case Constants.FACE_RESPONSE_CODE_ERROR_SHOULD_CHECK_CERTAIN_DAY:
                showChooseOutpatientDialog(responseMessageBean.getResultContent().getUserId());
                break;

            case Constants.FACE_RESPONSE_CODE_ERROR_OTHER_REASONS:
                dialogContents = resultMessage + "。\n请联系照护师，谢谢。";
                showRegisteredResultDialog(dialogContents);
                break;

            default:
                break;
        }
    }

}
