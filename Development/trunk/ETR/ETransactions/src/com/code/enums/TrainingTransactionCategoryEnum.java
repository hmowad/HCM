package com.code.enums;

public enum TrainingTransactionCategoryEnum {
    TRAINING_PLAN(1),
    NOMINATION(2),
    NOMINATION_APOLOGY(3),
    NOMINATION_REPLACEMENT(4),
    CLAIM(5),
    COURSE_EVENT_DECISION_REQUEST(6),
    COURSE_EVENT_DECISION_MODIFY_REQUEST(13),
    COURSE_EVENT_POSTPONEMENT_DECISION_REQUEST(7),
    COURSE_EVENT_CANCEL_DECISION_REQUEST(8),
    EMPLOYEE_COURSE_EVENT_CANCEL(9),
    EXTERNAL_COURSE_EVENT_ADDITION(10),
    EXTERNAL_COURSE_EVENT_POSTPONEMENT(11),
    EXTERNAL_COURSE_EVENT_CANCELLATION(12);

    private int code;

    private TrainingTransactionCategoryEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
