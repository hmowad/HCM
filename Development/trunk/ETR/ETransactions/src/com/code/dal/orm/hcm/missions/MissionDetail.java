package com.code.dal.orm.hcm.missions;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

/**
 * 
 * Provides all information regarding mission detail transaction such as mission purpose, its location, start date, and end date, .. etc <br/>
 * There is one or more {@link MissionDetail} for every {@link Mission} linked by missionId
 * 
 */

@NamedQueries({
	@NamedQuery(name = "hcm_missionDetail_overLap",
		query = " select count(md.id) " +
			" from MissionDetail md " +
			" where " +
			" (:P_EXCLUDED_MISSION_ID = -1 or md.missionId != :P_EXCLUDED_MISSION_ID) " +
			" and (md.empId  = :P_EMP_ID) " +
			" and (   (to_date(:P_START_DATE, 'MI/MM/YYYY') between md.actualStartDate and md.actualEndDate) " +
			"      OR (to_date(:P_END_DATE, 'MI/MM/YYYY') between md.actualStartDate and md.actualEndDate) " +
			"      OR (to_date(:P_START_DATE, 'MI/MM/YYYY') <= md.actualStartDate and to_date(:P_END_DATE, 'MI/MM/YYYY') >= md.actualEndDate) " +
			" ) " +
			" and (NVL(md.absenceFlag,0) = 0) ")

})

@Entity
@Table(name = "HCM_MSN_DETAILS")
public class MissionDetail extends AuditEntity implements UpdatableAuditEntity {

    private Long id;
    private Long missionId;
    private Long empId;
    private String transEmpUnitDesc;
    private String transEmpJobCode;
    private String transEmpJobDesc;
    private String transEmpRankDesc;
    private Integer balance;
    private Integer period;
    private Integer roadPeriod;
    private Date startDate;
    private Date endDate;
    private Integer actualPeriod;
    private Date actualStartDate;
    private Date actualEndDate;
    private String exceptionalApprovalNumber;
    private Date exceptionalApprovalDate;
    private String roadLine;
    private Integer absenceFlag;
    private String absenceReasons;
    private String remarks;
    private Date joiningDate;
    private String closingAttachments;
    private Integer paymentDecisionIssuedFlag;
    private String paymentDecisionNumber;
    private Date paymentDecisionDate;
    private Integer actualDataSavedFlag;
    private String payrollDecisionNumber;

