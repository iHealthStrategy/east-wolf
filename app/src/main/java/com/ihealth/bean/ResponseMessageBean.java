package com.ihealth.bean;

import java.io.Serializable;

/**
 * 人脸检测返回数据Bean类
 *
 * @author liyanwen
 * @date 2019-02-14
 */
public class ResponseMessageBean implements Serializable {
    private int resultStatus;
    private String resultMessage;
    private AppointmentsBean resultContent;
    public static class resultContent {
        private String userId;
        private String phoneNumber;
        private String nickname;
        private String idCard;
        private String socialInsurance;

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

        public String getSocialInsurance() {
            return socialInsurance;
        }

        public void setSocialInsurance(String socialInsurance) {
            this.socialInsurance = socialInsurance;
        }

        @Override
        public String toString() {
            return "resultContent{" +
                    "userId='" + userId + '\'' +
                    ", phoneNumber='" + phoneNumber + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", idCard='" + idCard + '\'' +
                    ", socialInsurance='" + socialInsurance + '\'' +
                    '}';
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

    public AppointmentsBean getResultContent() {
        return resultContent;
    }

    public void setResultContent(AppointmentsBean resultContent) {
        this.resultContent = resultContent;
    }
}
