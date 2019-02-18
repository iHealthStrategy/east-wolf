package com.ihealth.retrofit;

import com.ihealth.bean.AddUserBean;
import com.ihealth.bean.LoginBean;
import com.ihealth.bean.SearchFaceBean;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * retrofit POST数据
 *
 * @author liyanwen
 */
public interface PostRequestInterface {

    @POST("/login")
    Call<LoginBean> login();

    @Multipart
    @POST("detectFace/searchFace")
    Call<SearchFaceBean> searchFace(@Part List<MultipartBody.Part> partList);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("detectFace/addUser")
    // @FormUrlEncoded
    Call<AddUserBean> addUser(@Body RequestBody requestBody);
}
