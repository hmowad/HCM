package com.code.enums;

public enum JobReservationStatusEnum {

    UN_RESERVED(0),
    RESERVED(1),
    RESERVED_FOR_RECRUITMENT(2),
    RESERVED_FOR_PROMOTION(3);

    private int code;

    private JobReservationStatusEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
