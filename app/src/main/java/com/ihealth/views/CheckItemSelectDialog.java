package com.ihealth.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ihealth.BaseDialog;
import com.ihealth.bean.AppointmentsBean;
import com.ihealth.facecheckin.R;

/**
 * 选择检查项目dialog
 * Created by Liuhuan on 2019/06/04.
 */
public class CheckItemSelectDialog extends Dialog implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {

    private BaseDialog dialogCheck;
    private Context mContext;
    private LinearLayout ll_blood,ll_foot,ll_eye,ll_insulin,ll_nutrition,ll_teach,ll_quantization;
    private CheckBox cb_blood,cb_foot,cb_eye,cb_insulin,cb_nutrition,cb_teach,cb_quantization;
    private TextView btn_cancel,btn_ok;
    private  AppointmentsBean appointmentsBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(false);
    }

    public CheckItemSelectDialog(Context context, AppointmentsBean appointmentsBean) {
        super(context);
        this.mContext = context;
        this.appointmentsBean = appointmentsBean;
        dialogCheck = new BaseDialog(mContext);

        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_dialog_select_check, null);
        ll_blood = view.findViewById(R.id.ll_blood);
        ll_foot = view.findViewById(R.id.ll_foot);
        ll_eye = view.findViewById(R.id.ll_eye);
        ll_insulin = view.findViewById(R.id.ll_insulin);
        ll_nutrition = view.findViewById(R.id.ll_nutrition);
        ll_teach = view.findViewById(R.id.ll_teach);
        ll_quantization = view.findViewById(R.id.ll_quantization);

        cb_blood = view.findViewById(R.id.cb_blood);
        cb_foot = view.findViewById(R.id.cb_foot);
        cb_eye = view.findViewById(R.id.cb_eye);
        cb_insulin = view.findViewById(R.id.cb_insulin);
        cb_nutrition = view.findViewById(R.id.cb_nutrition);
        cb_teach = view.findViewById(R.id.cb_teach);
        cb_quantization = view.findViewById(R.id.cb_quantization);

        btn_cancel = view.findViewById(R.id.btn_dialog_cancel);
        btn_ok = view.findViewById(R.id.btn_dialog_ok);

        ll_blood.setOnClickListener(this);
        ll_foot.setOnClickListener(this);
        ll_eye.setOnClickListener(this);
        ll_insulin.setOnClickListener(this);
        ll_nutrition.setOnClickListener(this);
        ll_teach.setOnClickListener(this);
        ll_quantization.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_ok.setOnClickListener(this);

        cb_blood.setOnCheckedChangeListener(this);
        cb_foot.setOnCheckedChangeListener(this);
        cb_eye.setOnCheckedChangeListener(this);
        cb_insulin.setOnCheckedChangeListener(this);
        cb_nutrition.setOnCheckedChangeListener(this);
        cb_teach.setOnCheckedChangeListener(this);
        cb_quantization.setOnCheckedChangeListener(this);

        AppointmentsBean.Appointments appointments = appointmentsBean.getAppointment();
        if(appointments == null){
            appointments = null;
            appointments.setBlood("true");
            appointments.setEyeGroundAt("true");
            appointments.setInsulinAt("true");
            appointments.setNutritionAt("true");
            appointments.setHealthTech("true");
            appointments.setQuantizationAt("true");
            appointments.setFootAt("true");
        }
        if(appointments != null){
            if ("true".equals(appointments.getBlood())) {
                cb_blood.setChecked(true);
            } else {
                cb_blood.setChecked(false);
            }
            if ("true".equals(appointments.getFootAt())) {
                cb_foot.setChecked(true);
            } else {
                cb_foot.setChecked(false);
            }
            if ("true".equals(appointments.getEyeGroundAt())) {
                cb_eye.setChecked(true);
            } else {
                cb_eye.setChecked(false);
            }
            if ("true".equals(appointments.getInsulinAt())) {
                cb_insulin.setChecked(true);
            } else {
                cb_insulin.setChecked(false);
            }
            if ("true".equals(appointments.getNutritionAt())) {
                cb_nutrition.setChecked(true);
            } else {
                cb_nutrition.setChecked(false);
            }
            if ("true".equals(appointments.getHealthTech())) {
                cb_teach.setChecked(true);
            } else {
                cb_teach.setChecked(false);
            }
            if ("true".equals(appointments.getQuantizationAt())) {
                cb_quantization.setChecked(true);
            } else {
                cb_quantization.setChecked(false);
            }
        }

        dialogCheck.setContentView(view);
        dialogCheck.setCancelable(false);
        if (null != dialogCheck && !dialogCheck.isShowing())
        {
            dialogCheck.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ll_blood:
                appointmentsBean.getAppointment().setBlood(!cb_blood.isChecked()+"");
                cb_blood.setChecked(!cb_blood.isChecked());
                break;
            case R.id.ll_foot:
                appointmentsBean.getAppointment().setFootAt(!cb_blood.isChecked()+"");
                cb_foot.setChecked(!cb_foot.isChecked());
                break;
            case R.id.ll_eye:
                appointmentsBean.getAppointment().setEyeGroundAt(!cb_blood.isChecked()+"");
                cb_eye.setChecked(!cb_eye.isChecked());
                break;
            case R.id.ll_insulin:
                appointmentsBean.getAppointment().setInsulinAt(!cb_blood.isChecked()+"");
                cb_insulin.setChecked(!cb_insulin.isChecked());
                break;
            case R.id.ll_nutrition:
                appointmentsBean.getAppointment().setInsulinAt(!cb_blood.isChecked()+"");
                cb_nutrition.setChecked(!cb_nutrition.isChecked());
                break;
            case R.id.ll_teach:
                appointmentsBean.getAppointment().setHealthTech(!cb_blood.isChecked()+"");
                cb_teach.setChecked(!cb_teach.isChecked());
                break;
            case R.id.ll_quantization:
                appointmentsBean.getAppointment().setQuantizationAt(!cb_blood.isChecked()+"");
                cb_quantization.setChecked(!cb_quantization.isChecked());
                break;
            case R.id.btn_dialog_cancel:
                dialogCheck.dismiss();
                break;
            case R.id.btn_dialog_ok:
                dialogCheck.dismiss();
                new PrintContentDialog(mContext,appointmentsBean);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch(buttonView.getId()){
            case R.id.cb_blood:

                setLinearLayoutBackground(ll_blood,isChecked);
                break;
            case R.id.cb_foot:
                setLinearLayoutBackground(ll_foot,isChecked);
                break;
            case R.id.cb_eye:
                setLinearLayoutBackground(ll_eye,isChecked);
                break;
            case R.id.cb_insulin:
                setLinearLayoutBackground(ll_insulin,isChecked);
                break;
            case R.id.cb_nutrition:
                setLinearLayoutBackground(ll_nutrition,isChecked);
                break;
            case R.id.cb_teach:
                setLinearLayoutBackground(ll_teach,isChecked);
                break;
            case R.id.cb_quantization:
                setLinearLayoutBackground(ll_quantization,isChecked);
                break;
        }
    }

    private void setLinearLayoutBackground(LinearLayout layout, Boolean isChecked){
        if(isChecked){
            layout.setBackground(mContext.getResources().getDrawable(R.drawable.button_white_bg));
        } else {
            layout.setBackground(mContext.getResources().getDrawable(R.drawable.bg_blood_check));
        }
    }

}
