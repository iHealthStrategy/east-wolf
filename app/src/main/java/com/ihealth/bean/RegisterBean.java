package com.ihealth.bean;

/**
 * 注册接口返回数据的bean类
 *
 * @author liyanwen
 * @date 2019-02-14
 */
public class RegisterBean {
    private int resultStatus;
    private String errorMessage;

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
