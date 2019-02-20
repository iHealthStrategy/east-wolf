package com.ihealth.retrofit;

import android.content.Context;

import com.ihealth.bean.LoginBean;
import com.ihealth.bean.ResponseMessageBean;
import com.ihealth.utils.SharedPreferenceUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtil {
    private static final String HOST = "http://172.16.1.248:3080";
    private static Retrofit retrofit;
    private static final int DEFAULT_TIMEOUT = 10;

    public static String getHOST() {
        return HOST;
    }

    private static Retrofit getApiRetrofit(){
        if (null == retrofit){
            OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
            okHttpClient.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            retrofit = new Retrofit.Builder()
                    .client(okHttpClient.build())
                    .baseUrl(HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    // .addCallAdapterFactory()
                    .build();
        }
        return retrofit;
    }

    private static PostRequestInterface getApiService(){
        return ApiUtil.getApiRetrofit().create(PostRequestInterface.class);
    }

    public static Call<LoginBean> loginCall(RequestBody requestBody){
        return ApiUtil.getApiService().login(requestBody);
    }

    public static Call<ResponseMessageBean> searchFaceCall(Context context, RequestBody requestBody){
        String token = SharedPreferenceUtil.getStringTypeSharedPreference(context,Constants.SP_NAME_AUTHORIZATION,Constants.SP_KEY_TOKEN);
        Map<String, String> headerMap = new HashMap<>(2);
        headerMap.put("Content-type","application/json;charset=UTF-8");
        headerMap.put("authorization","Bearer "+token);
        return ApiUtil.getApiService().searchFace(headerMap, requestBody);
    }

    public static Call<ResponseMessageBean> addUserCall(Context context, RequestBody requestBody){
        String token = SharedPreferenceUtil.getStringTypeSharedPreference(context,Constants.SP_NAME_AUTHORIZATION,Constants.SP_KEY_TOKEN);
        Map<String, String> headerMap = new HashMap<>(2);
        headerMap.put("Content-type","application/json;charset=UTF-8");
        headerMap.put("authorization","Bearer "+token);
        return ApiUtil.getApiService().addUser(headerMap, requestBody);
    }
}
