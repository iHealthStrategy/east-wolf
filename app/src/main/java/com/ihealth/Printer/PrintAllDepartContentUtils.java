package com.ihealth.Printer;

import com.ihealth.bean.AppointmentsBean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PrintAllDepartContentUtils {
    public String getPringContent(AppointmentsBean appointmentsBean){
        String content = "";
        AppointmentsBean.Patient patient = appointmentsBean.getPatient();
        AppointmentsBean.Appointments appointments = appointmentsBean.getAppointments();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int date = c.get(Calendar.DATE);
        Date now = new Date();
        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
        String currSun = dateFm.format(now);
        String currentDay = year+"年"+month+"月"+date+"日 " + currSun;

        String title = "   本次门诊就诊项目（"+currentDay+"）\n";

        content = title + line() + diseasesTypeContent() + nameContent() + lastTimeContent() + lastDoctorContent()
        + thisTimeDoctorContent() + nextTimeContent() + timeContent() + tipContent();
        return content;
    }

    private String line(){
        return "______________________________________________";
    }


    private String diseasesTypeContent(){
        String content = "\n \n所看病种\n \n"+
                "----------------------------------------------\n \n";
        return content;
    }

    private String nameContent(){
        String content = "患者姓名\n \n"+
                "----------------------------------------------\n \n";
        return content;
    }

    private String lastTimeContent(){
        String content = "上次看诊时间\n \n"+
                "----------------------------------------------\n \n";
        return content;
    }

    private String lastDoctorContent(){
        String content = "上次看诊医生\n \n"+
                "----------------------------------------------\n \n";
        return content;
    }

    private String thisTimeDoctorContent(){
        String content = "本次看诊医生\n \n"+
                "______________________________________________\n \n";
        return content;
    }

    private String nextTimeContent(){
        String content = "下次复诊时间\n \n";
        return content;
    }
    private String timeContent(){
        String content = "□ _________ 周后\n \n"+
                "□ _________ 月后\n \n"+
                "_____________________________________________";
        return content;
    }

    private String tipContent(){
        String content = "\n \n看诊结束后请将本小条交给相关工作人员。\n";
        return content;
    }
}
