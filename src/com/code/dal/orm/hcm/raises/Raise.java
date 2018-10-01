package com.code.dal.orm.hcm.raises;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "hcm_raises_searchRaiseData",
		query = "select r from Raise r" +
			" where (:P_EXCLUDED_ID = -1 or r.id <> :P_EXCLUDED_ID)" +
			" and(:P_ID = -1 or r.id = :P_ID)" +
			" and (:P_DECISION_NUMBER = '-1' or r.decisionNumber like :P_DECISION_NUMBER)" +
			" and (:P_TYPE = -1 or r.type = :P_TYPE)" +
			" and (:P_STATUS = -1 or r.status = :P_STATUS )" +
			" and (:P_CATEGORY_ID = -1 or r.categoryId = :P_CATEGORY_ID)" +
			" and (:P_DECISION_DATE_FROM_FLAG = -1 or r.decisionDate >= (TO_DATE(:P_DECISION_DATE_FROM, 'MI/MM/YYYY')))" +
			" and (:P_DECISION_DATE_TO_FLAG = -1 or r.decisionDate <= (TO_DATE(:P_DECISION_DATE_TO, 'MI/MM/YYYY')))" +
			" and (:P_EXECUTION_DATE_FROM_FLAG = -1 or r.executionDate >= (TO_DATE(:P_EXECUTION_DATE_FROM, 'MI/MM/YYYY')))" +
			" and (:P_EXECUTION_DATE_TO_FLAG = -1 or r.executionDate <= (TO_DATE(:P_EXECUTION_DATE_TO,'MI/MM/YYYY')))" +
			" order by r.id"),

	@NamedQuery(name = "hcm_raises_validateRaise",
		query = "select r from Raise r" +
			" where (:P_EXCLUDED_ID = -1 or r.id <> :P_EXCLUDED_ID)" +
			" and (r.decisionNumber = :P_DECISION_NUMBER)" +
			" and (r.type = :P_TYPE)" +
			" and (r.decisionDate = (TO_DATE(:P_DECISION_DATE, 'MI/MM/YYYY')))" +
			" order by r.id"),

	@NamedQuery(name = "hcm_raises_searchExcludedEmployeesForEndOfLadder",
		query = "select ed from Raise r, EmployeeData ed" +
			" where (r.id = :P_Raise_ID)" +
			" and (ed.statusId between 15 and 45)" +
			" and (r.categoryId = ed.categoryId)" +
			" and (ed.degreeId = (select s.degreeId from PayrollSalary s " +
			" where s.rankId = :P_RANK_ID " +
			" and s.degreeId = (select max(m.degreeId) from PayrollSalary m where m.rankId = ed.rankId)))" +
			" and (r.executionDate >" +
			" (select CASE WHEN max(rt.executionDate) IS NULL " +
			" then to_date('1/1/1300', 'MI/MM/YYYY') " +
			" ELSE max(rt.executionDate) END " +
			" from RaiseTransaction rt" +
			" where rt.empId = ed.empId and rt.type = 2) )" +
			" and ((ed.lastAnnualRaiseDate is null) or (to_date(:P_EXECUTION_DATE, 'MI/MM/YYYY') > ed.lastAnnualRaiseDate))" +
			" and ((ed.lastPromotionDate is null) or (r.executionDate > ed.lastPromotionDate))" +
			" order by ed.empId"),

	@NamedQuery(name = "hcm_raises_searchAllDeservedEmployees",
		query = "select ed from Raise r, EmployeeData ed" +
			" where (r.id = :P_RAISE_ID)" +
			" and (ed.statusId between 15 and 45)" +
			" and (r.categoryId = ed.categoryId)" +
			" and (r.executionDate >" +
			" (select CASE WHEN max(rt.executionDate) IS NULL " +
			" then to_date('1/1/1300', 'MI/MM/YYYY') " +
			" ELSE max(rt.executionDate) END " +
			" from RaiseTransaction rt" +
			" where rt.empId = ed.empId and rt.type = 2) )" +
			" and ((ed.lastAnnualRaiseDate is null) or (to_date(:P_EXECUTION_DATE, 'MI/MM/YYYY') > ed.lastAnnualRaiseDate))" +
			" and ((ed.lastPromotionDate is null) or (r.executionDate > ed.lastPromotionDate))" +
			" order by ed.empId"),

	@NamedQuery(name = "hcm_raises_searchUnDeservedEmployees",
		query = "select ed from Raise r, EmployeeData ed" +
			" where (r.id = :P_RAISE_ID)" +
			" and (ed.statusId between 15 and 45)" +
			" and (r.categoryId = ed.categoryId)" +
			"and (" +
			" (r.executionDate <" +
			" (select CASE WHEN max(rt.executionDate) IS NULL " +
			" then to_date('1/1/1300', 'MI/MM/YYYY') " +
			" ELSE max(rt.executionDate) END " +
			" from RaiseTransaction rt" +
			" where rt.empId = ed.id and rt.type = 2) )" +
			" or ((ed.lastAnnualRaiseDate is not null) and (to_date(:P_EXECUTION_DATE, 'MI/MM/YYYY') <= ed.lastAnnualRaiseDate))" +
			" or ((ed.lastPromotionDate is not null) and (r.executionDate <= ed.lastPromotionDate))" +
			" )" +
			" order by ed.id"),

	@NamedQuery(name = "hcm_raises_searchDeservedEmployees",
		query = "select ed from Raise r, EmployeeData ed" +
			" where (r.id = :P_RAISE_ID)" +
			" and (ed.statusId between 15 and 45)" +
			" and (r.categoryId = ed.categoryId)" +
			" and (ed.degreeId <> (select s.degreeId from PayrollSalary s " +
			" where s.rankId = :P_RANK_ID " +
			" and s.degreeId = (select max(m.degreeId) from PayrollSalary m where m.rankId = ed.rankId)))" +
			" and (r.executionDate >" +
			" (select CASE WHEN max(rt.executionDate) IS NULL " +
			" then to_date('1/1/1300', 'MI/MM/YYYY') " +
			" ELSE max(rt.executionDate) END " +
			" from RaiseTransaction rt" +
			" where rt.empId = ed.empId and rt.type = 2) )" +
			" and ((ed.lastAnnualRaiseDate is null) or (to_date(:P_EXECUTION_DATE, 'MI/MM/YYYY') > ed.lastAnnualRaiseDate))" +
			" and ((ed.lastPromotionDate is null) or (r.executionDate > ed.lastPromotionDate))" +
			" and (ed.empId not in (select re.empId from RaiseEmployee re where re.raiseId = :P_RAISE_ID))" +
			" order by ed.empId"),

})

