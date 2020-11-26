package com.code.enums.databasecolumnamemappings;

public enum TransactionIdMappingEnum {

    DISCLAIMER_TRANSACTION_DATA("HCM_VW_DSCLMR_TRANS_AAD_INTEG", "ID"),
    RECRUITMENT_TRANSACTION_DATA("HCM_VW_REC_TRANSACTIONS", "ID"),
    VACATION_TRANSACTION("HCM_VAC_TRANSACTIONS", "ID"),
    SERVICE_TERMINATION_TRANSACTION_DATA("HCM_VW_STE_TRANS_AAD_INTEG", "ID"),
    MOVEMENT_TRANSACTION_DATA("HCM_VW_MVT_TRANSACTIONS", "ID"),
    PROMOTION_TRANSACTION_DATA("HCM_VW_PRM_TRANSACTIONS", "ID"),
    DUTY_EXETNSION_TRANSACTION("HCM_DUTY_EXTNSION_TRANSACTIONS", "ID"),
    INCENTIVE_TRANSACTION("HCM_INC_TRANSCATIONS", "ID"),
    TRAINING_TRANSACTION_DATA("HCM_VW_TRN_TRANSACTIONS", "ID"),
    MISSION_TRANSACTIONS_DETAILS_DATA("HCM_VW_MSN_DETAILS", "ID");

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
