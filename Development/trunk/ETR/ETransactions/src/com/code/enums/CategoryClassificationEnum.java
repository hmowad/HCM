package com.code.enums;

public enum CategoryClassificationEnum {

    NON_SAUDI_DOCOTRS_CONTRACTORS(601);

    private long code;

    private CategoryClassificationEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}
