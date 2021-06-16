package com.code.enums;

public enum OfficersPromotionTasksTypeEnum {

    EMPLOYEE_TASK("EMPLOYEE_TASK"),
    MANAGER_TASK("MANAGER_TASK"),
    CONFIGURED_MANAGER_TASK("CONFIGURED_MANAGER_TASK");

    private String code;

    private OfficersPromotionTasksTypeEnum(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }

}
