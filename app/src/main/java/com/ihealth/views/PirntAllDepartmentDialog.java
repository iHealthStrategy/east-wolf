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
import android.widget.TextView;

import com.ihealth.BaseDialog;
import com.ihealth.Printer.BluetoothPrinter;
import com.ihealth.Printer.BluetoothPrinterStatus;
import com.ihealth.Printer.PrinterStatusResponse;
import com.ihealth.bean.AppointmentsBean;
import com.ihealth.facecheckinapp.R;
import com.ihealth.utils.ScreenUtils;

import java.util.Calendar;

/**
 * 全科室就诊小条dialog
 * Created by Liuhuan on 2019/09/23.
 */
public class PirntAllDepartmentDialog extends Dialog implements View.OnClickListener {

    private BaseDialog dialogPrint;
    private Context mContext;
    private TextView tv_print_title,btn_print,btn_cancel,tv_name,tv_diseases_ype,tv_last_time,tv_last_doctor;
    private AppointmentsBean appointmentsBean;
    private BluetoothPrinter bluetoothPrinter;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(false);
    }

    public PirntAllDepartmentDialog(Context context,AppointmentsBean appointmentsBean) {
        super(context);
        this.mContext = context;
        this.appointmentsBean = appointmentsBean;
        dialogPrint = new BaseDialog(mContext);

        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_dialog_print_all_department, null);
        tv_print_title = view.findViewById(R.id.tv_print_title);
        tv_name = view.findViewById(R.id.tv_name);
        tv_diseases_ype = view.findViewById(R.id.tv_diseases_type);
        tv_last_time = view.findViewById(R.id.tv_last_time);
        tv_last_doctor = view.findViewById(R.id.tv_last_doctor);


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
//
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
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_dialog_cancel:
                dialogPrint.dismiss();
                break;
            case R.id.btn_dialog_print:
                initBluetooth(appointmentsBean);
                dialogPrint.dismiss();
                dialogPrint.dismiss();
                break;
        }
    }

}