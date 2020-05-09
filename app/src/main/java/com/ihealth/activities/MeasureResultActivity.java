package com.ihealth.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ihealth.BaseActivity;
import com.ihealth.facecheckin.R;
import com.ihealth.utils.MeasureMealDataUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 血糖测量结果的activity
 * Created by Liuhuan on 2020/05/08.
 */
public class MeasureResultActivity extends BaseActivity {
    @BindView(R.id.common_header_title)
    TextView commonHeaderTitle;
    @BindView(R.id.common_header_back_layout)
    LinearLayout commonHeaderBackLayout;
    @BindView(R.id.common_header_layout)
    RelativeLayout commonHeaderLayout;
    @BindView(R.id.tv_measure_value)
    TextView tvMeasureValue;
    @BindView(R.id.tv_measure_tip)
    TextView tvMeasureTip;
    @BindView(R.id.tv_measure_target)
    TextView tvMeasureTarget;
    @BindView(R.id.tv_measure_mark)
    TextView tvMeasureMark;
    @BindView(R.id.ll_mark)
    LinearLayout llMark;
    @BindView(R.id.ll_content)
    LinearLayout llContent;

    String day, measureTime, value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_result);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Bundle bundle = getIntent().getExtras();
        day = bundle.getString("day");
        measureTime = bundle.getString("measureTime");
        value = bundle.getString("value");
        if(!value.isEmpty()){
            int valueTemp = (Integer.parseInt(value) / 18);
            value = (float)(Math.round(valueTemp * 100))/100 + "";
        }

        String measureTimeText = MeasureMealDataUtils.getMeasureTimeText(measureTime);
        String targetValue = MeasureMealDataUtils.getTargetValue(measureTime);
        String controlInfo = MeasureMealDataUtils.getControlInfo(measureTime, value);
        String controlText = "血糖正常";
        commonHeaderLayout.setBackgroundColor(getResources().getColor(R.color.colorMeasureResultNormal));
        if (controlInfo.equals("low")) {
            controlText = "血糖过低";
            commonHeaderLayout.setBackgroundColor(getResources().getColor(R.color.colorMeasureResultLow));
            llContent.setBackgroundColor(getResources().getColor(R.color.colorMeasureResultLow));
            tvMeasureTip.setTextColor(getResources().getColor(R.color.colorMeasureResultLow));
        } else if (controlInfo.equals("high")) {
            controlText = "血糖过高";
            commonHeaderLayout.setBackgroundColor(getResources().getColor(R.color.colorMeasureResultHigh));
            llContent.setBackgroundColor(getResources().getColor(R.color.colorMeasureResultHigh));
            tvMeasureTip.setTextColor(getResources().getColor(R.color.colorMeasureResultHigh));
        }
        commonHeaderTitle.setText(day + " " + measureTimeText);
        commonHeaderTitle.setTextColor(getResources().getColor(R.color.colorWhite));
        tvMeasureTarget.setText(measureTimeText + "控糖目标 " + targetValue);
        tvMeasureValue.setText(value);
        tvMeasureTip.setText(controlText);
    }


    @OnClick({R.id.common_header_back_layout, R.id.ll_mark})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.common_header_back_layout:
                finish();
                break;
            case R.id.ll_mark:
                break;
        }
    }
}
