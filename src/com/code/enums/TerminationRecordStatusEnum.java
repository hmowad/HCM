package com.code.enums;

public enum TerminationRecordStatusEnum {
    CURRENT(5),
    UNDER_APPROVAL(10),
    CLOSED(15);
    
    private long code;
      
    private TerminationRecordStatusEnum(long code){
        this.code = code;
    }
    
    public long getCode(){return code;}
}
