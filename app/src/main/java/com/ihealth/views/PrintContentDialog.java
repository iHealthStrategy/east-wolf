package com.ihealth.views;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ihealth.BaseDialog;
import com.ihealth.Printer.BluetoothPrinter;
import com.ihealth.Printer.BluetoothPrinterStatus;
import com.ihealth.Printer.PrinterStatusResponse;
import com.ihealth.Printer.adapter.DiseaseProcessAdapter;
import com.ihealth.bean.AppointmentsBean;
import com.ihealth.facecheckinapp.R;
import com.ihealth.utils.ScreenUtils;

import java.util.Calendar;
import java.util.List;

/**
 * 打印就诊小条dialog
 * Created by Liuhuan on 2019/06/04.
 */
public class PrintContentDialog extends Dialog implements View.OnClickListener {

    private BaseDialog dialogPrint;
    private Context mContext;
    private LinearLayout ll_disease;
    private TextView tv_print_title,tv_print_name,btn_print,btn_cancel,tv_height;
    private ListView lv_disease;
    private  AppointmentsBean appointmentsBean;
    private BluetoothPrinter bluetoothPrinter;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private OnPriterClicker mListener;

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
        tv_print_title = view.findViewById(R.id.tv_print_title);
        tv_print_name = view.findViewById(R.id.tv_print_name);
        tv_height = view.findViewById(R.id.tv_height);
        lv_disease = view.findViewById(R.id.lv_disease_process);
        ll_disease = view.findViewById(R.id.ll_disease);

        btn_cancel = view.findViewById(R.id.btn_dialog_cancel);
        btn_print = view.findViewById(R.id.btn_dialog_print);

        btn_cancel.setOnClickListener(this);
        btn_print.setOnClickListener(this);

        List<AppointmentsBean.PatientReport> dataList = appointmentsBean.getPatientReport();
        if(dataList != null && dataList.size() > 0){
            DiseaseProcessAdapter adapter = new DiseaseProcessAdapter(mContext, dataList);
            lv_disease.setAdapter(adapter);
        } else {
            ll_disease.setVisibility(View.GONE);
        }
        AppointmentsBean.Patient patient = appointmentsBean.getPatient();
        AppointmentsBean.Appointments appointments = appointmentsBean.getAppointment();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int date = c.get(Calendar.DATE);
        String currentDay = year+"年"+month+"月"+date+"日";
        tv_print_title.setText("本次门诊就诊项目（"+currentDay+"）");
        if(patient != null && appointments != null){
            String type = "复诊";
            if(appointments.getType() != null){
                if(appointments.getType().equals("first")){
                    type = "初诊";
                } else if(appointments.getType().equals("addition")){
                    type = "加诊";
                } else if(appointments.getType().equals("year")){
                    type = "年诊";
                } else {
                    type = "复诊";
                }
            }
            tv_print_name.setText(patient.getNickname()+"/"+type+"/医生："+patient.getDoctor());
            tv_height.setText("身高："+patient.getHeight()+" cm");
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

private void initBluetooth(AppointmentsBean appointments){
    bluetoothPrinter = new BluetoothPrinter(getContext(), appointments, new PrinterStatusResponse() {
        @Override
        public void onStatusChange(BluetoothPrinterStatus status) {
            switch (status){
            }

        }
    });
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//         Android M Permission check
        if (mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ((Activity)mContext).requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            return;
        }
    }
    bluetoothPrinter.searchAndConnect();
}

    @Override
    protected void onStop() {
        super.onStop();
        bluetoothPrinter.destroy();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_dialog_cancel:
                dialogPrint.dismiss();
                if(mListener!=null){
                    mListener.onCancel();
                }
                break;
            case R.id.btn_dialog_print:
                initBluetooth(appointmentsBean);
                if(mListener!=null){
                    mListener.onPriterClick();
                }
                dialogPrint.dismiss();
                break;
        }
    }
    public interface OnPriterClicker {
        public void onPriterClick();
        public void onCancel();
    }

    public void setOnPriterClicker(OnPriterClicker listener) {
        mListener = listener;
    }

}
