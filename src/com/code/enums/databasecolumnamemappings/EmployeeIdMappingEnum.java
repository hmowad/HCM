package com.code.enums.databasecolumnamemappings;

public enum EmployeeIdMappingEnum {

    DISCLAIMER_TRANSACTION_DATA("HCM_VW_DSCLMR_TRANS_AAD_INTEG", "EMP_ID"),
    RECRUITMENT_TRANSACTION_DATA("HCM_VW_REC_TRANSACTIONS", "EMPLOYEE_ID"),
    QUALIFICATIONS_LEVEL_DATA("HCM_VW_PRS_EMPS_QUALIFICATIONS", "EMPLOYEE_ID"),
    EMPLOYEES_VIEW("HCM_VW_PRS_EMPLOYEES", "ID"),
    VACATION_TRANSACTION("HCM_VAC_TRANSACTIONS", "EMP_ID"),
    SERVICE_TERMINATION_TRANSACTION_DATA("HCM_VW_STE_TRANS_AAD_INTEG", "EMP_ID"),
    MOVEMENT_TRANSACTION_DATA("HCM_VW_MVT_TRANSACTIONS", "EMPLOYEE_ID"),
    PROMOTION_TRANSACTION_DATA("HCM_VW_PRM_TRANSACTIONS", "EMP_ID"),
    DUTY_EXETNSION_TRANSACTION("HCM_DUTY_EXTNSION_TRANSACTIONS", "EMP_ID"),
    INCENTIVE_TRANSACTION("HCM_INC_TRANSCATIONS", "EMPLOYEE_ID"),
    TRAINING_TRANSACTION_DATA("HCM_VW_TRN_TRANSACTIONS", "EMPLOYEE_ID");

    private String tableName;
    private String employeeIdColumnName;

    private EmployeeIdMappingEnum(String tableName, String employeeIdColumnName) {
	this.tableName = tableName;
	this.employeeIdColumnName = employeeIdColumnName;
    }

    public String getTableName() {
	return tableName;
    }

    public String getEmployeeIdColumnName() {
	return employeeIdColumnName;
    }

}
