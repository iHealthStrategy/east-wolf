package com.ihealth.bean;

import java.io.Serializable;

public class OfficesType implements Serializable {
    private String value;
    private String text;

    public OfficesType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public OfficesType() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
