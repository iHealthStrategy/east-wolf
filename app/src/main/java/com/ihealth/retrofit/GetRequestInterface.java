package com.ihealth.retrofit;

import com.ihealth.bean.HospitalBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * retrofit GET数据
 *
 * @author liyanwen
 */
public interface GetRequestInterface {
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @GET("/api/getHospitals")
    Call<HospitalBean> getHospitals();
}

