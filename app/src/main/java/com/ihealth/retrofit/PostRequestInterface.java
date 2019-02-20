package com.ihealth.retrofit;

import com.ihealth.bean.LoginBean;
import com.ihealth.bean.ResponseMessageBean;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * retrofit POST数据
 *
 * @author liyanwen
 */
public interface PostRequestInterface {

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("/detectLogin")
    Call<LoginBean> login(@Body RequestBody requestBody);

    // @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("detectFace/searchFace")
    Call<ResponseMessageBean> searchFace(@HeaderMap Map<String, String> headerMap, @Body RequestBody requestBody);

    // @Headers({"Content-type:application/json;charset=UTF-8","authorization:Bearer "})
    @POST("detectFace/addUser")
    Call<ResponseMessageBean> addUser(@HeaderMap Map<String, String> headerMap, @Body RequestBody requestBody);
}
