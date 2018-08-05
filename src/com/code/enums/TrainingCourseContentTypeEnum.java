package com.code.enums;

public enum TrainingCourseContentTypeEnum {
    CURRICULUM(1),
    PROGRAM(2),
    SYLLABUS(3);

    private int code;

    private TrainingCourseContentTypeEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }

}
