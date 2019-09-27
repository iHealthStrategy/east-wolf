package com.ihealth.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ihealth.BaseActivity;
import com.ihealth.bean.AppointmentsBean;
import com.ihealth.events.FinshDetectRegisterAndResultEvent;
import com.ihealth.events.FinshDetectRegisterSelectTypeAndResultEvent;
import com.ihealth.events.FinshRegisterAndResultEvent;
import com.ihealth.events.FinshRegisterSelectTypeAndResultEvent;
import com.ihealth.facecheckinapp.R;
import com.ihealth.utils.BundleKeys;
import com.ihealth.utils.ConstantArguments;
import com.ihealth.views.PirntAllDepartmentDialog;
import com.ihealth.views.PrintContentDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * 注册新患者信息之后返回结果的acitivity
 * Created by Wangyuxu on 2019/09/23.
 */

public class RegisterResultActivity extends BaseActivity {

    @BindView(R.id.common_header_title)
    TextView commonHeaderTitle;
    @BindView(R.id.common_header_back_layout)
    LinearLayout commonHeaderBackLayout;
    @BindView(R.id.activity_register_result_iv)
    ImageView activityRegisterResultIv;
    @BindView(R.id.activity_register_result_name)
    TextView activityRegisterResultName;
    @BindView(R.id.activity_register_result_message)
    TextView activityRegisterResultMessage;
    @BindView(R.id.activity_register_result_btn)
    Button activityRegisterResultBtn;
    @BindView(R.id.activity_register_result_other)
    TextView activityRegisterResultOther;
    int[] drawables = {R.drawable.register_success, R.drawable.register_failed, R.drawable.register_add};
    String[] messages = {"您的信息录入成功，请打印就诊小条准备门诊", "录入失败，重新填写信息", "识别失败次数过多，请现场咨询工作人员", "加诊咨询现场工作人员"};
    String[] titles = {"系统录入成功", "系统录入失败"};
    String[] btnMessages = {"打印就诊小条", "重新拍照", "确定"};
    private AppointmentsBean data;
    private AppointmentsBean.Patient mPatient;
    private int status;
    private boolean isComeFromSelectTypeUI = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_result);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();

        if (intent != null) {
            try {
                Bundle bundle = intent.getExtras();
                data = (AppointmentsBean) bundle.getSerializable(BundleKeys.APPOINTMENTSBEAN);
                mPatient = data.getPatient();
                status = bundle.getInt(BundleKeys.REGISTER_RESULT_STATUS, 0);
                isComeFromSelectTypeUI = bundle.getBoolean(BundleKeys.COME_FROM_SELECT_TYPE_UI, false);
                setUIResultByStatus(status);
            } catch (Exception e) {
                Log.e("123", e.toString());
            }
        }
    }

    private void setUIResultByStatus(int status) {
        switch (status) {
            case ConstantArguments.REGISTER_SUCESS:
                if (mPatient != null)
                    activityRegisterResultName.setText(mPatient.getNickname());
                commonHeaderTitle.setText(titles[0]);
                activityRegisterResultIv.setImageDrawable(getResources().getDrawable(drawables[0]));
                activityRegisterResultMessage.setText(messages[0]);
                activityRegisterResultBtn.setText(btnMessages[0]);
//                activityRegisterResultOther.setVisibility(View.VISIBLE);
                break;
            case ConstantArguments.REGISTER_FAILED:
                if (mPatient != null)
                    activityRegisterResultName.setText(mPatient.getNickname());
                commonHeaderTitle.setText(titles[1]);
                activityRegisterResultIv.setImageDrawable(getResources().getDrawable(drawables[1]));
                activityRegisterResultMessage.setText(messages[1]);
                activityRegisterResultBtn.setText(btnMessages[1]);
                activityRegisterResultOther.setVisibility(View.GONE);
                break;
            case ConstantArguments.REGISTER_FAILED_TO_TIMES:
                activityRegisterResultName.setText("您好");
                commonHeaderTitle.setText(titles[1]);
                activityRegisterResultIv.setImageDrawable(getResources().getDrawable(drawables[1]));
                activityRegisterResultMessage.setText(messages[2]);
                activityRegisterResultBtn.setText(btnMessages[2]);
                activityRegisterResultOther.setVisibility(View.GONE);
                break;
            case ConstantArguments.REGISTER_FAILED_ADD_CLINIC:
                activityRegisterResultName.setText("您好");
                commonHeaderTitle.setText(titles[1]);
                activityRegisterResultIv.setImageDrawable(getResources().getDrawable(drawables[2]));
                activityRegisterResultMessage.setText(messages[3]);
                activityRegisterResultBtn.setText(btnMessages[2]);
                activityRegisterResultOther.setVisibility(View.GONE);
                break;
        }
    }

    @OnClick({R.id.common_header_layout, R.id.activity_register_result_btn, R.id.activity_register_result_other})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.activity_register_result_btn:
                setResultActionByStatus(status);
                finshActivitysByEvent(status);
                break;
            case R.id.activity_register_result_other:
                break;
            case R.id.common_header_layout:
                break;
        }
    }

    private void setResultActionByStatus(int status) {
        switch (status) {
            case ConstantArguments.REGISTER_SUCESS://打印就诊小条

                break;
            case ConstantArguments.REGISTER_FAILED://录入失败，重新填写信息，重新拍照

                break;
            case ConstantArguments.REGISTER_FAILED_TO_TIMES://识别太多次数
            case ConstantArguments.REGISTER_FAILED_ADD_CLINIC://加诊


                break;
        }
    }

    private void finshActivitysByEvent(int status) {
        if (isComeFromSelectTypeUI) {
            switch (status) {
                case ConstantArguments.REGISTER_SUCESS://打印就诊小条
                    if (data != null && data.getPatient() != null) {
                        String patientType = data.getPatient().getPatientType();
                        if (patientType.equals("GTZH")) {

                            new PrintContentDialog(RegisterResultActivity.this, data).setOnPriterClicker(new PrintContentDialog.OnPriterClicker() {
                                @Override
                                public void onPriterClick() {
                                    EventBus.getDefault().post(new FinshDetectRegisterSelectTypeAndResultEvent("finsh掉Detect  RegisterPatient  selectType result 返回到主界面"));
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                        } else {
                            new PirntAllDepartmentDialog(RegisterResultActivity.this, data).setOnPriterClicker(new PirntAllDepartmentDialog.OnPriterClicker() {
                                @Override
                                public void onPriterClick() {
                                    EventBus.getDefault().post(new FinshDetectRegisterSelectTypeAndResultEvent("finsh掉Detect  RegisterPatient  selectType result 返回到主界面"));
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                        }
                    }
                    break;
                case ConstantArguments.REGISTER_FAILED_TO_TIMES://识别太多次数
                case ConstantArguments.REGISTER_FAILED_ADD_CLINIC://加诊

                    EventBus.getDefault().post(new FinshDetectRegisterSelectTypeAndResultEvent("finsh掉Detect  RegisterPatient  selectType result 返回到主界面"));
                    break;
                case ConstantArguments.REGISTER_FAILED://录入失败，重新填写信息，重新拍照
                    EventBus.getDefault().post(new FinshRegisterSelectTypeAndResultEvent("finsh掉RegisterPatient  selectType result ,返回到Detect"));
                    break;
            }
        } else {
            switch (status) {
                case ConstantArguments.REGISTER_SUCESS://打印就诊小条
                    if (data != null && data.getPatient() != null) {
                        String patientType = data.getPatient().getPatientType();
                        if (patientType.equals("GTZH")) {
                            new PrintContentDialog(RegisterResultActivity.this, data).setOnPriterClicker(new PrintContentDialog.OnPriterClicker() {
                                @Override
                                public void onPriterClick() {
                                    Toast.makeText(RegisterResultActivity.this, R.string.priter_sucess_toast, Toast.LENGTH_SHORT).show();
                                    EventBus.getDefault().post(new FinshDetectRegisterAndResultEvent("finsh掉Detect  RegisterPatient result 返回到主界面\""));
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                        } else {
                            new PirntAllDepartmentDialog(RegisterResultActivity.this, data).setOnPriterClicker(new PirntAllDepartmentDialog.OnPriterClicker() {
                                @Override
                                public void onPriterClick() {
                                    Toast.makeText(RegisterResultActivity.this, R.string.priter_sucess_toast, Toast.LENGTH_SHORT).show();
                                    EventBus.getDefault().post(new FinshDetectRegisterAndResultEvent("finsh掉Detect  RegisterPatient result 返回到主界面\""));
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                        }
                    }
                    break;
                case ConstantArguments.REGISTER_FAILED_TO_TIMES://识别太多次数
                case ConstantArguments.REGISTER_FAILED_ADD_CLINIC://加诊
                    EventBus.getDefault().post(new FinshDetectRegisterAndResultEvent("finsh掉Detect  RegisterPatient result 返回到主界面\""));

                    break;
                case ConstantArguments.REGISTER_FAILED://录入失败，重新填写信息，重新拍照
                    EventBus.getDefault().post(new FinshRegisterAndResultEvent("finsh掉RegisterPatient  selectType result ,返回到Detect"));
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FinshDetectRegisterAndResultEvent event){
        finish();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FinshDetectRegisterSelectTypeAndResultEvent event){
        finish();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FinshRegisterAndResultEvent event){
        finish();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FinshRegisterSelectTypeAndResultEvent event){
        finish();
    }
}
