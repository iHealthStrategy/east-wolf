package com.ihealth.events;
/**
 * 闭检测人脸 注册 还有结果页面的 event
 * Created by Wangyuxu on 2019/09/23.
 */

public class FinshDetectRegisterAndResultEvent {
    private String message;

    public FinshDetectRegisterAndResultEvent(String message) {
        this.message = message;
    }

    public FinshDetectRegisterAndResultEvent() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
