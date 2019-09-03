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
	@NamedQuery(name = "hcm_historicalVacationTransactionData_searchHistoricalVacations",
		query = " select v from HistoricalVacationTransactionData v " +
			" where v.empId = :P_EMP_ID " +
			" and (:P_REQUEST_TYPE_FLAG = -1 or v.requestType in (:P_REQUEST_TYPES) ) " +
			" and (:P_DECISION_NUMBER = '-1' or v.decisionNumber = :P_DECISION_NUMBER)" +
			" and (:P_DECISION_DATE_SKIP_FLAG = -1 or v.decisionDate = to_date(:P_DECISION_DATE, 'MI/MM/YYYY')) " +
			" and (:P_LOCATION_FLAG = -1 or v.locationFlag = :P_LOCATION_FLAG ) " +
			" and (:P_COUNTRY_NAME = '-1' or v.location like :P_COUNTRY_NAME )" +
			" and (:P_VACATION_TYPE_FLAG = -1 or v.vacationTypeId in (:P_VACATION_TYPES_ID))" +
			" and (   (v.startDate between to_date(:P_FROM_DATE, 'MI/MM/YYYY') and to_date(:P_TO_DATE, 'MI/MM/YYYY')) " +
			"        OR (v.endDate between to_date(:P_FROM_DATE, 'MI/MM/YYYY') and to_date(:P_TO_DATE, 'MI/MM/YYYY')) " +
			"        OR (v.startDate < to_date(:P_FROM_DATE, 'MI/MM/YYYY') and v.endDate > to_date(:P_TO_DATE, 'MI/MM/YYYY')) " +
			"   ) " +
			" and (:P_PERIOD = -1 or v.period = :P_PERIOD )" +
			" and (:P_APPROVED_FLAG = -1 or v.approvedFlag = :P_APPROVED_FLAG)" +
			" and  v.activeFlag = 1" +
			" order by v.startDate , v.approvedFlag,v.vacationTypeId,v.requestType"),

	@NamedQuery(name = "hcm_historicalVacationTransactionData_getHistoricalVacationById",
		query = " select v from HistoricalVacationTransactionData v " +
			" where v.id = :P_VACATION_ID "),
})

@Entity
@Table(name = "HCM_VW_VAC_HISTORICAL_TRNS")
public class HistoricalVacationTransactionData extends BaseEntity {

    private Long id;
    private Long vacationTransactionId;
    private Long historicalVacationParentId;
    private Long empId;
    private String empName;
    private Long currentEmpPhysicalUnitId;
    private Long vacationTypeId;
    private String vacationTypeDescription;
    private Integer subVacationType;
    private String subVacationTypeDescription;
    private Integer locationFlag;
    private String location;
    private Date startDate;
    private String startDateString;
    private Date endDate;
    private String endDateString;
    private Integer period;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;
    private Long decisionRegionId;
    private Integer requestType;
    private Integer approvedFlag;
    private String contactNumber;
    private String contactAddress;
    private Integer paidVacationType;
    private Integer exceededDays;
    private Date joiningDate;
    private String joiningDateString;
    private Integer activeFlag;
    private String remarks;
    private String joinigRemarks;
    private String attachments;

    private HistoricalVacationTransaction historicalVacationTransaction;

    public HistoricalVacationTransactionData() {
	historicalVacationTransaction = new HistoricalVacationTransaction();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	historicalVacationTransaction.setId(id);
    }

    @Basic
    @Column(name = "VAC_TRANS_ID")
    public Long getVacationTransactionId() {
	return vacationTransactionId;
    }

    public void setVacationTransactionId(Long vacationTransactionId) {
	this.vacationTransactionId = vacationTransactionId;
	this.historicalVacationTransaction.setVacationTransactionId(vacationTransactionId);
    }

    @Basic
    @Column(name = "HISTORICAL_VAC_PARENT_ID")
    public Long getHistoricalVacationParentId() {
	return historicalVacationParentId;
    }

