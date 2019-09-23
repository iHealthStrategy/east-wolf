package com.ihealth.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ihealth.BaseActivity;
import com.ihealth.facecheckinapp.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_patient_type);
        ButterKnife.bind(this);
        commonHeaderTitle.setText("共同照护内分泌全科室人脸签到");
        initAdapter();
    }

    private void initAdapter() {
        patientTypes.add("糖尿病");
        patientTypes.add("糖尿病我爱你中国");
        patientTypes.add("糖尿病");
        mInflater = LayoutInflater.from(this);
        idFlowlayout.setAdapter(new TagAdapter<String>(patientTypes)
        {
            @Override
            public View getView(FlowLayout parent, int position, String s)
            {
                TextView tempTv = (TextView) mInflater.inflate(R.layout.item_patient_type,
                        idFlowlayout, false);
                tempTv.setText(s);
                return tempTv;
            }
        });
    }

    @OnClick({R.id.common_header_back_layout, R.id.activity_patient_type_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.common_header_back_layout:
                break;
            case R.id.activity_patient_type_btn:
                break;
        }
    }
}
