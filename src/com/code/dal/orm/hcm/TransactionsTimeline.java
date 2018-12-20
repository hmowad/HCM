package com.code.dal.orm.hcm;

public class TransactionsTimeline {

    private String dueDateString;
    private Long daysPeriod;
    private String transactionTypeDescription;

    public String getDueDateString() {
	return dueDateString;
    }

    public void setDueDateString(String dueDateString) {
	this.dueDateString = dueDateString;
    }

    public Long getDaysPeriod() {
	return daysPeriod;
    }

    public void setDaysPeriod(Long daysPeriod) {
	this.daysPeriod = daysPeriod;
    }

    public String getTransactionTypeDescription() {
	return transactionTypeDescription;
    }

    public void setTransactionTypeDescription(String transactionTypeDescription) {
	this.transactionTypeDescription = transactionTypeDescription;
    }

}
