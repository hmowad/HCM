package com.code.enums;

public enum MovementTransactionViewsEnum {
    // These keys are supposed to determine if the transaction is a workflow (Request or decision) or a registration
    REQUEST(1),
    DECISION(2),
    REGISTRATION(3);

    private int code;

    private MovementTransactionViewsEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
