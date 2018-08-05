package com.code.enums;

public enum RequestTypesEnum {
    NEW(1),
    MODIFY(2),
    INTERRUPT(3),
    CANCEL(4);
    
    private int code;
      
    private RequestTypesEnum(int code){
        this.code = code;
    }
    
    public int getCode(){return code;}
}
