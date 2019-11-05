package com.code.enums.databasecolumnamemappings;

public enum TransactionIdMappingEnum {

    DISCLAIMER_TRANSACTION_DATA("HCM_VW_RET_DSCLMR_TRANSACTIONS", "ID"),
    RECRUITMENT_TRANSACTION_DATA("HCM_VW_REC_TRANSACTIONS", "ID"),
    VACATION_TRANSACTION("HCM_VAC_TRANSACTIONS", "ID"),
    SERVICE_TERMINATION_TRANSACTION_DATA("HCM_VW_STE_TRANSACTIONS", "ID"),
    MOVEMENT_TRANSACTION_DATA("HCM_VW_MVT_TRANSACTIONS", "ID");

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
