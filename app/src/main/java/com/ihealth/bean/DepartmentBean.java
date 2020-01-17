package com.ihealth.bean;

import java.io.Serializable;
import java.util.List;

public class DepartmentBean implements Serializable {
    private String name;
    private String value;

    private List<OfficesType> diseaseTypes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<OfficesType> getDiseaseTypes() {
        return diseaseTypes;
    }

    public void setDiseaseTypes(List<OfficesType> diseaseTypes) {
        this.diseaseTypes = diseaseTypes;
    }
}
