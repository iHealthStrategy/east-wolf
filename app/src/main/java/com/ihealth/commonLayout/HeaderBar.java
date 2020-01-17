package com.ihealth.commonLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ihealth.facecheckin.R;
import com.ihealth.retrofit.Constants;
import com.ihealth.utils.SharedPreferenceUtil;

public class HeaderBar extends LinearLayout {
    private TextView tvMainHospitalDepartmentName;
    private TextView getTvMainHospitalName;
    public HeaderBar(Context context){
        super(context);
    }
    public HeaderBar(final Context context, AttributeSet attrs) {
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.header_bar,this);
        tvMainHospitalDepartmentName  = (TextView) findViewById(R.id.tv_main_hospital_department_name);
        getTvMainHospitalName = (TextView) findViewById(R.id.hospital_name_tv);
        String hospitalName = SharedPreferenceUtil.getStringTypeSharedPreference(context, Constants.SP_NAME_HOSPITAL_INFOS, Constants.SP_KEY_HOSPITAL_FULL_NAME);
        getTvMainHospitalName.setText(hospitalName+"-");
        tvMainHospitalDepartmentName.setText("内分泌科");
    }
}
