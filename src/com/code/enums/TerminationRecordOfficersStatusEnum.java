package com.code.enums;

public enum TerminationRecordOfficersStatusEnum {
    UNDER_REVIEW(5),
    MINISTRY_SENT(10),
    EXTENDED(15),
    TERMINATED(20),
    REJECTED(25);

    private long code;

    private TerminationRecordOfficersStatusEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}