    public void setHistoricalVacationParentId(Long historicalVacationParentId) {
	this.historicalVacationParentId = historicalVacationParentId;
	this.historicalVacationTransaction.setHistoricalVacationParentId(historicalVacationParentId);
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	historicalVacationTransaction.setEmpId(empId);
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
    @Column(name = "CURRENT_EMP_PHYSICAL_UNIT_ID")
    public Long getCurrentEmpPhysicalUnitId() {
	return currentEmpPhysicalUnitId;
    }

    public void setCurrentEmpPhysicalUnitId(Long currentEmpPhysicalUnitId) {
	this.currentEmpPhysicalUnitId = currentEmpPhysicalUnitId;
    }

    @Basic
    @Column(name = "VACATION_TYPE_ID")
    public Long getVacationTypeId() {
	return vacationTypeId;
    }

    public void setVacationTypeId(Long vacationTypeId) {
	this.vacationTypeId = vacationTypeId;
	historicalVacationTransaction.setVacationTypeId(vacationTypeId);
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
	historicalVacationTransaction.setSubVacationType(subVacationType);
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
    @Column(name = "LOCATION_FLAG")
    public Integer getLocationFlag() {
	return locationFlag;
    }

    public void setLocationFlag(Integer locationFlag) {
	this.locationFlag = locationFlag;
	historicalVacationTransaction.setLocationFlag(locationFlag);
    }

    @Basic
    @Column(name = "LOCATION")
    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
	historicalVacationTransaction.setLocation(location);
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
	historicalVacationTransaction.setStartDate(startDate);
    }

    @Transient
    public String getStartDateString() {
	return startDateString;
    }

    public void setStartDateString(String startDateString) {
	this.startDateString = startDateString;
	historicalVacationTransaction.setStartDateString(startDateString);
	this.startDate = HijriDateService.getHijriDate(startDateString);
    }

    @Basic
    @Column(name = "DECISION_REGION_ID")
    public Long getDecisionRegionId() {
	return decisionRegionId;
    }

    public void setDecisionRegionId(Long decisionRegionId) {
	this.decisionRegionId = decisionRegionId;
	this.historicalVacationTransaction.setDecisionRegionId(decisionRegionId);
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
	historicalVacationTransaction.setEndDate(endDate);
    }

    @Transient
    public String getEndDateString() {
	return endDateString;
    }

    public void setEndDateString(String endDateString) {
	this.endDateString = endDateString;
	this.endDate = HijriDateService.getHijriDate(endDateString);
	historicalVacationTransaction.setEndDateString(endDateString);
    }

    @Basic
    @Column(name = "PERIOD")
    public Integer getPeriod() {
	return period;
    }

    public void setPeriod(Integer period) {
	this.period = period;
	historicalVacationTransaction.setPeriod(period);
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
	historicalVacationTransaction.setDecisionNumber(decisionNumber);
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
	historicalVacationTransaction.setDecisionDate(decisionDate);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
	historicalVacationTransaction.setDecisionDateString(decisionDateString);
    }

    @Basic
    @Column(name = "REQUEST_TYPE")
    public Integer getRequestType() {
	return requestType;
    }

    public void setRequestType(Integer requestType) {
	this.requestType = requestType;
	historicalVacationTransaction.setRequestType(requestType);
    }

    @Basic
    @Column(name = "APPROVED_FLAG")
    public Integer getApprovedFlag() {
	return approvedFlag;
    }

    public void setApprovedFlag(Integer approvedFlag) {
	this.approvedFlag = approvedFlag;
	historicalVacationTransaction.setApprovedFlag(approvedFlag);
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
	historicalVacationTransaction.setAttachments(attachments);
    }

    @Basic
    @Column(name = "CONTACT_NUMBER")
    public String getContactNumber() {
	return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
	this.contactNumber = contactNumber;
	historicalVacationTransaction.setContactNumber(contactNumber);
    }

    @Basic
    @Column(name = "CONTACT_ADDRESS")
    public String getContactAddress() {
	return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
	this.contactAddress = contactAddress;
	historicalVacationTransaction.setContactAddress(contactAddress);
    }

    @Basic
    @Column(name = "PAID_VACATION_TYPE")
    public Integer getPaidVacationType() {
	return paidVacationType;
    }

    public void setPaidVacationType(Integer paidVacationType) {
	this.paidVacationType = paidVacationType;
	historicalVacationTransaction.setPaidVacationType(paidVacationType);
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
	this.historicalVacationTransaction.setJoiningDate(joiningDate);
    }

    @Transient
    public String getJoiningDateString() {
	return joiningDateString;
    }

    public void setJoiningDateString(String joiningDateString) {
	this.joiningDateString = joiningDateString;
	this.joiningDate = HijriDateService.getHijriDate(joiningDateString);
	this.historicalVacationTransaction.setJoiningDateString(joiningDateString);
    }

    @Basic
    @Column(name = "ACTIVE_FLAG")
    public Integer getActiveFlag() {
	return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
	this.activeFlag = activeFlag;
	historicalVacationTransaction.setActiveFlag(activeFlag);
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
	historicalVacationTransaction.setRemarks(remarks);
    }

    @Basic
    @Column(name = "JOINING_REMARKS")
    public String getJoinigRemarks() {
	return joinigRemarks;
    }

    public void setJoinigRemarks(String joinigRemarks) {
	this.joinigRemarks = joinigRemarks;
	this.historicalVacationTransaction.setJoinigRemarks(joinigRemarks);
    }

    @Basic
    @Column(name = "EXCEEDED_DAYS")
    public Integer getExceededDays() {
	return exceededDays;
    }

    public void setExceededDays(Integer exceededDays) {
	this.exceededDays = exceededDays;
	this.historicalVacationTransaction.setExceededDays(exceededDays);
    }

    @Transient
    public HistoricalVacationTransaction getHistoricalVacationTransaction() {
	return historicalVacationTransaction;
    }

    public void setHistoricalVacationTransaction(HistoricalVacationTransaction historicalVacationTransaction) {
	this.historicalVacationTransaction = historicalVacationTransaction;
    }

}
