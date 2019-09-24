package com.ihealth.bean;

import java.io.Serializable;

public class FaceDetectResultByPhone implements Serializable {
    private int resultStatus;
    private String resultMessage;
    private ResultContent resultContent;
    public static class ResultContent {
        private String userId;
        private String phoneNumber;
        private String nickname;
        private String idCard;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
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

    public int getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(int resultStatus) {
        this.resultStatus = resultStatus;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public ResultContent getResultContent() {
        return resultContent;
    }

    public void setResultContent(ResultContent resultContent) {
        this.resultContent = resultContent;
    }
}
