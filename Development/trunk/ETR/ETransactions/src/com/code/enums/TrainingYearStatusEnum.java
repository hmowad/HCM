package com.code.enums;

public enum TrainingYearStatusEnum {

    INITIAL_DRAFT(1),
    FINAL_DRAFT(2),
    TRAINING_PLAN_PENDING_APPROVE(3),
    TRAINING_PLAN_APPROVED(4);

    private int code;

    private TrainingYearStatusEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
