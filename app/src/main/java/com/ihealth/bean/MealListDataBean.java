package com.ihealth.bean;

import java.io.Serializable;

/**
 * 测量时间的实体类
 * Created by Liuhuan on 2020/05/07.
 */
public class MealListDataBean implements Serializable {

    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
