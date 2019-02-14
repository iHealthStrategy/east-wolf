package com.ihealth.bean;

/**
 * 人脸检测返回数据Bean类
 *
 * @author liyanwen
 * @date 2019-02-14
 */
public class FaceDetectionBean {
    private int resultStatus;
    private String errorMessage;
    private static class ResultContent {
        private String userId;
        private String userName;
        private String idCard;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
