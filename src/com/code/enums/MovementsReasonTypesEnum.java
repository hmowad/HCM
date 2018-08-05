package com.code.enums;

public enum MovementsReasonTypesEnum {
    
    BASED_ON_HIS_REQUEST(1), // bona2an 3ala talaboh
    FOR_WORK_INTEREST(2), // le masla7et el 3amal
    FOR_PUBLIC_INTEREST(3); // lel masl7ah el 3amah
   
    private int code;

    private MovementsReasonTypesEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
