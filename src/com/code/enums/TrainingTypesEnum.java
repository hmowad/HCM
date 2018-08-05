package com.code.enums;

public enum TrainingTypesEnum {
    INTERNAL_MILITARY_COURSE(1),
    EXTERNAL_MILITARY_COURSE(2),
    SCHOLARSHIP(3),
    STUDY_ENROLLMENT(4),
    MORNING_COURSE(5),
    EVENING_COURSE(6);

    private long code;

    private TrainingTypesEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}
