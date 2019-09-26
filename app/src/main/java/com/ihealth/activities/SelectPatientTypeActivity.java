package com.ihealth.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ihealth.BaseActivity;
import com.ihealth.bean.AddUserRequestBean;
import com.ihealth.bean.DepartmentBean;
import com.ihealth.bean.HospitalBean;
import com.ihealth.bean.OfficesType;
import com.ihealth.bean.ResponseMessageBean;
import com.ihealth.events.FinshDetectRegisterAndResultEvent;
import com.ihealth.events.FinshDetectRegisterSelectTypeAndResultEvent;
import com.ihealth.events.FinshRegisterAndResultEvent;
import com.ihealth.events.FinshRegisterSelectTypeAndResultEvent;
import com.ihealth.facecheckinapp.R;
import com.ihealth.retrofit.ApiUtil;
import com.ihealth.retrofit.Constants;
import com.ihealth.utils.BundleKeys;
import com.ihealth.utils.ConstantArguments;
import com.ihealth.utils.DateUtils;
import com.ihealth.utils.FaceDetectExtendManager;
import com.ihealth.utils.LoadingProgressBar;
import com.ihealth.utils.SharedPreferenceUtil;
import com.ihealth.views.LoadingDialog;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 注册新患者信息之后的     选择患者科室     的activity
 * Created by Wangyuxu on 2019/09/23.
 */

public class SelectPatientTypeActivity extends BaseActivity {
    @BindView(R.id.common_header_title)
    TextView commonHeaderTitle;
    @BindView(R.id.common_header_back_layout)
    LinearLayout commonHeaderBackLayout;
    @BindView(R.id.id_flowlayout)
    TagFlowLayout idFlowlayout;
    @BindView(R.id.activity_patient_type_btn)
    Button activityPatientTypeBtn;
    private ArrayList<String> patientTypes = new ArrayList<>();
    private LayoutInflater mInflater;
    private int mPosition = -1;
    private ArrayList<OfficesType> temp;
    private AddUserRequestBean userBean;
    private LoadingDialog loadingProgressBar;
    private HospitalBean hospitalBean = new HospitalBean();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_patient_type);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
