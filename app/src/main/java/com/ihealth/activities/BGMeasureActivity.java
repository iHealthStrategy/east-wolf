package com.ihealth.activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ihealth.BaseActivity;
import com.ihealth.adapter.MealListAdapter;
import com.ihealth.bean.MealListDataBean;
import com.ihealth.communication.control.Bg1Control;
import com.ihealth.communication.control.Bg1Profile;
import com.ihealth.facecheckin.R;
import com.ihealth.utils.DateUtils;
import com.ihealth.utils.MeasureMealDataUtils;
import com.ihealth.utils.ToastUtils;
import com.ihealth.utils.UIUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * 血糖测量的activity
 * Created by Liuhuan on 2020/04/29.
 */
public class BGMeasureActivity extends BaseActivity {
    @BindView(R.id.common_header_title)
    TextView commonHeaderTitle;
    @BindView(R.id.back_imageview)
    ImageView backImageview;
    @BindView(R.id.common_header_back_layout)
    LinearLayout commonHeaderBackLayout;
    @BindView(R.id.common_header_layout)
    RelativeLayout commonHeaderLayout;
    @BindView(R.id.tv_tip_text)
    TextView tvTipText;
    @BindView(R.id.tv_step_one)
    TextView tvStepOne;
    @BindView(R.id.tv_line_one)
    View tvLineOne;
    @BindView(R.id.tv_step_two)
    TextView tvStepTwo;
    @BindView(R.id.tv_line_two)
    View tvLineTwo;
    @BindView(R.id.tv_step_three)
    TextView tvStepThree;
    @BindView(R.id.iv_guide)
    ImageView ivGuide;
    @BindView(R.id.iv_step_one)
    ImageView ivStepOne;
    @BindView(R.id.iv_step_two)
    ImageView ivStepTwo;
    @BindView(R.id.tv_meal)
    TextView tvMeal;
    @BindView(R.id.ll_meal)
    LinearLayout llMeal;
    @BindView(R.id.rl_measure_content)
    RelativeLayout rlMeasureContent;
    @BindView(R.id.view_bg_top)
    View viewBgTop;
    @BindView(R.id.iv_bg_measure)
    ImageView ivBgMeasure;
    @BindView(R.id.rl_bg_measure)
    RelativeLayout rlBgMeasure;

    private Context mContext;
    private PopupWindow popWnd;
    private static final String TAG = "BG1";
    public Bg1Control mBg1Control;

    private boolean isGetStripInBg1 = false;
    private boolean isGetResultBg1 = false;
    private boolean isGetBloodBg1 = false;
    private RxPermissions permissions;
    public String QRCode = "02554064554014322D1200A05542D3BACE1446CE9A961901222F00A70B46";

    private String measureTime = MeasureMealDataUtils.getCurrentMeasureTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_bg);
        ButterKnife.bind(this);
        mContext = this;
        initView();
        checkPermission();
    }

    private void initView() {
        commonHeaderLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryGreen));
        commonHeaderTitle.setText(mContext.getString(R.string.title_measure_bg));
        commonHeaderTitle.setTextColor(getResources().getColor(R.color.colorWhite));
        backImageview.setImageResource(R.mipmap.icon_back_white);
        tvMeal.setText(MeasureMealDataUtils.getMeasureTimeText(measureTime));

        Glide.with(this).load(R.mipmap.gif_measure_guide_1).into(ivGuide);
        registerBroadcast();
        Intent intent = getIntent();
