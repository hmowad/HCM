package com.code.dal.orm.hcm;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "hcm_transactionTimeline_getAllFutureTransactions",
		query = " select t from TransactionTimeline t" +
			" where t.employeeId = :P_EMP_ID " +
			" and t.dueDate >= to_date(:P_SYSTEM_DATE, 'MI/MM/YYYY')")
})

@Entity
@Table(name = "HCM_VW_TRANS_TIMELINE")
@IdClass(TransactionTimelineId.class)
public class TransactionTimeline extends BaseEntity {

    private Date dueDate;
    private String dueDateString;
    private String transactionTypeDescription;
    private Long employeeId;
    private String days;
    private String weeks;
    private String months;
    private String period;
    private Date startDate;
    private String startDateString;
    private Date endDate;
    private String endDateString;

    @Id
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

    @Id
    @Column(name = "DESCRIPTION")
    public String getTransactionTypeDescription() {
	return transactionTypeDescription;
    }

    public void setTransactionTypeDescription(String transactionTypeDescription) {
	this.transactionTypeDescription = transactionTypeDescription;
    }

    @Basic
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    @Basic
    @Column(name = "DAYS")
    public String getDays() {
	return days;
    }

    public void setDays(String days) {
	this.days = days;
    }

    @Basic
    @Column(name = "WEEKS")
    public String getWeeks() {
	return weeks;
    }

    public void setWeeks(String weeks) {
	this.weeks = weeks;
    }

    @Basic
    @Column(name = "MONTHS")
    public String getMonths() {
	return months;
    }

    public void setMonths(String months) {
	this.months = months;
    }

    @Transient
    public String getPeriod() {
	return period;
    }

    public void setPeriod(String period) {
	this.period = period;
    }

    @Basic
    @Column(name = "START_DATE")
    public Date getStartDate() {
	return startDate;
    }

    public void setStartDate(Date startDate) {
	this.startDate = startDate;
	this.startDateString = HijriDateService.getHijriDateString(startDate);
    }

    @Transient
    public String getStartDateString() {
	return startDateString;
    }

    public void setStartDateString(String startDateString) {
	this.startDateString = startDateString;
	this.startDate = HijriDateService.getHijriDate(startDateString);
    }

    @Basic
    @Column(name = "END_DATE")
    public Date getEndDate() {
	return endDate;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
	this.endDateString = HijriDateService.getHijriDateString(endDate);
    }

    @Transient
    public String getEndDateString() {
	return endDateString;
    }

    public void setEndDateString(String endDateString) {
	this.endDateString = endDateString;
	this.endDate = HijriDateService.getHijriDate(endDateString);
    }

}
