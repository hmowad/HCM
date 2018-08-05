package com.code.enums;

public enum CategoriesEnum {
    OFFICERS(1),
    SOLDIERS(2),
    PERSONS(3),
    USERS(4),
    WAGES(5),
    CONTRACTORS(6),
    MEDICAL_STAFF(9);
    
    private long code;
      
    private CategoriesEnum(long code){
        this.code = code;
    }
    
    public long getCode(){return code;}
}
