package com.code.enums;

public enum LocationFlagsEnum {
    INTERNAL(0),
    EXTERNAL(1),
    INTERNAL_EXTERNAL(2);
    
    private int code;
      
    private LocationFlagsEnum(int code){
        this.code = code;
    }
    
    public int getCode(){return code;}
}
