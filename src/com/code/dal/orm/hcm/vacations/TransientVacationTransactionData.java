package com.code.dal.orm.hcm.vacations;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "hcm_transientVacationTransactionData_getFutureVacationById",
		query = " select v from TransientVacationTransactionData v " +
			" where (:P_VACATION_ID = -1 or v.id = :P_VACATION_ID )" +
			" and (:P_EMPLOYEE_ID = -1 or v.empId = :P_EMPLOYEE_ID )" +
			" and (:P_VACATION_TYPE_ID = -1 or v.vacationTypeId = :P_VACATION_TYPE_ID)" +
			" and (:P_APPROVED_FLAG = -1 or v.approvedFlag = :P_APPROVED_FLAG)" +
			" and (:P_ACTIVE_FLAG = -1 or v.activeFlag = :P_ACTIVE_FLAG)" +
			" and (:P_REQUEST_TYPE_FLAG = -1 or v.requestType <> 4)" +
			" order by v.endDate desc "),

	@NamedQuery(name = "hcm_transientVacationTransactionData_getFutureVacationByParentId",
		query = " select v from TransientVacationTransactionData v " +
			" where v.transientVacationParentId = :P_PARENT_ID " +
			" and (:P_VACATION_ID = -1 or v.id <> :P_VACATION_ID )"),

	@NamedQuery(name = "hcm_transientVacationTransactionData_searchFutureVacations",
		query = " select v from TransientVacationTransactionData v " +
			" where (:P_EMP_ID = -1 or v.empId = :P_EMP_ID )" +
			" and (:P_DECISION_NUMBER = '-1' or v.decisionNumber = :P_DECISION_NUMBER)" +
			" and (:P_REQUEST_TYPE = -1 or v.requestType = :P_REQUEST_TYPE ) " +
			" and (:P_VACATION_TYPE_ID = -1 or v.vacationTypeId = :P_VACATION_TYPE_ID)" +
			" and (:P_APPROVED_FLAG = -1 or v.approvedFlag = :P_APPROVED_FLAG)" +
			" and (:P_FROM_DATE_FLAG = -1 or v.startDate >= to_date(:P_FROM_DATE, 'MI/MM/YYYY')" +
			" or to_date(:P_FROM_DATE, 'MI/MM/YYYY') between v.startDate and v.endDate)" +
			" and (:P_TO_DATE_FLAG = -1 or v.startDate <= to_date(:P_TO_DATE, 'MI/MM/YYYY'))" +
			" and (:P_PERIOD = -1 or v.period = :P_PERIOD )" +
			" and (:P_LOCATION_FLAG = -1 or v.locationFlag = :P_LOCATION_FLAG ) " +
			" and  v.activeFlag = 1" +
			" order by v.decisionDate , v.approvedFlag")
})

@Entity
@Table(name = "HCM_VW_VAC_TRANSIENT_TRANS")
public class TransientVacationTransactionData extends BaseEntity {

    private Long id;
    private Long vacationTransactionId;
    private Long transientVacationParentId;
    private Long empId;
    private String empName;
    private Integer requestType;
    private Long vacationTypeId;
    private String vacationTypeDescription;
    private Integer subVacationType;
    private String subVacationTypeDescription;
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

    private TransientVacationTransaction transientVacationTransaction;

    public TransientVacationTransactionData() {
	transientVacationTransaction = new TransientVacationTransaction();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.transientVacationTransaction.setId(id);
    }

    @Basic
    @Column(name = "VAC_TRANS_ID")
    public Long getVacationTransactionId() {
	return vacationTransactionId;
    }

    public void setVacationTransactionId(Long vacationTransactionId) {
	this.vacationTransactionId = vacationTransactionId;
	this.transientVacationTransaction.setVacationTransactionId(vacationTransactionId);
    }

    @Basic
    @Column(name = "TRANSIENT_VAC_PARENT_ID")
    public Long getTransientVacationParentId() {
	return transientVacationParentId;
    }

    public void setTransientVacationParentId(Long transientVacationParentId) {
	this.transientVacationParentId = transientVacationParentId;
	this.transientVacationTransaction.setTransientVacationParentId(transientVacationParentId);
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	this.transientVacationTransaction.setEmpId(empId);
    }

    @Basic
    @Column(name = "EMP_NAME")
    public String getEmpName() {
	return empName;
    }

    public void setEmpName(String empName) {
	this.empName = empName;
    }

    @Basic
    @Column(name = "REQUEST_TYPE")
    public Integer getRequestType() {
	return requestType;
    }

    public void setRequestType(Integer requestType) {
	this.requestType = requestType;
	this.transientVacationTransaction.setRequestType(requestType);
    }

    @Basic
    @Column(name = "VACATION_TYPE_ID")
    public Long getVacationTypeId() {
	return vacationTypeId;
    }

    public void setVacationTypeId(Long vacationTypeId) {
	this.vacationTypeId = vacationTypeId;
	this.transientVacationTransaction.setVacationTypeId(vacationTypeId);
    }

    @Basic
    @Column(name = "VACATION_TYPE_DESCRIPTION")
    public String getVacationTypeDescription() {
	return vacationTypeDescription;
    }

    public void setVacationTypeDescription(String vacationTypeDescription) {
	this.vacationTypeDescription = vacationTypeDescription;
    }

    @Basic
    @Column(name = "SUB_VACATION_TYPE")
    public Integer getSubVacationType() {
	return subVacationType;
    }