//        Bundle bundle = intent.getBundleExtra("data_from_detect_activity");
        userBean = (AddUserRequestBean) bundle.getSerializable(BundleKeys.ADDUSERREQUESTBEAN);
        commonHeaderTitle.setText("共同照护内分泌全科室人脸签到");
        loadingProgressBar = new LoadingDialog(this, "");
        initAdapter();
    }

    private void initAdapter() {
//        Gson gson = new Gson();
//        String jsonString = SharedPreferenceUtil.getStringTypeSharedPreference(SelectPatientTypeActivity.this, SharedPreferenceUtil.SP_LOGIN_SUCESS_HOSPITAL_BEAN, SharedPreferenceUtil.SP_LOGIN_SUCESS_HOSPITAL_BEAN);
//        if (!"".equals(jsonString)) {
//           hospitalBean=  gson.fromJson(jsonString, HospitalBean.class);
//        }
//        List<DepartmentBean> departmentBeans= hospitalBean.getDepartment();
//        if(departmentBeans==null){
//            departmentBeans = new ArrayList<>();
//        }
//        if(departmentBeans.size()<1){
//            return;
//        }
//        DepartmentBean departmentBean= departmentBeans.get(0);
//        if(departmentBean==null){
//            departmentBean = new DepartmentBean();
//        }
//        temp = (ArrayList<OfficesType>) departmentBean.getDiseaseTypes();
        temp = new ArrayList<>();

        temp.add(new OfficesType("diabetes", "糖尿病"));
        temp.add(new OfficesType("thyroid", "甲状腺疾病"));
        temp.add(new OfficesType("adrenalGland", "肾上腺疾病"));
        temp.add(new OfficesType("pituitary", "垂体和下丘脑疾病"));
        handleData(temp);
        mInflater = LayoutInflater.from(this);
        idFlowlayout.setAdapter(new TagAdapter<String>(patientTypes) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tempTv = (TextView) mInflater.inflate(R.layout.item_patient_type,
                        idFlowlayout, false);
                tempTv.setText(s);
                return tempTv;
            }
        });

        idFlowlayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                mPosition = position;
                return false;
            }
        });
    }

    private void handleData(ArrayList<OfficesType> datas) {
        for (int i = 0; i < datas.size(); i++) {
            OfficesType temp = datas.get(i);
            patientTypes.add(temp.getText());
        }
    }

    @OnClick({R.id.common_header_back_layout, R.id.activity_patient_type_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.common_header_back_layout:
                finish();
                break;
            case R.id.activity_patient_type_btn:
                if (mPosition >= 0) {

                    addUserRunnable.run();
                } else {
                    Toast.makeText(this, "请选择患者病种信息！", Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }

    Runnable addUserRunnable = new Runnable() {
        @Override
        public void run() {
            // Log.i("addUserRunnable", "run: base64Image = " + mFaceBase64Image);
            loadingProgressBar.show();

            Gson gson = new Gson();
            userBean.getUserInfo().setDisease(temp.get(mPosition).getValue());
            String jsonStr = gson.toJson(userBean, AddUserRequestBean.class);

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonStr);

            ApiUtil.addUserCall(SelectPatientTypeActivity.this, requestBody).enqueue(new Callback<ResponseMessageBean>() {
                @Override
                public void onResponse(Call<ResponseMessageBean> call, Response<ResponseMessageBean> response) {
                    // Log.i("addUserRunnable", "onResponse: "+ response.body());
                    loadingProgressBar.hide();
                    if (response.isSuccessful()) {
                        ResponseMessageBean responseMessageBean = response.body();
                        if (responseMessageBean != null) {
                            handleResult(responseMessageBean);
                        }
                    } else {
//                        showRegisteredResultDialog("注册超时，请返回重试。");
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessageBean> call, Throwable t) {
                    loadingProgressBar.hide();
                    Toast.makeText(SelectPatientTypeActivity.this, R.string.response_error, Toast.LENGTH_SHORT).show();
                    // Log.i("addUserRunnable", "onFailure: " + t.toString());
//                    showRegisteredResultDialog("注册超时，请返回重试。");
                }
            });

        }
    };

    private void handleResult(ResponseMessageBean responseMessage) {
        Intent intent = new Intent(this, RegisterResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BundleKeys.APPOINTMENTSBEAN, responseMessage.getResultContent());
        bundle.putBoolean(BundleKeys.COME_FROM_SELECT_TYPE_UI, true);
        intent.putExtras(bundle);
        switch (responseMessage.getResultStatus()) {
            case Constants.FACE_RESPONSE_CODE_SUCCESS://识别成功，直接打印 0

                bundle.putInt(BundleKeys.REGISTER_RESULT_STATUS, ConstantArguments.REGISTER_SUCESS);
                break;

            case Constants.FACE_RESPONSE_CODE_ERROR_SEARCH_USER_NOT_FOUND://跳转添加新用户，直接打印 1001

                break;

            case Constants.FACE_RESPONSE_CODE_ERROR_SEARCH_USER_FOUND_NOT_MATCH://重新扫脸  1002 1003  3001
            case Constants.FACE_RESPONSE_CODE_ERROR_SEARCH_OTHER_ERRORS:
            case Constants.FACE_RESPONSE_CODE_ERROR_DETECT_USER_FACE_INVALID:


                break;
            case Constants.FACE_RESPONSE_CODE_ERROR_ADD_USER_USER_NOT_EXIST://添加用户失败 2001 2002
            case Constants.FACE_RESPONSE_CODE_ERROR_ADD_USER_OTHER_ERRORS:
                bundle.putInt(BundleKeys.REGISTER_RESULT_STATUS, ConstantArguments.REGISTER_FAILED);
                break;
        }

        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FinshDetectRegisterSelectTypeAndResultEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FinshRegisterSelectTypeAndResultEvent event) {
        finish();
    }
}
