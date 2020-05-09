package com.ihealth.bean;


import java.io.Serializable;
import java.util.List;

public class HospitalBean implements Serializable {
    private String hospitalId;
    private String hospitalCode;
    private String name;
    private String fullname;
    private List<DepartmentBean> department;
    public HospitalBean() {
    }

    public HospitalBean(String _id, String code, String name, String fullname) {
        this.hospitalId = _id;
        this.hospitalCode = code;
        this.name = name;
        this.fullname = fullname;
    }



    public String get_id() {
        return hospitalId;
    }

    public void set_id(String _id) {
        this.hospitalId = _id;
    }

    public String getCode() {
        return hospitalCode;
    }

    public void setCode(String code) {
        this.hospitalCode = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public List<DepartmentBean> getDepartment() {
        return department;
    }

    public void setDepartment(List<DepartmentBean> department) {
        this.department = department;
    }
}


