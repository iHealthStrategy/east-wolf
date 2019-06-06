package com.ihealth.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ihealth.BaseDialog;
import com.ihealth.bean.AppointmentsBean;
import com.ihealth.facecheckinapp.R;
import com.ihealth.utils.ScreenUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 打印就诊小条dialog
 * Created by Liuhuan on 2019/06/04.
 */
public class PrintContentDialog extends Dialog implements View.OnClickListener {

    private BaseDialog dialogPrint;
    private Context mContext;
    private LinearLayout ll_blood,ll_foot,ll_eye,ll_insulin,ll_nutrition,ll_teach,ll_quantization;
    private TextView tv_print_title,tv_print_name,btn_print,btn_cancel,tv_height;
    private  AppointmentsBean appointmentsBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(false);
    }

    public PrintContentDialog(Context context,AppointmentsBean appointmentsBean) {
        super(context);
        this.mContext = context;
        this.appointmentsBean = appointmentsBean;
        dialogPrint = new BaseDialog(mContext);

        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_dialog_print_content, null);
        ll_blood = view.findViewById(R.id.ll_blood);
        ll_foot = view.findViewById(R.id.ll_foot);
        ll_eye = view.findViewById(R.id.ll_eye);
        ll_insulin = view.findViewById(R.id.ll_insulin);
        ll_nutrition = view.findViewById(R.id.ll_nutrition);
        ll_teach = view.findViewById(R.id.ll_teach);
        ll_quantization = view.findViewById(R.id.ll_quantization);
        tv_print_title = view.findViewById(R.id.tv_print_title);
        tv_print_name = view.findViewById(R.id.tv_print_name);
        tv_height = view.findViewById(R.id.tv_height);

        btn_cancel = view.findViewById(R.id.btn_dialog_cancel);
        btn_print = view.findViewById(R.id.btn_dialog_print);

        btn_cancel.setOnClickListener(this);
        btn_print.setOnClickListener(this);

        AppointmentsBean.Patient patient = appointmentsBean.getPatient();
        AppointmentsBean.Appointments appointments = appointmentsBean.getAppointments();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int date = c.get(Calendar.DATE);
        String currentDay = year+"年"+month+"月"+date+"日";
        tv_print_title.setText("本次门诊就诊项目（"+currentDay+"）");
        if(patient != null && appointments != null){
            String type;
            if(appointments.getType().equals("first")){
                type = "初诊";
            } else if(appointments.getType().equals("addition")){
                type = "加诊";
            } else if(appointments.getType().equals("year")){
                type = "年诊";
            } else {
                type = "复诊";
            }
            tv_print_name.setText(patient.getNickname()+"/"+type+"/医生："+patient.getDoctor());
            tv_height.setText("身高："+patient.getHeight()+"cm");
            
            if (!"true".equals(appointments.getBlood())) {
                ll_blood.setVisibility(View.GONE);
            }
            if (!"true".equals(appointments.getFootAt())) {
                ll_foot.setVisibility(View.GONE);
            }
            if (!"true".equals(appointments.getEyeGroundAt())) {
                ll_eye.setVisibility(View.GONE);
            }
            if (!"true".equals(appointments.getInsulinAt())) {
                ll_insulin.setVisibility(View.GONE);
            }
            if (!"true".equals(appointments.getNutritionAt())) {
                ll_nutrition.setVisibility(View.GONE);
            }
            if (!"true".equals(appointments.getHealthTech())) {
                ll_teach.setVisibility(View.GONE);
            }
            if (!"true".equals(appointments.getQuantizationAt())) {
                ll_quantization.setVisibility(View.GONE);
            }
        }

        dialogPrint.setContentView(view);
        dialogPrint.setCancelable(false);
        WindowManager.LayoutParams params = dialogPrint.getWindow().getAttributes();
        ScreenUtils screenUtils = new ScreenUtils(mContext);
        params.width = screenUtils.getScreenWidthPix()*3/8;
        params.height = screenUtils.getScreenHeightPix()*9/10;
        dialogPrint.getWindow().setAttributes(params);
        if (null != dialogPrint && !dialogPrint.isShowing())
        {
            dialogPrint.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_dialog_cancel:
                dialogPrint.dismiss();
                break;
            case R.id.btn_dialog_print:
                dialogPrint.dismiss();
                break;
        }
    }

}
