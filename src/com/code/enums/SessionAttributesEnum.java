package com.code.enums;

public enum SessionAttributesEnum {
    CURRENT_LANG("curLang"),

    EMP_DATA("empData"),

    USER_TRANSACTIONS_MENU("userTransactionsMenu"),
    USER_WORKFLOWS_MENU("userWorkflowsMenu"),
    USER_REPORTS_MENU("userReportsMenu"),

    TRANSACTIONS_TIME_LINE_SHOW_FLAG("timeLineMiniSearchShowFlag"),

    ACCESSED_WF_INSTANCES_IDS("accessedWfInstancesIds");

    private String code;

    private SessionAttributesEnum(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }
}
