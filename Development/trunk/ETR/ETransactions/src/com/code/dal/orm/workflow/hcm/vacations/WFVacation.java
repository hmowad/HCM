package com.code.dal.orm.workflow.hcm.vacations;

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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.code.dal.orm.BaseEntity;
import com.code.exceptions.BusinessException;
import com.code.services.util.HijriDateService;

/**
 * The <code>WFVacation</code> class represents the vacation request data.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "wf_vacation_getWFVacationByInstanceId",
		query = " select v from WFVacation v " +
			" where v.instanceId = :P_INSTANCE_ID "),

	@NamedQuery(name = "wf_vacation_validateRunningProcesses",
		query = " select count(v.instanceId) from WFVacation v, WFInstance i " +
			" where v.instanceId = i.instanceId " +
			"   and v.beneficiaryId = :P_BENEFICIARY_ID " +
			"   and (:P_PROCESSES_IDS_FLAG = -1  or i.processId in (:P_PROCESSES_IDS) ) " +
			"   and i.status = 1 "),

	@NamedQuery(name = "wf_vacation_checkDatesConflict",
		query = " select count(v.instanceId) from WFVacation v, WFInstance i " +
			" where v.instanceId = i.instanceId " +
			"   and i.status = 1 " +
			"   and v.beneficiaryId = :P_BENEFICIARY_ID " +
			"   and (   (to_date(:P_START_DATE, 'MI/MM/YYYY') between v.startDate and v.endDate) " +
			"        OR (to_date(:P_END_DATE, 'MI/MM/YYYY') between v.startDate and v.endDate) " +
			"        OR (to_date(:P_START_DATE, 'MI/MM/YYYY') <= v.startDate and to_date(:P_END_DATE, 'MI/MM/YYYY') >= v.endDate) " +
			"   ) "),

	@NamedQuery(name = "wf_vacation_getVacationsTasks",
		query = " select v, t, i, p.processName, requester, beneficiary, " +
			" case when t.originalId <> t.assigneeId then " +
			" (delegator.firstName || ' ' || delegator.secondName || ' ' || delegator.thirdName || case when delegator.thirdName is null then '' else ' ' end || delegator.lastName) " +
			" else null end " +
			" from WFVacation v, WFTask t, WFInstance i, WFProcess p, EmployeeData requester, Employee delegator, EmployeeData beneficiary" +
			" where v.instanceId 	= t.instanceId " +
			"   and v.instanceId 	= i.id " +
			"   and i.processId  	= p.id " +
			"   and v.beneficiaryId 	= beneficiary.id " +
			"   and i.requesterId 	= requester.id " +
			"   and t.originalId 	= delegator.id " +
			"   and t.action is null " +
			"   and t.assigneeId       = :P_ASSIGNEE_ID " +
			"   and t.assigneeWfRole 	= :P_ASSIGNEE_WF_ROLE " +
			"   order by t.taskId ")
})
@Entity
@Table(name = "WF_VACATIONS")
public class WFVacation extends BaseEntity {
    private Long instanceId;
    private Integer requestType;
    private Long vacationTypeId;
    private Integer subVacationType;
    private Date startDate;
    private String startDateString;
    private Date endDate;
    private String endDateString;
    private Integer period;
    private Integer locationFlag;
    private String location;
    private String contactNumber;
    private String notes;
    private Long oldVacationId;
    private Long beneficiaryId;
    private Long approvedId;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private String contactAddress;
    private Date extStartDate;
    private String extStartDateString;
    private Date extEndDate;
    private String extEndDateString;
    private Integer extPeriod;
    private String extLocation;
    private String reasons;
    private Long decisionRegionId;
    private Date contractualYearStartDate;
    private String contractualYearStartDateString;
    private Date contractualYearEndDate;
    private String contractualYearEndDateString;
    private String referring;
    private Integer exceededDays;
    private String joiningDateString;

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    @Basic
    @Column(name = "REQUEST_TYPE")
    @XmlTransient
    public Integer getRequestType() {
	return requestType;
    }

    public void setRequestType(Integer requestType) {
	this.requestType = requestType;
    }

    @Basic
    @Column(name = "VACATION_TYPE_ID")
    @XmlTransient
    public Long getVacationTypeId() {
	return vacationTypeId;
    }

    public void setVacationTypeId(Long vacationTypeId) {
	this.vacationTypeId = vacationTypeId;
    }

    @Basic
    @Column(name = "SUB_VACATION_TYPE")
    @XmlTransient
    public Integer getSubVacationType() {
	return subVacationType;
    }

    public void setSubVacationType(Integer subVacationType) {
	this.subVacationType = subVacationType;
    }

    @Basic
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getStartDate() {
	return startDate;
    }

    public void setStartDate(Date startDate) {
	this.startDate = startDate;
	this.startDateString = HijriDateService.getHijriDateString(startDate);

	if (instanceId == null) {
	    try {
		if (startDate != null && period != null && period > 0)
		    this.setEndDateString(HijriDateService.addSubStringHijriDays(startDateString, period - 1));
		else
		    this.setEndDateString(null);
	    } catch (Exception e) {
		e.printStackTrace();
		this.setEndDateString(null);
	    }
	}
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
    @XmlTransient
    public Date getEndDate() {
	return endDate;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
	this.endDateString = HijriDateService.getHijriDateString(endDate);
    }

    @Transient
    @XmlTransient
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

	if (instanceId == null) {
	    try {
		if (startDate != null && period != null && period > 0)
		    this.setEndDateString(HijriDateService.addSubStringHijriDays(startDateString, period - 1));
		else
		    this.setEndDateString(null);
	    } catch (Exception e) {
		e.printStackTrace();
		this.setEndDateString(null);
	    }
	}
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
    @Column(name = "CONTACT_NUMBER")
    @XmlElement(nillable = true)
    public String getContactNumber() {
	return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
	this.contactNumber = contactNumber;
    }

    @Basic
    @Column(name = "NOTES")
    @XmlElement(nillable = true)
    public String getNotes() {
	return notes;
    }

    public void setNotes(String notes) {
	this.notes = notes;
    }

    @Basic
    @Column(name = "OLD_VACATION_ID")
    @XmlTransient
    public Long getOldVacationId() {
	return oldVacationId;
    }

    public void setOldVacationId(Long oldVacationId) {
	this.oldVacationId = oldVacationId;
    }

    @Basic
    @Column(name = "BENEFICIARY_ID")
    @XmlTransient
    public Long getBeneficiaryId() {
	return beneficiaryId;
    }

    public void setBeneficiaryId(Long beneficiaryId) {
	this.beneficiaryId = beneficiaryId;
    }

    @Basic
    @Column(name = "APPROVED_ID")
    @XmlTransient
    public Long getApprovedId() {
	return approvedId;
    }

    public void setApprovedId(Long approvedId) {
	this.approvedId = approvedId;
    }

    @Basic
    @Column(name = "DECISION_APPROVED_ID")
    @XmlTransient
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    public void setDecisionApprovedId(Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
    }

    @Basic
    @Column(name = "ORIGINAL_DECISION_APPROVED_ID")
    @XmlTransient
    public Long getOriginalDecisionApprovedId() {
	return originalDecisionApprovedId;
    }

    public void setOriginalDecisionApprovedId(Long originalDecisionApprovedId) {
	this.originalDecisionApprovedId = originalDecisionApprovedId;
    }

    @Basic
    @Column(name = "CONTACT_ADDRESS")
    @XmlElement(nillable = true)
    public String getContactAddress() {
	return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
	this.contactAddress = contactAddress;
    }

    @Basic
    @Column(name = "EXT_START_DATE")
    @XmlTransient
    public Date getExtStartDate() {
	return extStartDate;
    }

    public void setExtStartDate(Date extStartDate) {
	this.extStartDate = extStartDate;
	this.extStartDateString = HijriDateService.getHijriDateString(extStartDate);

	if (instanceId == null) {
	    try {
		if (extStartDate != null && extPeriod != null && extPeriod > 0)
		    this.setExtEndDateString(HijriDateService.addSubStringHijriDays(extStartDateString, extPeriod - 1));
		else
		    this.setExtEndDateString(null);
	    } catch (Exception e) {
		e.printStackTrace();
		this.setExtEndDateString(null);
	    }
	}
    }

    @Transient
    @XmlTransient
    public String getExtStartDateString() {
	return extStartDateString;
    }

    public void setExtStartDateString(String extStartDateString) {
	this.extStartDateString = extStartDateString;
	this.extStartDate = HijriDateService.getHijriDate(extStartDateString);
    }

    @Basic
    @Column(name = "EXT_END_DATE")
    @XmlTransient
    public Date getExtEndDate() {
	return extEndDate;
    }

    public void setExtEndDate(Date extEndDate) {
	this.extEndDate = extEndDate;
	this.extEndDateString = HijriDateService.getHijriDateString(extEndDate);
    }

    @Transient
    @XmlTransient
    public String getExtEndDateString() {
	return extEndDateString;
    }

    public void setExtEndDateString(String extEndDateString) {
	this.extEndDateString = extEndDateString;
	this.extEndDate = HijriDateService.getHijriDate(extEndDateString);
    }

    @Basic
    @Column(name = "EXT_PERIOD")
    @XmlTransient
    public Integer getExtPeriod() {
	return extPeriod;
    }

    public void setExtPeriod(Integer extPeriod) {
	this.extPeriod = extPeriod;

	if (instanceId == null) {
	    try {
		if (extStartDate != null && extPeriod != null && extPeriod > 0)
		    this.setExtEndDateString(HijriDateService.addSubStringHijriDays(extStartDateString, extPeriod - 1));
		else
		    this.setExtEndDateString(null);
	    } catch (Exception e) {
		e.printStackTrace();
		this.setExtEndDateString(null);
	    }
	}
    }

    @Basic
    @Column(name = "EXT_LOCATION")
    @XmlTransient
    public String getExtLocation() {
	return extLocation;
    }

    public void setExtLocation(String extLocation) {
	this.extLocation = extLocation;
    }

    @Basic
    @Column(name = "REASONS")
    @XmlElement(nillable = true)
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
    }

    @Basic
    @Column(name = "DECISION_REGION_ID")
    @XmlTransient
    public Long getDecisionRegionId() {
	return decisionRegionId;
    }

    public void setDecisionRegionId(Long decisionRegionId) {
	this.decisionRegionId = decisionRegionId;
    }

    @Basic
    @Column(name = "CONTRACTUAL_YEAR_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getContractualYearStartDate() {
	return contractualYearStartDate;
    }

    public void setContractualYearStartDate(Date contractualYearStartDate) {
	this.contractualYearStartDate = contractualYearStartDate;
	this.contractualYearStartDateString = HijriDateService.getHijriDateString(contractualYearStartDate);
    }

    @Transient
    @XmlTransient
    public String getContractualYearStartDateString() {
	return contractualYearStartDateString;
    }

    public void setContractualYearStartDateString(String contractualYearStartDateString) {
	this.contractualYearStartDateString = contractualYearStartDateString;
	this.contractualYearStartDate = HijriDateService.getHijriDate(contractualYearStartDateString);
    }

    @Basic
    @Column(name = "CONTRACTUAL_YEAR_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getContractualYearEndDate() {
	return contractualYearEndDate;
    }

    public void setContractualYearEndDate(Date contractualYearEndDate) {
	this.contractualYearEndDate = contractualYearEndDate;
	this.contractualYearEndDateString = HijriDateService.getHijriDateString(contractualYearEndDate);
    }

    @Transient
    @XmlTransient
    public String getContractualYearEndDateString() {
	return contractualYearEndDateString;
    }

    public void setContractualYearEndDateString(String contractualYearEndDateString) {
	this.contractualYearEndDateString = contractualYearEndDateString;
	this.contractualYearEndDate = HijriDateService.getHijriDate(contractualYearEndDateString);
    }

    @Basic
    @Column(name = "REFERRING")
    @XmlTransient
    public String getReferring() {
	return referring;
    }

    public void setReferring(String referring) {
	this.referring = referring;
    }

    @Basic
    @Column(name = "EXCEEDED_DAYS")
    @XmlTransient
    public Integer getExceededDays() {
	return exceededDays;
    }

    public void setExceededDays(Integer exceededDays) {
	this.exceededDays = exceededDays;
	try {
	    if (exceededDays != null)
		this.setJoiningDateString(HijriDateService.addSubStringHijriDays(endDateString, exceededDays + 1));
	} catch (BusinessException e) {
	    e.printStackTrace();
	}
    }

    @Transient
    @XmlTransient
    public String getJoiningDateString() {
	return joiningDateString;
    }

    public void setJoiningDateString(String joiningDateString) {
	this.joiningDateString = joiningDateString;
    }
}
