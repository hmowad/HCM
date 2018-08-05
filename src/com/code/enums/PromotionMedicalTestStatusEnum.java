package com.code.enums;

public enum PromotionMedicalTestStatusEnum {
    NON_TESTED(5),
    EXEMPT(10),
    CURRENTLY_TESTING(15),
    NEGATIVE(20),
    POSITIVE(25),
    SENT_TO_HOSPITAL(30);

    private int code;

    private PromotionMedicalTestStatusEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
