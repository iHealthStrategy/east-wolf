package com.ihealth.events;
/**
 * 关闭检测人脸 注册 选择科室 注册结果页面的 event
 * Created by Wangyuxu on 2019/09/23.
 */
public class FinshDetectRegisterSelectTypeAndResultEvent {
    private String message;

    public FinshDetectRegisterSelectTypeAndResultEvent(String message) {
        this.message = message;
    }

    public FinshDetectRegisterSelectTypeAndResultEvent() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
