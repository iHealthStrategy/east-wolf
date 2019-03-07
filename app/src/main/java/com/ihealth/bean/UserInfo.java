package com.ihealth.bean;

public class UserInfo {
    private String phoneNumber;
    private String nickname;
    private String idCard;
    private String socialInsurance;

    public UserInfo(String phoneNumber, String nickname, String idCard, String socialInsurance) {
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.idCard = idCard;
        this.socialInsurance = socialInsurance;
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

    public String getSocialInsurance() {
        return socialInsurance;
    }

    public void setSocialInsurance(String socialInsurance) {
        this.socialInsurance = socialInsurance;
    }
}
