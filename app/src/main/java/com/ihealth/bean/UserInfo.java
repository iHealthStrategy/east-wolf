package com.ihealth.bean;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private String phoneNumber;
    private String nickname;
    private String idCard;
    private String disease;

    public UserInfo(String phoneNumber, String nickname, String idCard) {
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.idCard = idCard;
    }

    public UserInfo(String phoneNumber, String nickname, String idCard, String disease) {
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.idCard = idCard;
        this.disease = disease;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }
}
