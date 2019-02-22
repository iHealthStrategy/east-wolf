package com.ihealth.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
    private Handler mHandler = new Handler();
    private Context mContext;

    TextView tvRegisterHeader;
    TextView tvRegisterTitle;
    ViewFlipper vfNewUserInfos;

    Button btnRegisterHome;

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

    String mFaceBase64Image = "";

    boolean isUserNameValid = false;
    boolean isUserIdCardValid = true;
    boolean isUserMobileValid = false;

    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mContext = this;

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

        btnRegisterHome = (Button) findViewById(R.id.btn_register_home);

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

    }

    private void initListeners() {
        btnRegisterHome.setOnClickListener(this);
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
                if (!TextUtils.isEmpty(s)){
                    if (TextInfosCheckUtil.checkMobile(s.toString())){
                        isUserMobileValid = true;
                        setButtonState(btnNewUserStep2Next, true);
                    } else {
                        isUserMobileValid = false;
                        setButtonState(btnNewUserStep2Next, false);
                    }
                }else {
                    isUserMobileValid = false;
                    setButtonState(btnNewUserStep2Next, false);
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
                if (!TextUtils.isEmpty(s)){
                    isUserNameValid = true;
                    setButtonState(btnNewUserStep3Next, true);
                } else {
                    isUserNameValid = false;
                    setButtonState(btnNewUserStep3Next, false);
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
                        setButtonState(btnNewUserStep4Done, true);
                    }else {
                        isUserIdCardValid = false;
                        setButtonState(btnNewUserStep4Done, false);
                    }
                } else {
                    isUserIdCardValid = true;
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

    @Override
    protected void onStop() {
        super.onStop();
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
            case 0:
                tvRegisterHeader.setText("建立患者信息（1/3）");
                tvRegisterTitle.setText("请录入患者手机号");
                break;
            case 1:
                tvRegisterHeader.setText("建立患者信息（2/3）");
                tvRegisterTitle.setText("请录入患者姓名");
                break;
            case 2:
                tvRegisterHeader.setText("建立患者信息（3/3）");
                tvRegisterTitle.setText("请录入患者身份证号");
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
            case R.id.btn_register_home:
                this.finish();
                break;
            case R.id.btn_detect_new_user_step_2_next:

                Map<String, String> requestMap = new HashMap<>(2);
                requestMap.put("phoneNumber",etvNewUserMobile.getText().toString());
                Gson gson = new Gson();
                String jsonStr = gson.toJson(requestMap, HashMap.class);
                final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonStr);
                ApiUtil.searchUserByPhoneNumberCall(mContext,requestBody).enqueue(new Callback<ResponseMessageBean>() {
                    @Override
                    public void onResponse(Call<ResponseMessageBean> call, Response<ResponseMessageBean> response) {
                        Log.i(TAG, "onResponse: "+response.body());
                        if (response.isSuccessful()) {
                            ResponseMessageBean responseMessageBean = response.body();
                            int resultStatus = responseMessageBean.getResultStatus();
                            if (resultStatus == 0){
                                ResponseMessageBean.resultContent resultContent = responseMessageBean.getResultContent();
                                showCommonDialog(
                                        "请确认如下信息是否正确：\n"
                                                + "手机号："+ resultContent.getPhoneNumber() +"\n"
                                                + "姓名："+ resultContent.getNickname()+"\n"
                                                + "身份证号："+ resultContent.getIdCard(),
                                        "正确无误",
                                        "取消"
                                );
                            }else {

                            }
                        } else {
                            showRegisteredResultDialog("很抱歉，签到失败。\n请返回重试。");
                        }
                        vfNewUserInfos.setDisplayedChild(1);
                        changeTitle();
                    }

                    @Override
                    public void onFailure(Call<ResponseMessageBean> call, Throwable t) {
                        Log.i(TAG, "onFailure: "+t);
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

    /**
     * 展示对话框
     *
     * @param
     */
    private void showCommonDialog(String dialogContent, String confirmString, String cancelString) {
        final BaseDialog dialog = new BaseDialog(mContext);
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.fragment_dialog_common, null);

        final TextView tvDialogContent = (TextView) view.findViewById(R.id.tv_common_dialog_content);
        tvDialogContent.setText(dialogContent);

        final TextView tvDialogOk = (TextView) view.findViewById(R.id.btn_dialog_ok);
        tvDialogOk.setText(confirmString);

        final TextView tvDialogCancel = (TextView) view.findViewById(R.id.btn_dialog_cancel);
        tvDialogCancel.setText(cancelString);

        (view.findViewById(R.id.btn_dialog_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Activity activity  = (Activity) mContext;
                activity.finish();
            }
        });

        (view.findViewById(R.id.btn_dialog_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Activity activity  = (Activity) mContext;
                activity.finish();
            }
        });



        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.show();
    }

}
