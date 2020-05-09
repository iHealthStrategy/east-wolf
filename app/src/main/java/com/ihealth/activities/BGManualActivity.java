package com.ihealth.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ihealth.BaseActivity;
import com.ihealth.adapter.MealListAdapter;
import com.ihealth.bean.MealListDataBean;
import com.ihealth.facecheckin.R;
import com.ihealth.utils.MeasureMealDataUtils;
import com.ihealth.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 手动补录血糖的activity
 * Created by Liuhuan on 2020/05/09.
 */
public class BGManualActivity extends BaseActivity {

    @BindView(R.id.common_header_title)
    TextView commonHeaderTitle;
    @BindView(R.id.back_imageview)
    ImageView backImageview;
    @BindView(R.id.common_header_back_layout)
    LinearLayout commonHeaderBackLayout;
    @BindView(R.id.common_header_layout)
    RelativeLayout commonHeaderLayout;
    @BindView(R.id.et_value)
    EditText etValue;
    @BindView(R.id.bt_save)
    Button btSave;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.ll_date)
    LinearLayout llDate;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.ll_time)
    LinearLayout llTime;

    private Context mContext;
    private PopupWindow popWndMeal;
    private String measureTime = MeasureMealDataUtils.getCurrentMeasureTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_measure_bg);
        ButterKnife.bind(this);
        mContext = this;
        initView();
    }

    private void initView() {
        commonHeaderLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryGreen));
        commonHeaderTitle.setText(mContext.getString(R.string.title_manual_measure_bg));
        commonHeaderTitle.setTextColor(getResources().getColor(R.color.colorWhite));
    }

    private void showPopupWindow() {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_meal_select, null);
        popWndMeal = new PopupWindow(mContext);
        MealListAdapter adapter = new MealListAdapter(BGManualActivity.this);
        ListView listView = contentView.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        popWndMeal.setContentView(contentView);
        popWndMeal.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWndMeal.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWndMeal.showAtLocation(llTime, Gravity.BOTTOM, 0, 0);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MealListDataBean dataBean = MeasureMealDataUtils.getData().get(i);
                ToastUtils.showToast(BGManualActivity.this, dataBean.getKey());
                measureTime = dataBean.getKey();
                tvTime.setText(dataBean.getValue());
                popWndMeal.dismiss();
            }
        });
    }


    @OnClick({R.id.common_header_back_layout, R.id.ll_date, R.id.ll_time, R.id.bt_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.common_header_back_layout:
                finish();
                break;
            case R.id.ll_date:
                break;
            case R.id.ll_time:
                showPopupWindow();
                break;
            case R.id.bt_save:
                break;
        }
    }
}
