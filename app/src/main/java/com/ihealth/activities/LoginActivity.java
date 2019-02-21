package com.ihealth.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ihealth.BaseActivity;
import com.ihealth.MainActivity;
import com.ihealth.bean.LoginBean;
import com.ihealth.facecheckinapp.R;
import com.ihealth.retrofit.ApiUtil;
import com.ihealth.retrofit.Constants;
import com.ihealth.utils.SharedPreferenceUtil;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    Context mContext;
    TextInputEditText etvLoginUsername;
    TextInputEditText etvLoginPassword;
    Button floatingActionButton;

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

    }

    private void initView() {
        etvLoginUsername = (TextInputEditText) findViewById(R.id.etv_login_username);
        etvLoginPassword = (TextInputEditText) findViewById(R.id.etv_login_password);
        floatingActionButton = (Button) findViewById(R.id.fabtn_login);
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
        String username = etvLoginUsername.getText().toString();
        String password = etvLoginPassword.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
            Toast.makeText(mContext, "照护组名和密码不能为空！",Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> requestMap = new HashMap<>(2);
        requestMap.put("userName",username);
        requestMap.put("password",password);

        Gson gson = new Gson();
        String jsonStr = gson.toJson(requestMap, HashMap.class);

        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonStr);

        ApiUtil.loginCall(requestBody).enqueue(new Callback<LoginBean>() {
            @Override
            public void onResponse(Call<LoginBean> call, Response<LoginBean> response) {
                Log.i("loginCall", "onResponse: "+response.body());
                LoginBean loginBean = response.body();
                if (null!=loginBean){
                    Toast.makeText(mContext, "登录成功！",Toast.LENGTH_SHORT).show();
                    LoginBean.User user = loginBean.getUser();
                    Bundle bundle = new Bundle();
                    bundle.putString("login_info_groupId", user.getGroupId());
                    bundle.putString("login_info_hospitalFullName", user.getHospitalFullName());
                    bundle.putString("login_info_hospitalLogoImage", user.getHospitalLogoImage());
                    String token = loginBean.getToken();
                    SharedPreferenceUtil.editSharedPreference(mContext, Constants.SP_NAME_AUTHORIZATION, Constants.SP_KEY_TOKEN, token);
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("login_bundle",bundle);
                    startActivity(intent);
                    Activity activity = (Activity) mContext;
                    activity.finish();
                } else {
                    Toast.makeText(mContext, "登录失败。请检查用户名和密码后重试。",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginBean> call, Throwable t) {
                Log.i("loginCall", "onFailure: "+t);
                Toast.makeText(mContext, "登录失败。请检查用户名和密码后重试。",Toast.LENGTH_LONG).show();
            }
        });
    }
}