    public void setSubVacationType(Integer subVacationType) {
	this.subVacationType = subVacationType;
	this.transientVacationTransaction.setSubVacationType(subVacationType);
    }

    @Basic
    @Column(name = "SUB_VACATION_TYPE_DESCRIPTION")
    public String getSubVacationTypeDescription() {
	return subVacationTypeDescription;
    }

    public void setSubVacationTypeDescription(String subVacationTypeDescription) {
	this.subVacationTypeDescription = subVacationTypeDescription;
    }

    @Basic
    @Column(name = "PAID_VACATION_TYPE")
    public Integer getPaidVacationType() {
	return paidVacationType;
    }

    public void setPaidVacationType(Integer paidVacationType) {
	this.paidVacationType = paidVacationType;
	this.transientVacationTransaction.setPaidVacationType(paidVacationType);
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
	this.transientVacationTransaction.setStartDate(startDate);
    }

    @Transient
    public String getStartDateString() {
	return startDateString;
    }

    public void setStartDateString(String startDateString) {
	this.startDateString = startDateString;
	this.startDate = HijriDateService.getHijriDate(startDateString);
	this.transientVacationTransaction.setStartDateString(startDateString);
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
	this.transientVacationTransaction.setEndDate(endDate);
    }

    @Transient
    public String getEndDateString() {
	return endDateString;
    }

    public void setEndDateString(String endDateString) {
	this.endDateString = endDateString;
	this.endDate = HijriDateService.getHijriDate(endDateString);
	this.transientVacationTransaction.setEndDateString(endDateString);
    }

    @Basic
    @Column(name = "PERIOD")
    public Integer getPeriod() {
	return period;
    }

    public void setPeriod(Integer period) {
	this.period = period;
	this.transientVacationTransaction.setPeriod(period);
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
	this.transientVacationTransaction.setDecisionNumber(decisionNumber);
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
	this.transientVacationTransaction.setDecisionDate(decisionDate);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
	this.transientVacationTransaction.setDecisionDateString(decisionDateString);
    }

    @Basic
    @Column(name = "DECISION_ISSUER_PLACE")
    public String getDecisionIssuerPlace() {
	return decisionIssuerPlace;
    }

    public void setDecisionIssuerPlace(String decisionIssuerPlace) {
	this.decisionIssuerPlace = decisionIssuerPlace;
	this.transientVacationTransaction.setDecisionIssuerPlace(decisionIssuerPlace);
    }

    @Basic
    @Column(name = "LOCATION_FLAG")
    public Integer getLocationFlag() {
	return locationFlag;
    }

    public void setLocationFlag(Integer locationFlag) {
	this.locationFlag = locationFlag;
	this.transientVacationTransaction.setLocationFlag(locationFlag);
    }

    @Basic
    @Column(name = "LOCATION")
    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
	this.transientVacationTransaction.setLocation(location);
    }

    @Basic
    @Column(name = "CONTACT_ADDRESS")
    public String getContactAddress() {
	return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
	this.contactAddress = contactAddress;
	this.transientVacationTransaction.setContactAddress(contactAddress);
    }

    @Basic
    @Column(name = "CONTACT_NUMBER")
    public String getContactNumber() {
	return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
	this.contactNumber = contactNumber;
	this.transientVacationTransaction.setContactNumber(contactNumber);
    }

    @Basic
    @Column(name = "APPROVED_FLAG")
    public Integer getApprovedFlag() {
	return approvedFlag;
    }

    public void setApprovedFlag(Integer approvedFlag) {
	this.approvedFlag = approvedFlag;
	this.transientVacationTransaction.setApprovedFlag(approvedFlag);
    }

    @Basic
    @Column(name = "EXCEEDED_DAYS")
    public Integer getExceededDays() {
	return exceededDays;
    }

    public void setExceededDays(Integer exceededDays) {
	this.exceededDays = exceededDays;
	this.transientVacationTransaction.setExceededDays(exceededDays);
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
	this.transientVacationTransaction.setJoiningDate(joiningDate);
    }

    @Transient
    public String getJoiningDateString() {
	return joiningDateString;
    }

    public void setJoiningDateString(String joiningDateString) {
	this.joiningDateString = joiningDateString;
	this.joiningDate = HijriDateService.getHijriDate(joiningDateString);
	this.transientVacationTransaction.setJoiningDateString(joiningDateString);
    }

    @Basic
    @Column(name = "ACTIVE_FLAG")
    public Integer getActiveFlag() {
	return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
	this.activeFlag = activeFlag;
	this.transientVacationTransaction.setActiveFlag(activeFlag);
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
	this.transientVacationTransaction.setRemarks(remarks);
    }

    @Basic
    @Column(name = "JOINING_REMARKS")
    public String getJoiningRemarks() {
	return joiningRemarks;
    }

    public void setJoiningRemarks(String joiningRemarks) {
	this.joiningRemarks = joiningRemarks;
	this.transientVacationTransaction.setJoiningRemarks(joiningRemarks);
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
	this.transientVacationTransaction.setAttachments(attachments);
    }

    @Transient
    public TransientVacationTransaction getTransientVacationTransaction() {
	return transientVacationTransaction;
    }

    public void setTransientVacationTransaction(TransientVacationTransaction transientVacationTransaction) {
	this.transientVacationTransaction = transientVacationTransaction;
    }

}
