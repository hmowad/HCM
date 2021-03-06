package com.code.dal.orm.hcm;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;

@Embeddable
public class TransactionTimelineId implements Serializable {

    private Date dueDate;
    private String transactionTypeDescription;

    public Date getDueDate() {
	return dueDate;
    }

    public void setDueDate(Date dueDate) {
	this.dueDate = dueDate;
    }

    public String getTransactionTypeDescription() {
	return transactionTypeDescription;
    }

    public void setTransactionTypeDescription(String transactionTypeDescription) {
	this.transactionTypeDescription = transactionTypeDescription;
    }

    public boolean equals(Object o) {
	return ((o instanceof TransactionTimelineId) && (dueDate.equals(((TransactionTimelineId) o).getDueDate())) && (transactionTypeDescription.equals(((TransactionTimelineId) o).getTransactionTypeDescription())));
    }

    public int hashCode() {
	return dueDate.hashCode() + transactionTypeDescription.hashCode();
    }
}
