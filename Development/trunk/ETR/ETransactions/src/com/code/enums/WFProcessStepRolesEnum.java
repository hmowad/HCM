package com.code.enums;

public enum WFProcessStepRolesEnum {
    APPROVAL("Approval"),
    SINGLE_APPROVAL_GROUP("SingleApprovalGroup"),
    ALL_APPROVAL_GROUP("AllApprovalGroup"),
    NOTIFICATION("Notification");

    private String code;

    WFProcessStepRolesEnum(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }
}
