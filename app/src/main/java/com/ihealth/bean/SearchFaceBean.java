package com.ihealth.bean;

public class SearchFaceBean {
    private boolean resultStatus;
    private String resultMessage;

    public Boolean getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(Boolean resultStatus) {
        this.resultStatus = resultStatus;
    }

    private static class resultContent {
        private String phoneNumber;
        private String nickName;
        private String idCard;

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }
    }
    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    @Override
    public String toString() {
        return "SearchFaceBean{" +
                "resultStatus='" + resultStatus + '\'' +
                ", resultMessage='" + resultMessage + '\'' +
                '}';
    }
}