@Entity
@Table(name = "HCM_RAISES")
public class Raise extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {
    private Long id;
    private Date decisionDate;
    private String decisionNumber;
    private Date executionDate;
    private Integer type;
    private Integer status;
    private Long categoryId;
    private String remarks;
    private String decisionDateString;
    private String executionDateString;

    @SequenceGenerator(name = "HCMRaiseSeq",
	    sequenceName = "HCM_RAISE_SEQ")
    @Id
    @GeneratedValue(generator = "HCMRaiseSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "DECISION_DATE")
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
	this.decisionDateString = HijriDateService.getHijriDateString(decisionDate);
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    @Basic
    @Column(name = "EXECUTION_DATE")
    public Date getExecutionDate() {
	return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
	this.executionDate = executionDate;
	this.executionDateString = HijriDateService.getHijriDateString(executionDate);
    }

    @Basic
    @Column(name = "TYPE")
    public Integer getType() {
	return type;
    }

    public void setType(Integer type) {
	this.type = type;
    }

    @Basic
    @Column(name = "STATUS")
    public Integer getStatus() {
	return status;
    }

    public void setStatus(Integer status) {
	this.status = status;
    }

    @Basic
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
    }

    @Transient
    public String getExecutionDateString() {
	return executionDateString;
    }

    public void setExecutionDateString(String executionDateString) {
	this.executionDateString = executionDateString;
	this.executionDate = HijriDateService.getHijriDate(executionDateString);
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "decisionDate:" + decisionDate + AUDIT_SEPARATOR +
		"decisionNumber:" + decisionNumber + AUDIT_SEPARATOR +
		"executionDate:" + executionDate + AUDIT_SEPARATOR +
		"type:" + type + AUDIT_SEPARATOR +
		"status:" + status + AUDIT_SEPARATOR +
		"categoryId:" + categoryId + AUDIT_SEPARATOR +
		"remarks:" + remarks + AUDIT_SEPARATOR;
    }

}