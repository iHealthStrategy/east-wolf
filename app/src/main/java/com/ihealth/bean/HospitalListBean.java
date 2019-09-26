package com.ihealth.bean;

import java.io.Serializable;
import java.util.List;

public class HospitalListBean implements Serializable {
    private int resultStatus;
    private String resultMessage;
    private List<HospitalBean> resultContent;

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

    public List<HospitalBean> getResultContent() {
        return resultContent;
    }

    public void setResultContent(List<HospitalBean> resultContent) {
        this.resultContent = resultContent;
    }
}
