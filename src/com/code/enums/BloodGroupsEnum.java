package com.code.enums;

public enum BloodGroupsEnum {
    A_PLUS(1),
    A_MINUS(2),
    B_PLUS(3),
    B_MINUS(4),
    AB_PLUS(5),
    AB_MINUS(6),
    O_PLUS(7),
    O_MINUS(8);
    
    private int code;
      
    private BloodGroupsEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
