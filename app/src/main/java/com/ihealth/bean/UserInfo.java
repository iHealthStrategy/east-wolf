package com.ihealth.bean;

public class UserInfo {
    private String phoneNumber;
    private String nickname;
    private String idCard;

    public UserInfo(String phoneNumber, String nickname, String idCard) {
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.idCard = idCard;
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


}