//        String userName = intent.getExtras().getString("userName");
        mDeviceMac = intent.getStringExtra("mac");
        mDeviceName = intent.getStringExtra("type");
        mBg1Control = Bg1Control.getInstance();
        mBg1Control.init(this, "", 0x00FF1304, true);
    }

    private void performAnim() {
        //属性动画对象
        ValueAnimator va;
        if (true) {
            //显示view，高度从0变到height值
            va = ValueAnimator.ofInt(0, UIUtils.dp2px(BGMeasureActivity.this, 353));
        } else {
            //隐藏view，高度从height变为0
            va = ValueAnimator.ofInt(373, 0);
        }
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取当前的height值
                int h = (Integer) valueAnimator.getAnimatedValue();
                //动态更新view的高度
                viewBgTop.getLayoutParams().height = h;
                viewBgTop.requestLayout();
            }
        });
        va.setDuration(8000);
        va.start();
    }


    private void showPopupWindow() {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_meal_select, null);
        popWnd = new PopupWindow(mContext);
        MealListAdapter adapter = new MealListAdapter(BGMeasureActivity.this);
        ListView listView = contentView.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        popWnd.setContentView(contentView);
        popWnd.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWnd.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWnd.showAtLocation(llMeal, Gravity.BOTTOM, 0, 0);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MealListDataBean dataBean = MeasureMealDataUtils.getData().get(i);
                ToastUtils.showToast(BGMeasureActivity.this, dataBean.getKey());
                measureTime = dataBean.getKey();
                tvMeal.setText(dataBean.getValue());
                popWnd.dismiss();
            }
        });
    }

    private void setStepView(String step) {
        if (step.equals("connect")) {
            tvTipText.setText(mContext.getString(R.string.confirm_tip_bg1_connect));
        } else if (step.equals("disconnect")) {
            ivStepOne.setVisibility(View.GONE);
            ivStepTwo.setVisibility(View.GONE);
            tvStepOne.setVisibility(View.VISIBLE);
            tvStepTwo.setVisibility(View.VISIBLE);
            tvTipText.setText(mContext.getString(R.string.confirm_tip_bg1_1));
            tvStepOne.setTextColor(getResources().getColor(R.color.colorWhite));
            tvStepOne.setBackground(mContext.getDrawable(R.drawable.icon_measure_step_circle));
            tvStepTwo.setTextColor(getResources().getColor(R.color.colorHalfWhite));
            tvStepTwo.setBackground(mContext.getDrawable(R.drawable.icon_measure_step_circle_grey));
            tvStepThree.setTextColor(getResources().getColor(R.color.colorHalfWhite));
            tvStepThree.setBackground(mContext.getDrawable(R.drawable.icon_measure_step_circle_grey));
            tvLineOne.setBackgroundColor(getResources().getColor(R.color.colorHalfWhite));
            tvLineTwo.setBackgroundColor(getResources().getColor(R.color.colorHalfWhite));
            ivGuide.setImageResource(R.mipmap.gif_measure_guide_1);
            Glide.with(this).load(R.mipmap.gif_measure_guide_1).into(ivGuide);
        } else if (step.equals("one")) {
            ivStepOne.setVisibility(View.GONE);
            ivStepTwo.setVisibility(View.GONE);
            tvStepOne.setVisibility(View.VISIBLE);
            tvStepTwo.setVisibility(View.VISIBLE);
            tvTipText.setText(mContext.getString(R.string.confirm_tip_bg1_1));
            tvStepOne.setTextColor(getResources().getColor(R.color.colorWhite));
            tvStepOne.setBackground(mContext.getDrawable(R.drawable.icon_measure_step_circle));
            tvStepTwo.setTextColor(getResources().getColor(R.color.colorHalfWhite));
            tvStepTwo.setBackground(mContext.getDrawable(R.drawable.icon_measure_step_circle_grey));
            tvStepThree.setTextColor(getResources().getColor(R.color.colorHalfWhite));
            tvStepThree.setBackground(mContext.getDrawable(R.drawable.icon_measure_step_circle_grey));
            tvLineOne.setBackgroundColor(getResources().getColor(R.color.colorHalfWhite));
            tvLineTwo.setBackgroundColor(getResources().getColor(R.color.colorHalfWhite));
            ivGuide.setImageResource(R.mipmap.gif_measure_guide_1);
            Glide.with(this).load(R.mipmap.gif_measure_guide_1).into(ivGuide);
        } else if (step.equals("two")) {
            ivStepOne.setVisibility(View.VISIBLE);
            tvStepOne.setVisibility(View.GONE);
            tvTipText.setText(mContext.getString(R.string.confirm_tip_bg1_2));
            tvStepTwo.setTextColor(getResources().getColor(R.color.colorWhite));
            tvStepTwo.setBackground(mContext.getDrawable(R.drawable.icon_measure_step_circle));
            tvLineOne.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            ivGuide.setImageResource(R.mipmap.gif_measure_guide_2);
            Glide.with(this).load(R.mipmap.gif_measure_guide_2).into(ivGuide);
        } else if (step.equals("three")) {
            ivStepOne.setVisibility(View.VISIBLE);
            ivStepTwo.setVisibility(View.VISIBLE);
            tvStepOne.setVisibility(View.GONE);
            tvStepTwo.setVisibility(View.GONE);
            tvTipText.setText(mContext.getString(R.string.confirm_tip_bg1_3));
            tvStepTwo.setTextColor(getResources().getColor(R.color.colorWhite));
            tvStepTwo.setBackground(mContext.getDrawable(R.drawable.icon_measure_step_circle));
            tvStepThree.setTextColor(getResources().getColor(R.color.colorWhite));
            tvStepThree.setBackground(mContext.getDrawable(R.drawable.icon_measure_step_circle));
            tvLineOne.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvLineTwo.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            ivGuide.setImageResource(R.mipmap.gif_measure_guide_3);
            Glide.with(this).load(R.mipmap.gif_measure_guide_3).into(ivGuide);
        } else if (step.equals("blood")) {
            tvTipText.setText(mContext.getString(R.string.confirm_tip_bg1_4));
            rlMeasureContent.setVisibility(View.GONE);
            rlBgMeasure.setVisibility(View.VISIBLE);
            performAnim();
        } else if (step.equals("failed")) {
            tvTipText.setText(mContext.getString(R.string.confirm_tip_bg1_6));
        }
    }

    /**
     * 检查权限
     * check Permission
     */
    private void checkPermission() {
        permissions = new RxPermissions(this);
        permissions.requestEach(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        )
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) {
                        if (permission.granted) {

                        } else if (permission.shouldShowRequestPermissionRationale) {
                            ToastUtils.showToast(BGMeasureActivity.this, "请打开相关权限，否则会影响功能的使用");
                        } else {
                            ToastUtils.showToast(BGMeasureActivity.this, "请打开相关权限，否则会影响功能的使用");
                        }
                    }
                });
    }

    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);

        filter.addAction(Bg1Profile.ACTION_BG1_DEVICE_READY);
        filter.addAction(Bg1Profile.ACTION_BG1_IDPS);
        filter.addAction(Bg1Profile.ACTION_BG1_CONNECT_RESULT);
        filter.addAction(Bg1Profile.ACTION_BG1_SENDCODE_RESULT);

        filter.addAction(Bg1Profile.ACTION_BG1_MEASURE_ERROR);
        filter.addAction(Bg1Profile.ACTION_BG1_MEASURE_STRIP_IN);
        filter.addAction(Bg1Profile.ACTION_BG1_MEASURE_STRIP_OUT);
        filter.addAction(Bg1Profile.ACTION_BG1_MEASURE_GET_BLOOD);
        filter.addAction(Bg1Profile.ACTION_BG1_MEASURE_RESULT);
        filter.addAction(Bg1Profile.ACTION_BG1_MEASURE_STANDBY);
        this.registerReceiver(mBroadcastReceiver, filter);
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_HEADSET_PLUG)) {

                if (intent.hasExtra("state")) {
                    if (intent.getIntExtra("state", 0) == 0) {
                        mBg1Control.disconnect();
//                        Toast.makeText(MainActivity.this,"headset out",Toast.LENGTH_SHORT);
                        addLogInfo("headset out");
//                      请把血糖仪插入手机耳机孔
                        setStepView("disconnect");
                    }
                    if (intent.getIntExtra("state", 0) == 1) {
//                        showLogLayout();
                        addLogInfo("headset in");
                        String QRInfo = mBg1Control.getBottleInfoFromQR(QRCode);
                        addLogInfo("QRInfo =" + QRInfo);
                        mBg1Control.connect();
                        setStepView("connect");
                    }
                }
            }

            //1305
            else if (action.equals(Bg1Profile.ACTION_BG1_DEVICE_READY)) {
                addLogInfo("device handshake");
            } else if (action.equals(Bg1Profile.ACTION_BG1_IDPS)) {
                String idps = intent.getStringExtra(Bg1Profile.BG1_IDPS);
                addLogInfo("idps =" + idps);
            } else if (action.equals(Bg1Profile.ACTION_BG1_CONNECT_RESULT)) {
                int flag = intent.getIntExtra(Bg1Profile.BG1_CONNECT_RESULT, -1);
                addLogInfo("conect flag =" + flag);
                if (flag == 0) {
                    addLogInfo("connect success,please send code");
                    mBg1Control.sendCode(QRCode, Bg1Profile.CODE_GOD, Bg1Profile.MEASURE_CTL);
//                    请将试纸插入血糖仪
                    setStepView("two");
                } else {
                    addLogInfo("connect failed");
                    mBg1Control.disconnect();
                    setStepView("failed");
                }
            } else if (action.equals(Bg1Profile.ACTION_BG1_SENDCODE_RESULT)) {
                int flag = intent.getIntExtra(Bg1Profile.BG1_SENDCODE_RESULT, -1);
                addLogInfo("sendCode flag = " + flag);
                if (flag == 0) {
                    addLogInfo("sendCode success,ready to  measure");
                } else {
                    addLogInfo("sendCode failed");
                    mBg1Control.disconnect();
                    setStepView("failed");
                }
            } else if (action.equals(Bg1Profile.ACTION_BG1_MEASURE_ERROR)) {
                int errorNum = intent.getIntExtra(Bg1Profile.BG1_MEASURE_ERROR, -1);
                String error = intent.getStringExtra(Bg1Profile.BG1_MEASURE_ERROR_DESCRIPTION);
                addLogInfo("msgError = " + errorNum);
                addLogInfo("error information = " + error);
                setStepView("failed");
                //resend code to fix error 4
                if (errorNum == 4) {
                    mBg1Control.sendCode(QRCode, Bg1Profile.CODE_GOD, Bg1Profile.MEASURE_BLOOD);
                }
//                拔出试条再次插入可重新测量
            } else if (action.equals(Bg1Profile.ACTION_BG1_MEASURE_STRIP_IN)) {
                if (!isGetStripInBg1) {
                    isGetStripInBg1 = true;
                    addLogInfo("Strip In");
//                    请将试纸底端接触手指采血处
                    setStepView("three");
                }
                new Thread() {
                    @Override
                    public void run() {
                        SystemClock.sleep(3000);
                        isGetStripInBg1 = false;
                    }
                }.start();
            } else if (action.equals(Bg1Profile.ACTION_BG1_MEASURE_GET_BLOOD)) {
                if (!isGetBloodBg1) {
                    isGetBloodBg1 = true;
                    addLogInfo("Get Blood");
//                    正在检测，请稍后
                    setStepView("blood");
                }
                new Thread() {
                    @Override
                    public void run() {
                        SystemClock.sleep(3000);
                        isGetBloodBg1 = false;
                    }
                }.start();
            } else if (action.equals(Bg1Profile.ACTION_BG1_MEASURE_RESULT)) {
                if (!isGetResultBg1) {
                    isGetResultBg1 = true;
                    int measureResult = intent.getIntExtra(Bg1Profile.BG1_MEASURE_RESULT, -1);
                    String dataId = intent.getStringExtra(Bg1Profile.DATA_ID);
                    addLogInfo("dataId = " + dataId);
                    addLogInfo("msgResult = " + measureResult);
                    ToastUtils.showToast(BGMeasureActivity.this, "血糖值为：" + measureResult);
                    Log.d(TAG, measureResult + "");
                    Intent itDetect = new Intent(BGMeasureActivity.this, MeasureResultActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("day", DateUtils.formatDay(new Date().toString()));
                    bundle.putString("measureTime", measureTime);
                    bundle.putString("value", measureResult + "");
                    itDetect.putExtras(bundle);
                    startActivity(itDetect);
                    finish();
                }
//                拔出试条再次插入可重新测量
                new Thread() {
                    @Override
                    public void run() {
                        SystemClock.sleep(3000);
                        isGetResultBg1 = false;
                    }
                }.start();

            } else if (action.equals(Bg1Profile.ACTION_BG1_MEASURE_STRIP_OUT)) {
                addLogInfo("Strip Out");
//                请将试纸插入血糖仪
                setStepView("two");
            } else if (action.equals(Bg1Profile.ACTION_BG1_MEASURE_STANDBY)) {
                mBg1Control.disconnect();
                if (!isGetResultBg1) {
                    isGetResultBg1 = true;
                    addLogInfo("Stand By");
                }
                new Thread() {
                    @Override
                    public void run() {
                        SystemClock.sleep(3000);
                        isGetResultBg1 = false;
                    }
                }.start();
            }
        }
    };


    @Override
    protected void onDestroy() {
        if (mBg1Control != null) {
            mBg1Control.disconnect();
        }
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    @OnClick({R.id.common_header_back_layout, R.id.ll_meal})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.common_header_back_layout:
                finish();
                break;
            case R.id.ll_meal:
                showPopupWindow();
                break;
        }
    }

}
