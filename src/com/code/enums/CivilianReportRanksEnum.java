package com.code.enums;

public enum CivilianReportRanksEnum {

    THE_HIGHER_RANKS(306),
    FROM_FIFTH_TO_NINTH(311),
    FROM_FIRST_TO_FOURTH(315);
    
    private long code;
    
    private CivilianReportRanksEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}
