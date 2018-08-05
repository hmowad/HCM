package com.code.enums;

public enum RecruitmentTypeEnum {
    GRADUATION_LETTER(0),
    RECRUITMENT(1),
    RE_RECRUITMENT(2),
    RECRUITMENT_BY_EXTERNAL_MOVE(3);

    private int code;
    
    private RecruitmentTypeEnum(int code) {
	this.code = code;
    }
    
    public int getCode() {
	return code;
    }
}
