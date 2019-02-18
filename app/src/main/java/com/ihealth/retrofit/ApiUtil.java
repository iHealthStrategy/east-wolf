package com.ihealth.retrofit;

import android.util.Log;

import com.ihealth.bean.AddUserBean;
import com.ihealth.bean.SearchFaceBean;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtil {
    private static final String HOST = "http://172.16.0.51:3080";
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

    public static Call<SearchFaceBean> searchFaceCall(List<MultipartBody.Part> partList){
        return ApiUtil.getApiService().searchFace(partList);
    }

    public static Call<AddUserBean> addUserCall(RequestBody requestBody){
        Log.i("addUserCall", "addUserCall: requestBody = "+requestBody.toString());
        Call<AddUserBean> ccc = ApiUtil.getApiService().addUser(requestBody);
        return ccc;
    }
}