    @SequenceGenerator(name = "HCMMissionsSeq",
	    sequenceName = "HCM_MSN_SEQ")
    @Id
    @GeneratedValue(generator = "HCMMissionsSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "MISSION_ID")
    public Long getMissionId() {
	return missionId;
    }

    public void setMissionId(Long missionId) {
	this.missionId = missionId;
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    @Basic
    @Column(name = "TRANS_EMP_UNIT_DESC")
    public String getTransEmpUnitDesc() {
	return transEmpUnitDesc;
    }

    public void setTransEmpUnitDesc(String transEmpUnitDesc) {
	this.transEmpUnitDesc = transEmpUnitDesc;
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_CODE")
    public String getTransEmpJobCode() {
	return transEmpJobCode;
    }

    public void setTransEmpJobCode(String transEmpJobCode) {
	this.transEmpJobCode = transEmpJobCode;
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_DESC")
    public String getTransEmpJobDesc() {
	return transEmpJobDesc;
    }

    public void setTransEmpJobDesc(String transEmpJobDesc) {
	this.transEmpJobDesc = transEmpJobDesc;
    }

    @Basic
    @Column(name = "TRANS_EMP_RANK_DESC")
    public String getTransEmpRankDesc() {
	return transEmpRankDesc;
    }

    public void setTransEmpRankDesc(String transEmpRankDesc) {
	this.transEmpRankDesc = transEmpRankDesc;
    }

    @Basic
    @Column(name = "BALANCE")
    public Integer getBalance() {
	return balance;
    }

    public void setBalance(Integer balance) {
	this.balance = balance;
    }

    @Basic
    @Column(name = "PERIOD")
    public Integer getPeriod() {
	return period;
    }

    public void setPeriod(Integer period) {
	this.period = period;
    }

    @Basic
    @Column(name = "ROAD_PERIOD")
    public Integer getRoadPeriod() {
	return roadPeriod;
    }

    public void setRoadPeriod(Integer roadPeriod) {
	this.roadPeriod = roadPeriod;
    }

    @Basic
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStartDate() {
	return startDate;
    }

    public void setStartDate(Date startDate) {
	this.startDate = startDate;
    }

    @Basic
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getEndDate() {
	return endDate;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
    }

    @Basic
    @Column(name = "ACTUAL_PERIOD")
    public Integer getActualPeriod() {
	return actualPeriod;
    }

    public void setActualPeriod(Integer actualPeriod) {
	this.actualPeriod = actualPeriod;
    }

    @Basic
    @Column(name = "ACTUAL_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getActualStartDate() {
	return actualStartDate;
    }

    public void setActualStartDate(Date actualStartDate) {
	this.actualStartDate = actualStartDate;
    }

    @Basic
    @Column(name = "ACTUAL_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getActualEndDate() {
	return actualEndDate;
    }

    public void setActualEndDate(Date actualEndDate) {
	this.actualEndDate = actualEndDate;
    }

    @Basic
    @Column(name = "EXCEPTIONAL_APPROVAL_NUMBER")
    public String getExceptionalApprovalNumber() {
	return exceptionalApprovalNumber;
    }

    public void setExceptionalApprovalNumber(String exceptionalApprovalNumber) {
	this.exceptionalApprovalNumber = exceptionalApprovalNumber;
    }

    @Basic
    @Column(name = "EXCEPTIONAL_APPROVAL_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getExceptionalApprovalDate() {
	return exceptionalApprovalDate;
    }

    public void setExceptionalApprovalDate(Date exceptionalApprovalDate) {
	this.exceptionalApprovalDate = exceptionalApprovalDate;
    }

    @Basic
    @Column(name = "ROAD_LINE")
    public String getRoadLine() {
	return roadLine;
    }

    public void setRoadLine(String roadLine) {
	this.roadLine = roadLine;
    }

    @Basic
    @Column(name = "ABSENCE_FLAG")
    public Integer getAbsenceFlag() {
	return absenceFlag;
    }

    public void setAbsenceFlag(Integer absenceFlag) {
	this.absenceFlag = absenceFlag;
    }

    @Basic
    @Column(name = "ABSENCE_REASONS")
    public String getAbsenceReasons() {
	return absenceReasons;
    }

    public void setAbsenceReasons(String absenceReasons) {
	this.absenceReasons = absenceReasons;
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }

    @Basic
    @Column(name = "JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getJoiningDate() {
	return joiningDate;
    }

    public void setJoiningDate(Date joiningDate) {
	this.joiningDate = joiningDate;
    }

    @Basic
    @Column(name = "CLOSING_ATTACHMENTS")
    public String getClosingAttachments() {
	return closingAttachments;
    }

    public void setClosingAttachments(String closingAttachments) {
	this.closingAttachments = closingAttachments;
    }

    @Basic
    @Column(name = "PAYMENT_DECISION_ISSUED_FLAG")
    public Integer getPaymentDecisionIssuedFlag() {
	return paymentDecisionIssuedFlag;
    }

    public void setPaymentDecisionIssuedFlag(Integer paymentDecisionIssuedFlag) {
	this.paymentDecisionIssuedFlag = paymentDecisionIssuedFlag;
    }

    @Basic
    @Column(name = "PAYMENT_DECISION_NUMBER")
    public String getPaymentDecisionNumber() {
	return paymentDecisionNumber;
    }

    public void setPaymentDecisionNumber(String paymentDecisionNumber) {
	this.paymentDecisionNumber = paymentDecisionNumber;
    }

    @Basic
    @Column(name = "PAYMENT_DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPaymentDecisionDate() {
	return paymentDecisionDate;
    }

    public void setPaymentDecisionDate(Date paymentDecisionDate) {
	this.paymentDecisionDate = paymentDecisionDate;
    }

    @Basic
    @Column(name = "ACTUAL_DATA_SAVED_FLAG")
    public Integer getActualDataSavedFlag() {
	return actualDataSavedFlag;
    }

    public void setActualDataSavedFlag(Integer actualDataSavedFlag) {
	this.actualDataSavedFlag = actualDataSavedFlag;
    }

    @Basic
    @Column(name = "PAYROLL_DECISION_NUMBER")
    public String getPayrollDecisionNumber() {
	return payrollDecisionNumber;
    }

    public void setPayrollDecisionNumber(String payrollDecisionNumber) {
	this.payrollDecisionNumber = payrollDecisionNumber;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "empId:" + empId + AUDIT_SEPARATOR +
		"actualPeriod:" + actualPeriod + AUDIT_SEPARATOR +
		"actualStartDate:" + actualStartDate + AUDIT_SEPARATOR +
		"actualEndDate:" + actualEndDate + AUDIT_SEPARATOR +
		"absenceFlag:" + absenceFlag + AUDIT_SEPARATOR +
		"absenceReasons:" + absenceReasons + AUDIT_SEPARATOR +
		"remarks:" + remarks + AUDIT_SEPARATOR +
		"joiningDate:" + joiningDate + AUDIT_SEPARATOR +
		"closingAttachments:" + closingAttachments + AUDIT_SEPARATOR +
		"paymentDecisionIssuedFlag:" + paymentDecisionIssuedFlag + AUDIT_SEPARATOR +
		"paymentDecisionNumber:" + paymentDecisionNumber + AUDIT_SEPARATOR +
		"paymentDecisionDate:" + paymentDecisionDate + AUDIT_SEPARATOR +
		"actualDataSavedFlag:" + actualDataSavedFlag + AUDIT_SEPARATOR;
    }
}
