package com.code.enums;

public enum PromotionReportStatusEnum {
    CURRENT(5),
    UNDER_REVIEW(10),
    UNDER_APPROVAL(15),
    CLOSED(20);

    private long code;

    private PromotionReportStatusEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}
