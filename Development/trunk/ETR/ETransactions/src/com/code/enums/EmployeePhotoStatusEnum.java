package com.code.enums;

public enum EmployeePhotoStatusEnum {
    NOT_FOUND(1),
    NOT_UPDATED(2);
    private long code;

    private EmployeePhotoStatusEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }

}
