package com.code.enums.databasecolumnamemappingsý;

public enum TransactionIdMappingEnum {

    DISCLAIMER_TRANSACTION("HCM_VW_RET_DSCLMR_TRANSACTIONS", "ID"),
    RECRUITMENT_TRANSACTION("HCM_VW_REC_TRANSACTIONS", "ID");

    private String tableName;
    private String transactionIdColumnName;

    private TransactionIdMappingEnum(String tableName, String employeeIdColumnName) {
	this.tableName = tableName;
	this.transactionIdColumnName = employeeIdColumnName;
    }

    public String getTableName() {
	return tableName;
    }

    public String getTransactionIdColumnName() {
	return transactionIdColumnName;
    }

}
