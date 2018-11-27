package com.code.enums;

public enum WFActionFlagsEnum {

    REJECT(0),
    APPROVE(1),
    RETURN_REVIEWER(2),
    RECOMMEND_APPROVE(3),
    RETURN_TO_PREPARATOR(4),
    RETURN_TO_COMMITTEE_SECRETARY(5),
    RETURN_TO_COMMITTEE_PRESIDENT(6),
    RECOMMEND_REVIEW(7),
    SEND_FOR_ADVISE(8),
    REDIRECT(9),
    DISCLAIM(10),
    CLAIM(11),
    SENT_BACK_TO_UNITS(12);

    private int code;

    private WFActionFlagsEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}