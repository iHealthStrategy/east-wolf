package com.ihealth.retrofit;

import android.content.Context;

import com.ihealth.bean.HospitalBean;
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

/**
 * Retrofit API Utilities
 * @author liyanwen
 */
public class ApiUtil {
    // 上线最终打包前，注意修改此地址！
    private static final String HOST = Constants.BACKEND_URL_STAGING;
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

    private static PostRequestInterface getPostApiService(){
        return ApiUtil.getApiRetrofit().create(PostRequestInterface.class);
    }

    private static GetRequestInterface getGetApiService(){
        return ApiUtil.getApiRetrofit().create(GetRequestInterface.class);
    }

    public static Call<LoginBean> loginCall(RequestBody requestBody){
         Map<String, String> headerMap = new HashMap<>(2);
         headerMap.put("Content-type","application/json;charset=UTF-8");
        return ApiUtil.getPostApiService().login(headerMap, requestBody);
    }

    public static Call<ResponseMessageBean> searchFaceCall(Context context, RequestBody requestBody){
        String token = SharedPreferenceUtil.getStringTypeSharedPreference(context,Constants.SP_NAME_AUTHORIZATION,Constants.SP_KEY_TOKEN);
        // Log.i("searchFaceCall", "searchFaceCall: "+token);
        Map<String, String> headerMap = new HashMap<>(2);
        headerMap.put("Content-type","application/json;charset=UTF-8");
        headerMap.put("Authorization","Bearer "+token);
        return ApiUtil.getPostApiService().searchFace(headerMap, requestBody);
    }

    public static Call<ResponseMessageBean> searchUserByPhoneNumberCall(Context context, RequestBody requestBody){
        String token = SharedPreferenceUtil.getStringTypeSharedPreference(context,Constants.SP_NAME_AUTHORIZATION,Constants.SP_KEY_TOKEN);
        // Log.i("searchUserByPhoneNumberCall", "searchFaceCall: "+token);
        Map<String, String> headerMap = new HashMap<>(2);
        headerMap.put("Content-type","application/json;charset=UTF-8");
        headerMap.put("Authorization","Bearer "+token);
        return ApiUtil.getPostApiService().searchUserByPhoneNumber(headerMap, requestBody);
    }

    public static Call<ResponseMessageBean> checkInWithConditionCall(Context context, RequestBody requestBody){
        String token = SharedPreferenceUtil.getStringTypeSharedPreference(context,Constants.SP_NAME_AUTHORIZATION,Constants.SP_KEY_TOKEN);
        // Log.i("searchUserByPhoneNumberCall", "searchFaceCall: "+token);
        Map<String, String> headerMap = new HashMap<>(2);
        headerMap.put("Content-type","application/json;charset=UTF-8");
        headerMap.put("Authorization","Bearer "+token);
        return ApiUtil.getPostApiService().checkInWithCondition(headerMap, requestBody);
    }

    public static Call<ResponseMessageBean> addUserCall(Context context, RequestBody requestBody){
        String token = SharedPreferenceUtil.getStringTypeSharedPreference(context,Constants.SP_NAME_AUTHORIZATION,Constants.SP_KEY_TOKEN);
        Map<String, String> headerMap = new HashMap<>(2);
        headerMap.put("Content-type","application/json;charset=UTF-8");
        headerMap.put("Authorization","Bearer "+token);
        return ApiUtil.getPostApiService().addUser(headerMap, requestBody);
    }

    public static Call<HospitalBean> getHospitalsCall(){
        return ApiUtil.getGetApiService().getHospitals();
    }
}
