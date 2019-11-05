package com.code.enums.databasecolumnamemappings;

public enum EmployeeIdMappingEnum {

    DISCLAIMER_TRANSACTION_DATA("HCM_VW_RET_DSCLMR_TRANSACTIONS", "EMP_ID"),
    RECRUITMENT_TRANSACTION_DATA("HCM_VW_REC_TRANSACTIONS", "EMPLOYEE_ID"),
    QUALIFICATIONS_LEVEL_DATA("HCM_VW_PRS_EMPS_QUALIFICATIONS", "EMPLOYEE_ID"),
    EMPLOYEES_VIEW("HCM_VW_PRS_EMPLOYEES", "ID"),
    VACATION_TRANSACTION("HCM_VAC_TRANSACTIONS", "EMP_ID"),
    SERVICE_TERMINATION_TRANSACTION_DATA("HCM_VW_STE_TRANSACTIONS", "EMP_ID"),
    MOVEMENT_TRANSACTION_DATA("HCM_VW_MVT_TRANSACTIONS", "EMPLOYEE_ID");

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
