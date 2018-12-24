package com.code.dal.orm.hcm;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "hcm_transactionsTimeline_getAllFutureTransactions",
		query = " select t from TransactionsTimeline t" +
			" where t.employeeId = :P_EMP_ID " +
			" and t.dueDate >= to_date(:P_SYSTEM_DATE, 'MI/MM/YYYY')")
})

@Entity
@Table(name = "HCM_VW_TRANS_TIMELINE")
public class TransactionsTimeline extends BaseEntity {

    private Long employeeId;
    private Date dueDate;
    private String dueDateString;
    private String daysPeriod;
    private String transactionTypeDescription;

    @Id
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    @Basic
    @Column(name = "DUE_DATE")
    public Date getDueDate() {
	return dueDate;
    }

    public void setDueDate(Date dueDate) {
	this.dueDate = dueDate;
	this.dueDateString = HijriDateService.getHijriDateString(dueDate);
    }

    @Transient
    public String getDueDateString() {
	return dueDateString;
    }

    public void setDueDateString(String dueDateString) {
	this.dueDateString = dueDateString;
	this.dueDate = HijriDateService.getHijriDate(dueDateString);
    }

    @Basic
    @Column(name = "DAYS_COUNT")
    public String getDaysPeriod() {
	return daysPeriod;
    }

    public void setDaysPeriod(String daysPeriod) {
	this.daysPeriod = daysPeriod;
    }

    @Basic
    @Column(name = "DESCRIPTION")
    public String getTransactionTypeDescription() {
	return transactionTypeDescription;
    }

    public void setTransactionTypeDescription(String transactionTypeDescription) {
	this.transactionTypeDescription = transactionTypeDescription;
    }

}
