package com.code.dal.orm.hcm.vacations;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

@Entity
@Table(name = "HCM_VAC_TRANSIENT_TRANS")
public class TransientVacationTransaction extends BaseEntity {

    private Long id;
    private Long vacationTransactionId;
    private Long transientVacationParentId;
    private Long empId;
    private Integer requestType;
    private Long vacationTypeId;
    private Integer subVacationType;
    private Integer paidVacationType;
    private Date startDate;
    private String startDateString;
    private Date endDate;
    private String endDateString;
    private Integer period;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;
    private String decisionIssuerPlace;
    private Integer locationFlag;
    private String location;
    private String contactAddress;
    private String contactNumber;
    private Integer approvedFlag;
    private Integer exceededDays;
    private Date joiningDate;
    private String joiningDateString;
    private Integer activeFlag;
    private String remarks;
    private String joiningRemarks;
    private String attachments;

    @SequenceGenerator(name = "HCMTransientVacationsSeq",
	    sequenceName = "HCM_TRANSIENT_VACATIONS_SEQ",
	    allocationSize = 1)
    @Id
    @GeneratedValue(generator = "HCMTransientVacationsSeq",
	    strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "VAC_TRANS_ID")
    public Long getVacationTransactionId() {
	return vacationTransactionId;
    }

    public void setVacationTransactionId(Long vacationTransactionId) {
	this.vacationTransactionId = vacationTransactionId;
    }

    @Basic
    @Column(name = "TRANSIENT_VAC_PARENT_ID")
    public Long getTransientVacationParentId() {
	return transientVacationParentId;
    }

    public void setTransientVacationParentId(Long transientVacationParentId) {
	this.transientVacationParentId = transientVacationParentId;
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
    @Column(name = "REQUEST_TYPE")
    public Integer getRequestType() {
	return requestType;
    }

    public void setRequestType(Integer requestType) {
	this.requestType = requestType;
    }

    @Basic
    @Column(name = "VACATION_TYPE_ID")
    public Long getVacationTypeId() {
	return vacationTypeId;
    }

    public void setVacationTypeId(Long vacationTypeId) {
	this.vacationTypeId = vacationTypeId;
    }

    @Basic
    @Column(name = "SUB_VACATION_TYPE")
    public Integer getSubVacationType() {
	return subVacationType;
    }

    public void setSubVacationType(Integer subVacationType) {
	this.subVacationType = subVacationType;
    }

    @Basic
    @Column(name = "PAID_VACATION_TYPE")
    public Integer getPaidVacationType() {
	return paidVacationType;
    }

    public void setPaidVacationType(Integer paidVacationType) {
	this.paidVacationType = paidVacationType;
    }

    @Basic
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
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
    @Temporal(TemporalType.TIMESTAMP)
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

    @Basic
    @Column(name = "PERIOD")
    public Integer getPeriod() {
	return period;
    }

    public void setPeriod(Integer period) {
	this.period = period;
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
    @Column(name = "DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
	this.decisionDateString = HijriDateService.getHijriDateString(decisionDate);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
    }

    @Basic
    @Column(name = "DECISION_ISSUER_PLACE")
    public String getDecisionIssuerPlace() {
	return decisionIssuerPlace;
    }

    public void setDecisionIssuerPlace(String decisionIssuerPlace) {
	this.decisionIssuerPlace = decisionIssuerPlace;
    }

    @Basic
    @Column(name = "LOCATION_FLAG")
    public Integer getLocationFlag() {
	return locationFlag;
    }

    public void setLocationFlag(Integer locationFlag) {
	this.locationFlag = locationFlag;
    }

    @Basic
    @Column(name = "LOCATION")
    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
    }

    @Basic
    @Column(name = "CONTACT_ADDRESS")
    public String getContactAddress() {
	return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
	this.contactAddress = contactAddress;
    }

    @Basic
    @Column(name = "CONTACT_NUMBER")
    public String getContactNumber() {
	return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
	this.contactNumber = contactNumber;
    }

    @Basic
    @Column(name = "APPROVED_FLAG")
    public Integer getApprovedFlag() {
	return approvedFlag;
    }

    public void setApprovedFlag(Integer approvedFlag) {
	this.approvedFlag = approvedFlag;
    }

    @Basic
    @Column(name = "EXCEEDED_DAYS")
    public Integer getExceededDays() {
	return exceededDays;
    }

    public void setExceededDays(Integer exceededDays) {
	this.exceededDays = exceededDays;
    }

    @Basic
    @Column(name = "JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getJoiningDate() {
	return joiningDate;
    }

    public void setJoiningDate(Date joiningDate) {
	this.joiningDate = joiningDate;
	this.joiningDateString = HijriDateService.getHijriDateString(joiningDate);
    }

    @Transient
    public String getJoiningDateString() {
	return joiningDateString;
    }

    public void setJoiningDateString(String joiningDateString) {
	this.joiningDateString = joiningDateString;
	this.joiningDate = HijriDateService.getHijriDate(joiningDateString);
    }

    @Basic
    @Column(name = "ACTIVE_FLAG")
    public Integer getActiveFlag() {
	return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
	this.activeFlag = activeFlag;
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
    @Column(name = "JOINING_REMARKS")
    public String getJoiningRemarks() {
	return joiningRemarks;
    }

    public void setJoiningRemarks(String joiningRemarks) {
	this.joiningRemarks = joiningRemarks;
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }

}
