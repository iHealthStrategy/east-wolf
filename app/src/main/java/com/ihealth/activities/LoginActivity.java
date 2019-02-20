package com.ihealth.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    Context mContext;
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

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("userName","chaoyangyiyuan");
        requestMap.put("password","iHealth2019");

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
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
                    Activity activity = (Activity) mContext;
                    activity.finish();
                } else {

                }
            }

            @Override
            public void onFailure(Call<LoginBean> call, Throwable t) {
                Log.i("loginCall", "onFailure: "+t);
            }
        });
    }
}
