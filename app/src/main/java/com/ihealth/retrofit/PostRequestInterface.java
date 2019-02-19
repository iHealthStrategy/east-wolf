package com.ihealth.retrofit;

import com.ihealth.bean.ResponseMessageBean;
import com.ihealth.bean.LoginBean;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * retrofit POST数据
 *
 * @author liyanwen
 */
public interface PostRequestInterface {

    @POST("/login")
    Call<LoginBean> login();

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("detectFace/searchFace")
    Call<ResponseMessageBean> searchFace(@Body RequestBody requestBody);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("detectFace/addUser")
    Call<ResponseMessageBean> addUser(@Body RequestBody requestBody);
}
