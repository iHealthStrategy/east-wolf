package com.ihealth.utils;

import com.ihealth.bean.MealListDataBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 血糖测量时间的工具类
 * Created by Liuhuan on 2020/05/07.
 */
public class MeasureMealDataUtils {

    public static List<MealListDataBean> getData() {
        List<MealListDataBean> mealDataList = new ArrayList<>();
        MealListDataBean dataBean1 = new MealListDataBean();
        MealListDataBean dataBean2 = new MealListDataBean();
        MealListDataBean dataBean3 = new MealListDataBean();
        MealListDataBean dataBean4 = new MealListDataBean();
        MealListDataBean dataBean5 = new MealListDataBean();
        MealListDataBean dataBean6 = new MealListDataBean();
        MealListDataBean dataBean7 = new MealListDataBean();
        MealListDataBean dataBean8 = new MealListDataBean();

        dataBean1.setKey("MIDNIGHT");
        dataBean1.setValue("凌晨");
        dataBean2.setKey("BEFORE_BREAKFAST");
        dataBean2.setValue("早餐前");
        dataBean3.setKey("AFTER_BREAKFAST");
        dataBean3.setValue("早餐后");
        dataBean4.setKey("BEFORE_LUNCH");
        dataBean4.setValue("午餐前");
        dataBean5.setKey("AFTER_LUNCH");
        dataBean5.setValue("午餐后");
        dataBean6.setKey("BEFORE_DINNER");
        dataBean6.setValue("晚餐前");
        dataBean7.setKey("AFTER_DINNER");
        dataBean7.setValue("晚餐后");
        dataBean8.setKey("BEFORE_SLEEPING");
        dataBean8.setValue("睡前");

        mealDataList.add(dataBean1);
        mealDataList.add(dataBean2);
        mealDataList.add(dataBean3);
        mealDataList.add(dataBean4);
        mealDataList.add(dataBean5);
        mealDataList.add(dataBean6);
        mealDataList.add(dataBean7);
        mealDataList.add(dataBean8);

        return mealDataList;
    }

//    const goal = {
//        BEFORE_BREAKFAST: { upper: 7, lower: 4 },
//        AFTER_BREAKFAST: { upper: 10, lower: 4 },
//        BEFORE_LUNCH: { upper: 7, lower: 4 },
//        AFTER_LUNCH: { upper: 10, lower: 4 },
//        BEFORE_DINNER: { upper: 7, lower: 4 },
//        AFTER_DINNER: { upper: 10, lower: 4 },
//        BEFORE_SLEEPING: { upper: 8, lower: 5 },
//        MIDNIGHT: { upper: 8, lower: 5 },
//        RANDOM: { upper: 30, lower: 1 }, // From Nick: 随机的人不管高低，都显示灰色, 高和低是随便写的
//    }

    public static String getMeasureTimeText(String measureTime){
        List<MealListDataBean> mealDataList = MeasureMealDataUtils.getData();
        String measureTimeText = "";
        for(int i = 0; i < mealDataList.size(); i++){
            MealListDataBean dataBean = mealDataList.get(i);
            if(dataBean.getKey().equals(measureTime)){
                measureTimeText = dataBean.getValue();
            }
        }
        return measureTimeText;
    }

    public static String getTargetValue(String measureTime){
        String target = "4 ~ 7";
        if(measureTime.equals("BEFORE_SLEEPING") || measureTime.equals("MIDNIGHT")){
            target = "5 ~ 8";
        } else if(measureTime.contains("AFTER")){
            target = "4 ~ 10";
        }
        return target;
    }

    public static String getControlInfo(String measureTime, String value){
        String info = "normal";
        if(measureTime.equals("BEFORE_SLEEPING") || measureTime.equals("MIDNIGHT")){
            if(Float.parseFloat(value) < 5){
                info = "low";
            } else if(Float.parseFloat(value) > 8){
                info = "high";
            }
        } else if(measureTime.contains("AFTER")){
            if(Float.parseFloat(value) < 4){
                info = "low";
            } else if(Float.parseFloat(value) > 10){
                info = "high";
            }
        } else {
            if(Float.parseFloat(value) < 4){
                info = "low";
            } else if(Float.parseFloat(value) > 7){
                info = "high";
            }
        }
        return info;
    }

    public static String getCurrentMeasureTime(){
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(calendar.HOUR_OF_DAY);
        if (currentHour >= 0 && currentHour <= 4) {
            return "MIDNIGHT";
        } else if (currentHour >= 5 && currentHour <= 8) {
            return "BEFORE_BREAKFAST";
        } else if (currentHour >= 9 && currentHour <= 10) {
            return "AFTER_BREAKFAST";
        } else if (currentHour >= 11 && currentHour <= 12) {
            return "BEFORE_LUNCH";
        } else if (currentHour >= 13 && currentHour <= 15) {
            return "AFTER_LUNCH";
        } else if (currentHour >= 16 && currentHour <= 18) {
            return "BEFORE_DINNER";
        } else if (currentHour >= 19 && currentHour <= 21) {
            return "AFTER_DINNER";
        }
        return "BEFORE_SLEEPING";
    }


}
