package com.code.enums;

public enum TrainingCourseEventStatusEnum {
    PLANNED_TO_BE_HELD(1),
    COURSE_EVENT_HOLDING_DECISION_ISSUED(2),
    COURSE_EVENT_POSTPONEMENT_DECISION_ISSUED(3),
    COURSE_EVENT_CANCEL_DECISION_ISSUED(4),
    COURSE_EVENT_HELD(5);

    private int code;

    private TrainingCourseEventStatusEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
