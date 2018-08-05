package com.code.enums;

public enum CategoryModesEnum {
    OFFICERS(1),
    SOLDIERS(2),
    CIVILIANS(3);
    
    private int code;
      
    private CategoryModesEnum(int code){
        this.code = code;
    }
    
    public int getCode(){return code;}
}
