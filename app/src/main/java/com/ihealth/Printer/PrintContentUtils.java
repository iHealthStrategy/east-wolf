package com.ihealth.Printer;

import com.ihealth.bean.AppointmentsBean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 就诊小条需要打印的内容
 * Created by Liuhuan on 2019/06/13.
 */
public class PrintContentUtils {

    public String getPringContent(AppointmentsBean appointmentsBean){
        String content = "";
        AppointmentsBean.Patient patient = appointmentsBean.getPatient();
        AppointmentsBean.Appointments appointments = appointmentsBean.getAppointment();
        List<AppointmentsBean.PatientReport> dataList = appointmentsBean.getPatientReport();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int date = c.get(Calendar.DATE);
        Date now = new Date();
        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
        String currSun = dateFm.format(now);
        String currentDay = year+"年"+month+"月"+date+"日 " + currSun;

        String title = "   本次门诊就诊项目（"+currentDay+"）\n \n";
        String name;
        String height;
        String diseaseContent = "";

        if(dataList != null && dataList.size() > 0){
            diseaseContent = diseaseTitle();
            for(int i = 0; i < dataList.size(); i++){
                diseaseContent += diseaseProcessContent(dataList.get(i).getStartDate(),
                        dataList.get(i).getEndDate(),dataList.get(i).getContent());
            }
        }
        if(patient != null) {
            String type = "";
            if(appointments != null){
                type = appointments.getType();
            }
            if ("first".equals(type)) {
                type = "初诊";
            } else if ("addition".equals(type)) {
                type = "加诊";
            } else if ("year".equals(type)) {
                type = "年诊";
            } else {
                type = "复诊";
            }
            name = "          " +  patient.getNickname() + "/" + type + "/医生：" + patient.getDoctor() + "\n\n";
            height = patient.getHeight();
            content = title + name + baseContent(height) + bloodContent() + quantizationContent() +
                    nutritionContent() + insulinContent() + footContent() + eyeContent() + doctorAskContent()
                    + teachContent() + diseaseContent + payContent();
            return content;
        }
        return content;
    }

    private String line(){
        return "______________________________________________";
    }

    private String baseContent(String height){
        String content = "______________________________________________\n"+
                         " \n体征测量    腰围    体重    " + "身高："+height+" cm\n \n"+
                         "            血压    心率    空腹/餐后\n"+
                         "----------------------------------------------\n";
        return content;
    }

    private String bloodContent(){
        String content = "化验检查    糖化    尿微\n"+
                         "----------------------------------------------\n";
        return content;
    }

    private String quantizationContent(){
        String content = "量表填写\n"+
                         "----------------------------------------------\n";
        return content;
    }

    private String nutritionContent(){
        String content = "营养评估\n"+
                         "----------------------------------------------\n";
        return content;
    }

    private String insulinContent(){
        String content = "胰岛素注射部位评估\n"+
                         "----------------------------------------------\n";
        return content;
    }

    private String footContent(){
        String content = "足部评估   □ 血管    □ 神经 \n \n"+
                         "           □ ABI     □ TBI    □ 躯体感觉\n"+
                         "----------------------------------------------\n";
        return content;
    }

    private String eyeContent(){
        String content = "眼底检查\n"+
                         "----------------------------------------------\n";
        return content;
    }

    private String doctorAskContent(){
        String content = "医生问诊\n"+
                         "----------------------------------------------\n";
        return content;
    }

    private String diseaseTitle(){
        String content = "总结\n \n";
        return content;
    }

    private String diseaseProcessContent(String startDate, String endDate, String text){
        String content = startDate + "----" + endDate + "\n" +
                "本周总结： " + text +"\n"+
                "----------------------------------------------\n";
        return content;
    }

    private String teachContent(){
        String content = "健康知识教育\n"+
                         "----------------------------------------------\n";
        return content;
    }

    private String payContent(){
        String content = "\n 缴费   缴费后请把部分单据交回给护士\n"+
                         "______________________________________________\n";
        return content;
    }

}
