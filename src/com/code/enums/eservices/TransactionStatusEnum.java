package com.code.enums.eservices;

public enum TransactionStatusEnum {
    UNDER_PROCESSING(1),
    APPROVED(2),
    REJECTED(3);

    private int code;

    TransactionStatusEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
