/*
 * CONFIDENTIAL
 * Copyright 2014 Ejada, Ltd.  All rights reserved.
 */

package com.code.enums;

public enum PaidVacationTypesEnum {

    FULL_PAID(1),
    THREE_QUARTER_PAID(2),
    HALF_PAID(3),
    QUARTER_PAID(4),
    WITHOUT_PAID(5);

    private int code;

    private PaidVacationTypesEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
