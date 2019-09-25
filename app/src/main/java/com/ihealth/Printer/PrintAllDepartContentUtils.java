package com.ihealth.Printer;

import com.ihealth.bean.AppointmentsBean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 全科室患者就诊小条打印
 * Created by Liuhuan on 2019/09/23.
 */
public class PrintAllDepartContentUtils {
    public String getPringContent(AppointmentsBean appointmentsBean){
        Map<String, String> diseaseMap = new HashMap<>();
        diseaseMap.put("diabetes", "糖尿病");
        diseaseMap.put("thyroid", "甲状腺疾病");
        diseaseMap.put("adrenalGland", "肾上腺疾病");
        diseaseMap.put("pituitary", "垂体和下丘脑疾病");

        String content;
        AppointmentsBean.Patient patient = appointmentsBean.getPatient();
        AppointmentsBean.Appointments appointments = appointmentsBean.getAppointment();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int date = c.get(Calendar.DATE);
        Date now = new Date();
        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
        String currSun = dateFm.format(now);
        String currentDay = year+"年"+month+"月"+date+"日 " + currSun;

        String title = "   本次门诊就诊项目（"+currentDay+"）\n";

        content = title + line() + diseasesTypeContent(patient, diseaseMap) + nameContent(patient) + lastTimeContent(appointments) +
                lastDoctorContent(appointments) + thisTimeDoctorContent() + nextTimeContent() + timeContent() + tipContent();
        return content;
    }

    private String line(){
        return "______________________________________________";
    }


    private String diseasesTypeContent(AppointmentsBean.Patient patient, Map<String, String> diseaseMap){
        String disease = patient.getDisease();
        if(disease == null){
            disease = "";
        } else {
            disease = diseaseMap.get(disease);
        }

        String content = "\n \n所看病种          " + disease + "\n \n"+
                "----------------------------------------------\n \n";
        return content;
    }

    private String nameContent(AppointmentsBean.Patient patient){
        String content = "患者姓名          " + patient.getNickname() + "\n \n"+
                "----------------------------------------------\n \n";
        return content;
    }

    private String lastTimeContent(AppointmentsBean.Appointments appointments){
        String date = appointments.getDate();
        if(date == null){
            date = "--";
        }
        String content = "上次看诊时间      " + date + "\n \n"+
                "----------------------------------------------\n \n";
        return content;
    }

    private String lastDoctorContent(AppointmentsBean.Appointments appointments){
        String doctor = appointments.getDoctor();
        if(doctor == null){
            doctor = "--";
        }
        String content = "上次看诊医生      " + doctor + "\n \n"+
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
