package com.ihealth.retrofit;

import com.ihealth.bean.LoginBean;

import retrofit2.Call;
import retrofit2.http.POST;

/**
 * retrofit POST数据
 *
 * @author liyanwen
 */
public interface PostRequestInterface {
    @POST("")
    Call<LoginBean> getCall();
}
