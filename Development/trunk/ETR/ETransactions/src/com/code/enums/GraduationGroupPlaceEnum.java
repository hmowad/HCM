package com.code.enums;

public enum GraduationGroupPlaceEnum {
    KING_FAHD_SECURITY_COLLEGE(1),
    KING_FAHD_NAVAL_COLLEGE(2);
    
    private int code;
      
    private GraduationGroupPlaceEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
