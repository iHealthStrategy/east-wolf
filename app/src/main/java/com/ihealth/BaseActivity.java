package com.ihealth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ihealth.utils.FileUtils;
import com.ihealth.utils.StatusBarUtils;

import org.greenrobot.eventbus.EventBus;

public class BaseActivity extends AppCompatActivity {

    /** Global Log Information */
    private String mLogInformation = "";
    /** Device Name */
    public String mDeviceName = "";
    /** Device Mac */
    public String mDeviceMac = "";
    /** Handle Message What Code*/
    public static final int HANDLER_MESSAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.transparencyBar(this);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 设置全局设备信息
     * set global device information
     * @param deviceName device show name
     * @param deviceMac device mac
     */
    public void setDeviceInfo(String deviceName, String deviceMac) {
        mDeviceName = deviceName;
        mDeviceMac = deviceMac;
    }

    /**
     * 将信息存储到日志中
     * Add information to the log
     * @param infomation
     */
    public void addLogInfo(String infomation) {
        if (infomation != null && !infomation.isEmpty()) {
            String infor =  infomation + " \n";
            mLogInformation += infor;
//            Toast.makeText(this,infor+"===="+mLogInformation,Toast.LENGTH_LONG).show();
//            FileUtils.writeData("");
            FileUtils.writeData(mLogInformation);
        }
    }



}
