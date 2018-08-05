package com.code.enums;

public enum WFInstanceStatusEnum {
    RUNNING(1),         // Under processing.
    DONE(2),           // DB or Integration Effect has been done and notifications are still.
    COMPLETED(3);      // Closed.
     
    private int code;
     
    private WFInstanceStatusEnum(int code){
        this.code = code;
    }
     
    public int getCode(){return code;}
}