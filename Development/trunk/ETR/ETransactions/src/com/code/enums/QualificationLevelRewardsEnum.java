package com.code.enums;

public enum QualificationLevelRewardsEnum {
    PRIMARY_SCHOOL(1),
    INTERMEDIATE_EDUCATION(2),
    SECONDARY_SCHOOL(3);

    private int code;

    private QualificationLevelRewardsEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
