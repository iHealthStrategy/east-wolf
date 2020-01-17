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
import com.ihealth.bean.ResponseMessageBean;
import com.ihealth.bean.UserInfo;
import com.ihealth.facecheckinapp.R;
import com.ihealth.retrofit.ApiUtil;
import com.ihealth.retrofit.Constants;
import com.ihealth.utils.LoadingProgressBar;
import com.ihealth.utils.SharedPreferenceUtil;
import com.ihealth.utils.TextInfosCheckUtil;

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
 * 新用户注册
 *
 * @author liyanwen
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {

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

    private Handler mHandler = new Handler();
    private Context mContext;

    TextView tvRegisterHeader;
    TextView tvRegisterTitle;
    ViewFlipper vfNewUserInfos;
    LinearLayout ivRegisterHome;

    Button btnNewUserStep2Previous;
    Button btnNewUserStep2Next;

    Button btnNewUserStep3Previous;
    Button btnNewUserStep3Skip;
    Button btnNewUserStep3Next;

    Button btnNewUserStep4Previous;
    Button btnNewUserStep4Next;

    Button btnNewUserStep5Previous;
    Button btnNewUserStep5Skip;
    Button btnNewUserStep5Next;

    Button btnNewUserStep6Previous;
    Button btnNewUserStep6Done;

    TextInputEditText etvNewUserName;
    TextInputEditText etvNewUserIdCard;
    TextInputEditText etvNewUserMobile;
    TextInputEditText etvNewUserSocialInsurance;
    TextInputEditText etvNewUserSocialInsuranceConfirm;

    TextInputLayout layoutNewUserName;
    TextInputLayout layoutNewUserIdCard;
    TextInputLayout layoutNewUserMobile;
    TextInputLayout layoutNewUserSocialInsurance;
    TextInputLayout layoutNewUserSocialInsuranceConfirm;

    TextView tvTimeLine1, tvTimeLine2, tvTimeLine3, tvTimeLine4;
    TextView tvTitle1, tvTitle2, tvTitle3, tvTitle4;
    View lineView1, lineView2, lineView3;


    String mFaceBase64Image = "";

    boolean isUserNameValid = false;
    boolean isUserIdCardValid = true;
    boolean isUserMobileValid = false;
    boolean isUserSocialInsuranceValid = false;

    CountDownTimer timer;

    private BaseDialog dialogRegisteredSucceeded;
    private BaseDialog dialogMessage;
    private BaseDialog dialogChooseOutpatient;

    LoadingProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

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

    @OnClick(R.id.common_header_back_layout)
    public void onViewClicked() {
        finish();
    }

    @OnClick({R.id.activity_register_telephone_dele_iv, R.id.activity_register_name_iv, R.id.activity_register_idcard_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.activity_register_telephone_dele_iv:
                activityRegisterTelephoneEt.setText("");
                activityRegisterTelephoneDeleIv.setVisibility(View.GONE);
                break;
            case R.id.activity_register_name_iv:
                activityRegisterNameEt.setText("");
                activityRegisterNameIv.setVisibility(View.GONE);
                break;
            case R.id.activity_register_idcard_iv:
                activityRegisterIdcardEt.setText("");
                activityRegisterIdcardIv.setVisibility(View.GONE);
                break;
        }
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

    private void initComponents() {
        dialogChooseOutpatient = new BaseDialog(mContext);
        dialogMessage = new BaseDialog(mContext);
        dialogRegisteredSucceeded = new BaseDialog(mContext);
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data_from_detect_activity");
//        mFaceBase64Image = bundle.getString("new_user_image", "");
        Log.i("initData", "initData: " + mFaceBase64Image);
    }

    private void initView() {


        vfNewUserInfos = (ViewFlipper) findViewById(R.id.vf_register_new_user_infos);
        vfNewUserInfos.setInAnimation(this, R.anim.bottom_in);
        vfNewUserInfos.setOutAnimation(this, R.anim.bottom_out);
        vfNewUserInfos.setAutoStart(false);
        vfNewUserInfos.setDisplayedChild(0);

        ivRegisterHome = (LinearLayout) findViewById(R.id.close_ll);

        btnNewUserStep2Previous = (Button) findViewById(R.id.btn_detect_new_user_step_2_previous);
        btnNewUserStep2Next = (Button) findViewById(R.id.btn_detect_new_user_step_2_next);

        btnNewUserStep3Previous = (Button) findViewById(R.id.btn_detect_new_user_step_3_previous);
        btnNewUserStep3Skip = (Button) findViewById(R.id.btn_detect_new_user_step_3_skip);
        btnNewUserStep3Next = (Button) findViewById(R.id.btn_detect_new_user_step_3_next);

        btnNewUserStep4Previous = (Button) findViewById(R.id.btn_detect_new_user_step_4_previous);
        btnNewUserStep4Next = (Button) findViewById(R.id.btn_detect_new_user_step_4_next);

        tvTimeLine1 = findViewById(R.id.tv_time_line_one);
        tvTimeLine2 = findViewById(R.id.tv_time_line_two);
        tvTimeLine3 = findViewById(R.id.tv_time_line_three);
        tvTimeLine4 = findViewById(R.id.tv_time_line_four);
        lineView1 = findViewById(R.id.time_line_one);
        lineView2 = findViewById(R.id.time_line_two);
        lineView3 = findViewById(R.id.time_line_three);
        tvTitle1 = findViewById(R.id.tv_title_one);
        tvTitle2 = findViewById(R.id.tv_title_two);
        tvTitle3 = findViewById(R.id.tv_title_three);
        tvTitle4 = findViewById(R.id.tv_title_four);

        loadingProgressBar = new LoadingProgressBar(RegisterActivity.this);

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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        String temp = "";
        if(motionEvent.getAction() == MotionEvent.ACTION_UP){
            if (view instanceof Button) {
                Button tempBtn = (Button) view;
                if (!tempBtn.isEnabled()) {
                    switch (view.getId()) {
                        case R.id.btn_detect_new_user_step_2_next:
                            temp = "请输入正确手机号！";
                            break;
                    }
                    Toast.makeText(RegisterActivity.this,temp,Toast.LENGTH_SHORT).show();
                }
            }
        }


        return false;
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
        btnNewUserStep4Next.setOnClickListener(this);
        btnNewUserStep5Next.setOnClickListener(this);
        btnNewUserStep5Previous.setOnClickListener(this);
        btnNewUserStep5Skip.setOnClickListener(this);
        btnNewUserStep6Previous.setOnClickListener(this);
        btnNewUserStep6Done.setOnClickListener(this);
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
                        setButtonState(btnNewUserStep2Next, true);
                        layoutNewUserMobile.setErrorEnabled(false);
                    } else {
                        isUserMobileValid = false;
                        setButtonState(btnNewUserStep2Next, false);
                        if(TextInfosCheckUtil.checkLength(editable.toString(),11))
                        Toast.makeText(RegisterActivity.this,R.string.txt_register_mobile_error,Toast.LENGTH_SHORT).show();

                        layoutNewUserMobile.setErrorEnabled(true);
                    }
                } else {
                    isUserMobileValid = false;
                    setButtonState(btnNewUserStep2Next, false);
//                    layotNewUserMobile.setError(getResources().getString(R.string.txt_register_mobile_cannot_blank));
                    layoutNewUserMobile.setErrorEnabled(true);
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
                    setButtonState(btnNewUserStep3Next, true);
                    layoutNewUserName.setErrorEnabled(false);
                } else {
                    isUserNameValid = false;
                    setButtonState(btnNewUserStep3Next, false);
                    layoutNewUserName.setError(getResources().getString(R.string.txt_register_name_cannot_blank));
                    layoutNewUserName.setErrorEnabled(true);
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
                    if ("".equals(TextInfosCheckUtil.IDCardValidate(s.toString()))) {
                        isUserIdCardValid = true;
                        setButtonState(btnNewUserStep4Next, true);
                        layoutNewUserIdCard.setErrorEnabled(false);
                    } else {
                        isUserIdCardValid = false;
                        setButtonState(btnNewUserStep4Next, false);
                        Toast.makeText(RegisterActivity.this,R.string.txt_register_id_invalid_error,Toast.LENGTH_SHORT).show();
                        layoutNewUserIdCard.setErrorEnabled(true);
                    }
                } else {
                    isUserIdCardValid = true;
                    setButtonState(btnNewUserStep4Next, false);
                    layoutNewUserIdCard.setError(getResources().getString(R.string.txt_register_id_cannot_blank));
                    layoutNewUserIdCard.setErrorEnabled(false);
                }
            }
        });

        etvNewUserSocialInsurance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    setButtonState(btnNewUserStep5Next, true);
                    layoutNewUserSocialInsurance.setErrorEnabled(false);
                    if (etvNewUserSocialInsuranceConfirm.getText().toString().equals(s.toString())) {
                        isUserSocialInsuranceValid = true;
                        setButtonState(btnNewUserStep6Done, true);
                        layoutNewUserSocialInsuranceConfirm.setErrorEnabled(false);
                    } else {
                        isUserSocialInsuranceValid = false;
                        setButtonState(btnNewUserStep6Done, false);
                        layoutNewUserSocialInsuranceConfirm.setError(getResources().getString(R.string.txt_register_social_insurance_mismatch));
                        layoutNewUserSocialInsuranceConfirm.setErrorEnabled(true);
                    }
                } else {
                    isUserSocialInsuranceValid = false;
                    setButtonState(btnNewUserStep5Next, false);
                    layoutNewUserSocialInsurance.setError(getResources().getString(R.string.txt_register_social_insurance_cannot_blank));
                    layoutNewUserSocialInsurance.setErrorEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etvNewUserSocialInsuranceConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (etvNewUserSocialInsurance.getText().toString().equals(s.toString())) {
                        isUserSocialInsuranceValid = true;
                        setButtonState(btnNewUserStep6Done, true);
                        layoutNewUserSocialInsuranceConfirm.setErrorEnabled(false);
                    } else {
                        isUserSocialInsuranceValid = false;
                        setButtonState(btnNewUserStep6Done, false);
                        layoutNewUserSocialInsuranceConfirm.setError(getResources().getString(R.string.txt_register_social_insurance_mismatch));
                        layoutNewUserSocialInsuranceConfirm.setErrorEnabled(true);
                    }
                } else {
                    isUserSocialInsuranceValid = false;
                    setButtonState(btnNewUserStep6Done, false);
                    layoutNewUserSocialInsuranceConfirm.setError(getResources().getString(R.string.txt_register_social_insurance_mismatch));
                    layoutNewUserSocialInsuranceConfirm.setErrorEnabled(true);
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
                chageTimeLineOffColor(1);
                chageTimeLineOffColor(2);
                chageTimeLineOffColor(3);
                tvRegisterHeader.setText("建立患者信息（1/5）");
                tvRegisterTitle.setText("患者手机号");
                break;
            case 1:
                chageTimeLineOnColor(1);
                chageTimeLineOffColor(2);
                chageTimeLineOffColor(3);
                tvRegisterHeader.setText("建立患者信息（2/5）");
                tvRegisterTitle.setText("患者姓名");
                break;
            case 2:
                chageTimeLineOnColor(1);
                chageTimeLineOnColor(2);
                chageTimeLineOffColor(3);
                tvRegisterHeader.setText("建立患者信息（3/5）");
                tvRegisterTitle.setText("居民身份证号");
                break;
            case 3:
                chageTimeLineOnColor(1);
                chageTimeLineOnColor(2);
                chageTimeLineOnColor(3);
                tvRegisterHeader.setText("建立患者信息（4/5）");
                tvRegisterTitle.setText("社会保障卡号");
                break;
            case 4:
                chageTimeLineOnColor(1);
                chageTimeLineOnColor(2);
                chageTimeLineOnColor(3);
                tvRegisterHeader.setText("建立患者信息（5/5）");
                tvRegisterTitle.setText("社会保障卡号");
                break;
            default:
                chageTimeLineOffColor(1);
                chageTimeLineOffColor(2);
                chageTimeLineOffColor(3);
                tvRegisterHeader.setText("建立患者信息");
                tvRegisterTitle.setText("患者信息");
                break;
        }
    }

    //把timeline的各项设置为选中色
    private void chageTimeLineOnColor(int index) {
        switch (index) {
            case 1:
                tvTitle2.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvTimeLine2.setTextColor(getResources().getColor(R.color.colorWhite));
                tvTimeLine2.setBackground(getResources().getDrawable(R.drawable.shape_timeline_point_on));
                lineView2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                break;
            case 2:
                tvTitle3.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvTimeLine3.setTextColor(getResources().getColor(R.color.colorWhite));
                tvTimeLine3.setBackground(getResources().getDrawable(R.drawable.shape_timeline_point_on));
                lineView3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                break;
            case 3:
                tvTitle4.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvTimeLine4.setTextColor(getResources().getColor(R.color.colorWhite));
                tvTimeLine4.setBackground(getResources().getDrawable(R.drawable.shape_timeline_point_on));
                break;
            case 4:
                tvTitle4.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvTimeLine4.setTextColor(getResources().getColor(R.color.colorWhite));
                tvTimeLine4.setBackground(getResources().getDrawable(R.drawable.shape_timeline_point_on));
                break;
            default:
                break;
        }
    }

    //把timeline的各项设置为非选中色
    private void chageTimeLineOffColor(int index) {
        switch (index) {
            case 1:
                tvTitle2.setTextColor(getResources().getColor(R.color.color_time_line_point_off));
                tvTimeLine2.setTextColor(getResources().getColor(R.color.color_time_line_point_off));
                tvTimeLine2.setBackground(getResources().getDrawable(R.drawable.shape_timeline_point_off));
                lineView2.setBackgroundColor(getResources().getColor(R.color.color_time_line_point_off));
                break;
            case 2:
                tvTitle3.setTextColor(getResources().getColor(R.color.color_time_line_point_off));
                tvTimeLine3.setTextColor(getResources().getColor(R.color.color_time_line_point_off));
                tvTimeLine3.setBackground(getResources().getDrawable(R.drawable.shape_timeline_point_off));
                lineView3.setBackgroundColor(getResources().getColor(R.color.color_time_line_point_off));
                break;
            case 3:
                tvTitle4.setTextColor(getResources().getColor(R.color.color_time_line_point_off));
                tvTimeLine4.setTextColor(getResources().getColor(R.color.color_time_line_point_off));
                tvTimeLine4.setBackground(getResources().getDrawable(R.drawable.shape_timeline_point_off));
                break;
            case 4:
                tvTitle4.setTextColor(getResources().getColor(R.color.color_time_line_point_off));
                tvTimeLine4.setTextColor(getResources().getColor(R.color.color_time_line_point_off));
                tvTimeLine4.setBackground(getResources().getDrawable(R.drawable.shape_timeline_point_off));
                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_ll:
                this.finish();
                break;
            case R.id.btn_detect_new_user_step_2_next:

//                Map<String, String> requestMap = new HashMap<>(2);
//                requestMap.put("phoneNumber", etvNewUserMobile.getText().toString());
//                Gson gson = new Gson();
//                String jsonStr = gson.toJson(requestMap, HashMap.class);
//                final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonStr);
//                ApiUtil.searchUserByPhoneNumberCall(mContext, requestBody).enqueue(new Callback<ResponseMessageBean>() {
//                    @Override
//                    public void onResponse(Call<ResponseMessageBean> call, Response<ResponseMessageBean> response) {
//                        // Log.i(TAG, "onResponse: "+response.body());
//                        if (response.isSuccessful()) {
//                            ResponseMessageBean responseMessageBean = response.body();
//                            int resultStatus = responseMessageBean.getResultStatus();
//                            if (resultStatus == 0) {
////                                ResponseMessageBean.resultContent resultContent = responseMessageBean.getResultContent();
////                                String phoneNumber = resultContent.getPhoneNumber();
////                                String nickname = resultContent.getNickname();
////                                String idCard = resultContent.getIdCard();
////                                String socialInsurance = resultContent.getSocialInsurance();
////                                showCommonDialog(phoneNumber, nickname, idCard, socialInsurance);
//                            } else {
//                                // 说明是新患者，直接下一步，建立患者信息
//                                vfNewUserInfos.setDisplayedChild(1);
//                                changeTitle();
//                            }
//                        } else {
//                            showRegisteredResultDialog("注册超时，请返回重试。");
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseMessageBean> call, Throwable t) {
//                        // Log.i(TAG, "onFailure: "+t);
//                    }
//                });
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
                vfNewUserInfos.setDisplayedChild(3);
                changeTitle();
                etvNewUserIdCard.setText("");
                break;
            case R.id.btn_detect_new_user_step_4_previous:
                vfNewUserInfos.setDisplayedChild(1);
                changeTitle();
                break;
            case R.id.btn_detect_new_user_step_4_next:
                vfNewUserInfos.setDisplayedChild(3);
                changeTitle();
                break;

//           case R.id.btn_detect_new_user_step_4_next:
//                if (!mFaceBase64Image.isEmpty()) {
//                    Log.d("6_done/5_skip", "onClick: entered!");
//                    mHandler.postDelayed(addUserRunnable, 100);
//                }
//                break;
            default:
                changeTitle();
                break;
        }
    }

    Runnable addUserRunnable = new Runnable() {
        @Override
        public void run() {
            // Log.i("addUserRunnable", "run: base64Image = " + mFaceBase64Image);
            loadingProgressBar.show();
            String nickname = etvNewUserName.getText().toString();
            String phoneNumber = etvNewUserMobile.getText().toString();
            String idCard = etvNewUserIdCard.getText().toString();
            String socialInsurance = etvNewUserSocialInsuranceConfirm.getText().toString();

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
                    loadingProgressBar.hide();
                    if (response.isSuccessful()) {
                        ResponseMessageBean responseMessageBean = response.body();
                        tackleWithResponds(responseMessageBean);
                    } else {
                        showRegisteredResultDialog("注册超时，请返回重试。");
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessageBean> call, Throwable t) {
                    // Log.i("addUserRunnable", "onFailure: " + t.toString());
                    showRegisteredResultDialog("注册超时，请返回重试。");
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

    /**
     * 展示对话框
     *
     * @param
     */
    private void showCommonDialog(final String phoneNumber, final String nickname, final String idCard, final String socialInsurance) {
        if (null == dialogMessage) {
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
                        + "身份证号：" + (TextUtils.isEmpty(idCard) ? "--" : (idCard.substring(0, 6) + "********" + idCard.substring(idCard.length() - 4))) + "\n"
                        + "社会保障卡号：" + (TextUtils.isEmpty(socialInsurance) ? "--" : socialInsurance)
        );


        final TextView tvDialogOk = (TextView) view.findViewById(R.id.btn_dialog_ok);
        tvDialogOk.setText("正确无误");
        tvDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMessage.dismiss();

                // 先把已有信息设定到对应控件上
                etvNewUserName.setText(TextUtils.isEmpty(nickname) ? "" : nickname);
                // etvNewUserIdCard.setText(TextUtils.isEmpty(idCard) ? "" : idCard);

                // 如果信息全部完整，那么进行签到
                // 否则需要填写缺失信息
                if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(nickname) && !TextUtils.isEmpty(idCard) && !TextUtils.isEmpty(socialInsurance)) {
                    mHandler.postDelayed(addUserRunnable, 100);
                } else if (TextUtils.isEmpty(nickname)) {
                    vfNewUserInfos.setDisplayedChild(1);
                    changeTitle();
                } else if (TextUtils.isEmpty(idCard)) {
                    vfNewUserInfos.setDisplayedChild(2);
                    changeTitle();
                } else {
                    vfNewUserInfos.setDisplayedChild(3);
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
        if (null != dialogMessage && !dialogMessage.isShowing()) {
            dialogMessage.show();
        }
    }

    /**
     * 展示共同照护患者选择看诊门诊类型对话框
     *
     * @param
     */
    private void showChooseOutpatientDialog(final String patientId) {
        if (null == dialogChooseOutpatient) {
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
                showRegisteredResultDialog("请您联系照护师改期或进行其他操作");
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
        if (null != dialogChooseOutpatient && !dialogChooseOutpatient.isShowing()) {
            dialogChooseOutpatient.show();
        }
    }

    private void checkInOnHealthCareTeamAttendanceState(String patientId, boolean hasAttendedHealthCareTeam) {
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

    private void tackleWithResponds(ResponseMessageBean responseMessageBean) {
        int resultStatus = responseMessageBean.getResultStatus();
        String resultMessage = responseMessageBean.getResultMessage();
        String dialogContents = "";
        switch (resultStatus) {
            case Constants.FACE_RESPONSE_CODE_SUCCESS:
                dialogContents = "签到成功！";
                showRegisteredResultDialog(dialogContents);
                break;

            case Constants.FACE_RESPONSE_CODE_ERROR_ALREADY_SIGNED_IN:
                dialogContents = "您已成功签到，请就诊。";
                showRegisteredResultDialog(dialogContents);
                break;

            case Constants.FACE_RESPONSE_CODE_ERROR_NEED_CONTACT_CDE:
                dialogContents = "请联系照护师核对信息进行签到，谢谢。";
                showRegisteredResultDialog(dialogContents);
                break;

            case Constants.FACE_RESPONSE_CODE_ERROR_SHOULD_CHECK_CERTAIN_DAY:
//                showChooseOutpatientDialog(responseMessageBean.getResultContent().getUserId());
                break;

            case Constants.FACE_RESPONSE_CODE_ERROR_OTHER_REASONS:
                dialogContents = "请联系照护师核对信息进行签到，谢谢。";
                showRegisteredResultDialog(dialogContents);
                break;

            default:
                dialogContents = "注册超时，请返回重试。";
                showRegisteredResultDialog(dialogContents);
                break;
        }
    }

}
