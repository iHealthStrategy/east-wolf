package com.ihealth.events;
/**
 * 注册 还有结果页面的 event
 * Created by Wangyuxu on 2019/09/23.
 */
public class FinshRegisterAndResultEvent {
    private String message;

    public FinshRegisterAndResultEvent(String message) {
        this.message = message;
    }

    public FinshRegisterAndResultEvent() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
