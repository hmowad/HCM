package com.code.enums;

public enum TraineeStatusEnum {
    NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS(1),
    NOMINATION_REJECTED_FROM_PARTY(2),
    NOMINATION_ACCEPTED(3),
    JOINED(4),
    APOLOGIZED(5),
    REPLACED(6),
    CANCELLED(7),
    FINISHED(8),
    TRAINING_COURSE_EVENT_POSTPONED(9),
    TRAINING_COURSE_EVENT_CANCELLED(10),
    SCHOLARSHIP(11),
    SCHOLARSHIP_EXTENSION(12),
    SCHOLARSHIP_TERMINATION(13),
    SCHOLARSHIP_CANCELLATION(14);

    private int code;

    private TraineeStatusEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
