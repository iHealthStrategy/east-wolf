package com.ihealth.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonToken;
import com.ihealth.BaseActivity;
import com.ihealth.MainActivity;
import com.ihealth.bean.HospitalBean;
import com.ihealth.bean.HospitalListBean;
import com.ihealth.bean.LoginBean;
import com.ihealth.facecheckinapp.R;
import com.ihealth.retrofit.ApiUtil;
import com.ihealth.retrofit.Constants;
import com.ihealth.utils.SharedPreferenceUtil;
import com.ihealth.views.LoginSpinnerAdapter;

import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int MSG_REFRESH_SPINNER = 2001;

    Context mContext;
    EditText etvLoginPassword;
    Button floatingActionButton;
    Spinner spinnerLoginSelectHospitals;

    SpinnerAdapter spinnerAdapter;

    List<HospitalBean> hospitalList = new ArrayList<>();
    HospitalBean defaultContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;
        initData();
        initView();
        initListeners();
    }

    private void initData() {

        defaultContent = new HospitalBean("--","--","--","--请选择医院--");
        ApiUtil.getHospitalsCall().enqueue(new Callback<HospitalListBean>() {
            @Override
            public void onResponse(Call<HospitalListBean> call, Response<HospitalListBean> response) {
                // Log.i("getHospitalsCall", "onResponse: " + response.body().getResultMessage() +"," + response.body().getResultContent()+","+ response.body().getResultStatus());
                if (response.body().getResultContent() !=null){
                    int respondCode = response.body().getResultStatus();
                    String respondMsg = response.body().getResultMessage();
                    if (respondCode == 0){
                        List<HospitalBean> resultContents = response.body().getResultContent();
                        hospitalList.add(0,defaultContent);
                        hospitalList.addAll(resultContents);
                        Message message = Message.obtain(handler);
                        message.what = MSG_REFRESH_SPINNER;
                        message.sendToTarget();
                    }else{
                        Toast.makeText(mContext, respondMsg, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(mContext, "请检查网络连接后重试", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<HospitalListBean> call, Throwable t) {
                // Log.i("getHospitalsCall", "onFailure: " + t);
                Toast.makeText(LoginActivity.this,R.string.response_error,Toast.LENGTH_SHORT).show();
//                Toast.makeText(mContext, "请检查网络连接后重试", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initView() {
        // etvLoginUsername = (TextInputEditText) findViewById(R.id.etv_login_username);
        etvLoginPassword = (EditText) findViewById(R.id.etv_login_password);
        floatingActionButton = (Button) findViewById(R.id.fabtn_login);
        spinnerLoginSelectHospitals = (Spinner) findViewById(R.id.spinner_login_select_hospital);
    }

    private void initListeners() {
        floatingActionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabtn_login:
                healthCareTeamLogin();
                break;
            default:
                break;
        }
    }

    private void healthCareTeamLogin(){
        final HospitalBean hospital = (HospitalBean)spinnerLoginSelectHospitals.getSelectedItem();
        String hospitalId = hospital.get_id();
        // Log.i(TAG, "healthCareTeamLogin: hospitalId = "+ hospitalId);
        String password = etvLoginPassword.getText().toString();

        if ("--".equals(hospitalId)){
            Toast.makeText(mContext, "请选择照护组",Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(hospitalId) || TextUtils.isEmpty(password)){
            Toast.makeText(mContext, "照护组名和密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> requestMap = new HashMap<>(2);
        requestMap.put("hospitalId",hospitalId);
        requestMap.put("password",password);

        Gson gson = new Gson();
        String jsonStr = gson.toJson(requestMap, HashMap.class);

        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonStr);

        ApiUtil.loginCall(requestBody).enqueue(new Callback<LoginBean>() {
            @Override
            public void onResponse(Call<LoginBean> call, Response<LoginBean> response) {
                // Log.i("loginCall", "onResponse: "+response.body());
                LoginBean loginBean = response.body();
                if (null!=loginBean){
                    Toast.makeText(mContext, "登录成功！",Toast.LENGTH_SHORT).show();
                    Gson gson = new Gson();
                   String jsonString = gson.toJson(hospital);
                    SharedPreferenceUtil.editSharedPreference(LoginActivity.this,SharedPreferenceUtil.SP_LOGIN_SUCESS_HOSPITAL_BEAN,SharedPreferenceUtil.SP_LOGIN_SUCESS_HOSPITAL_BEAN,jsonString);
                    LoginBean.User user = loginBean.getUser();
                    Bundle bundle = new Bundle();
                    bundle.putString("login_info_groupId", user.getGroupId());
                    bundle.putString("login_info_hospitalFullName", user.getHospitalFullName());
                    bundle.putString("login_info_hospitalLogoImage", user.getHospitalLogoImage());
                    SharedPreferenceUtil.editSharedPreference(mContext, Constants.SP_NAME_HOSPITAL_INFOS, Constants.SP_KEY_HOSPITAL_FULL_NAME, user.getHospitalFullName());
                    SharedPreferenceUtil.editSharedPreference(mContext, Constants.SP_NAME_HOSPITAL_INFOS, Constants.SP_KEY_HOSPITAL_GROUP_ID, user.getGroupId());
                    SharedPreferenceUtil.editSharedPreference(mContext, Constants.SP_NAME_HOSPITAL_INFOS, Constants.SP_KEY_HOSPITAL_LOGO_URL, user.getHospitalLogoImage());
                    String token = loginBean.getToken();
                    SharedPreferenceUtil.editSharedPreference(mContext, Constants.SP_NAME_AUTHORIZATION, Constants.SP_KEY_TOKEN, token);
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("login_bundle",bundle);
                    startActivity(intent);
                    Activity activity = (Activity) mContext;
                    activity.finish();
                } else {
                    Toast.makeText(mContext, "请检查用户名和密码后重试",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginBean> call, Throwable t) {
                // Log.i("loginCall", "onFailure: "+t);
                Toast.makeText(LoginActivity.this,R.string.response_error,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_REFRESH_SPINNER){
                // Log.i(TAG, "initView: hospitalList.size() = "+ hospitalList.size());
                spinnerAdapter = new LoginSpinnerAdapter(mContext, hospitalList);
                spinnerLoginSelectHospitals.setAdapter(spinnerAdapter);
                spinnerLoginSelectHospitals.setSelection(0);
                spinnerLoginSelectHospitals.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Log.i(TAG, "onItemSelected called: " + position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }
    };
}
