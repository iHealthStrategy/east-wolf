package com.ihealth.bean;

/**
 * 人脸检测返回数据Bean类
 *
 * @author liyanwen
 * @date 2019-02-14
 */
public class AddUserBean {
    private boolean resultStatus;
    private String resultMessage;
    private resultContent resultContent;
    private static class resultContent {
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

        @Override
        public String toString() {
            return "resultContent{" +
                    "userId='" + userId + '\'' +
                    ", phoneNumber='" + phoneNumber + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", idCard='" + idCard + '\'' +
                    '}';
        }
    }

    public Boolean getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(Boolean resultStatus) {
        this.resultStatus = resultStatus;
    }

    public resultContent getResultContent() {
        return resultContent;
    }

    public void setResultContent(resultContent resultContent) {
        this.resultContent = resultContent;
    }

    public boolean isResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(boolean resultStatus) {
        this.resultStatus = resultStatus;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    @Override
    public String toString() {
        return "AddUserBean{" +
                "resultStatus=" + resultStatus +
                ", resultContent = "+ resultContent.toString() +
                ", resultMessage='" + resultMessage + '\'' +
                '}';
    }
}
