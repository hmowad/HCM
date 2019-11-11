package com.code.enums;

public enum WFProcessRolesEnum {
    REQUESTER("Requester"),
    APPROVAL("Approval"),
    REVIEWER("Reviewer"),
    NOTIFICATION("Notification"),
    HISTORY("History"),
    ;

    private String code;

    WFProcessRolesEnum(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }
}
