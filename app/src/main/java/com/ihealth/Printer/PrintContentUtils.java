package com.ihealth.Printer;

import com.ihealth.bean.AppointmentsBean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 就诊小条需要打印的内容
 * Created by Liuhuan on 2019/06/13.
 */
public class PrintContentUtils {

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

        String title = "   本次门诊就诊项目（"+currentDay+"）\n \n";
        String name;
        String height;

//        if(patient != null && appointments != null){
//            String type= appointments.getType() ;
        String type= "first";
            if("first".equals(type)){
                type = "初诊";
            } else if("addition".equals(type)){
                type = "加诊";
            } else if("year".equals(type)){
                type = "年诊";
            } else {
                type = "复诊";
            }
            name = "          "+"五月天"+"/"+type+"/医生："+"张琳"+"\n\n";
//            height = patient.getHeight();
            height = "183";

            content = title + name + baseContent(height);

//            if ("true".equals(appointments.getBlood())) {
                content += bloodContent();
//            }
//            if ("true".equals(appointments.getQuantizationAt())) {
                content += quantizationContent();
//            }
//            if ("true".equals(appointments.getNutritionAt())) {
                content += nutritionContent();
//            }
//            if ("true".equals(appointments.getInsulinAt())) {
                content += insulinContent();
//            }
//            if ("true".equals(appointments.getFootAt())) {
                content += footContent();
//            }
//            if ("true".equals(appointments.getEyeGroundAt())) {
                content += eyeContent();
//            }
            content += doctorAskContent();
//            if ("true".equals(appointments.getHealthTech())) {
                content += teachContent();
//            }
            content += payContent();
//        }
        return content;
    }

    private String line(){
        return "______________________________________________";
    }

    private String baseContent(String height){
        String content = "______________________________________________\n"+
                         " \n体征测量    腰围    体重    " + "身高："+height+"cm\n \n"+
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

    private String teachContent(){
        String content = "健康知识教育\n"+
                         "----------------------------------------------\n";
        return content;
    }

    private String payContent(){
        String content = "缴费   缴费后请把部分单据交回给护士\n"+
                         "______________________________________________\n";
        return content;
    }

}
