package com.code.enums.databasecolumnamemappingsý;

public enum EmployeeIdMappingEnum {

    DISCLAIMER_TRANSACTION("HCM_VW_RET_DSCLMR_TRANSACTIONS", "EMP_ID"),
    RECRUITMENT_TRANSACTION("HCM_VW_REC_TRANSACTIONS", "EMPLOYEE_ID"),
    QUALIFICATIONS_LEVEL("HCM_VW_PRS_EMPS_QUALIFICATIONS", "EMPLOYEE_ID"),
    EMPLOYEES("HCM_VW_PRS_EMPLOYEES", "ID");

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
