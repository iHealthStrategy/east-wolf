package com.ihealth.events;
/**
 * 关闭注册 选择科室 注册结果页面的 event
 * Created by Wangyuxu on 2019/09/23.
 */
public class FinshRegisterSelectTypeAndResultEvent {
    private String message;

    public FinshRegisterSelectTypeAndResultEvent(String message) {
        this.message = message;
    }

    public FinshRegisterSelectTypeAndResultEvent() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
