package com.code.enums;

public enum TrainingYearTypesEnum {

    NEW_SEMESTER_IN_EXISTING_TRAINING_YEAR(1),
    NEW_SEMESTER_IN_NEW_TRAINING_YEAR(2),
    NEW_YEAR_WITH_NO_SEMESTERS(3);

    private long code;

    private TrainingYearTypesEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }

}
