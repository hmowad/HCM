package com.code.dal.orm.workflow.hcm.missions;

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
import com.code.enums.FlagsEnum;
import com.code.services.util.HijriDateService;

/**
 * 
 * Provides all information regarding mission workflow View Object which is the view of {@link WFMission} with additional data such as String-formatted dates to be readable for the users. <br/>
 * 
 */
@NamedQueries({
	@NamedQuery(name = "wf_mission_getWFMissionByInstanceId",
		query = " select m from WFMissionData m " +
			" where m.instanceId = :P_INSTANCE_ID "),

	@NamedQuery(name = "wf_mission_getMessionsTasks",
		query = " select m, t, i, p.processName, requester, " +
			" case when t.originalId <> t.assigneeId then " +
			" (delegator.firstName || ' ' || delegator.secondName || ' ' || delegator.thirdName || case when delegator.thirdName is null then '' else ' ' end || delegator.lastName) " +
			" else null end " +
			" from WFMissionData m, WFTask t, WFInstance i, WFProcess p, EmployeeData requester, Employee delegator " +
			" where m.instanceId 	= t.instanceId " +
			"   and m.instanceId 	= i.id " +
			"   and i.processId  	= p.id " +
			"   and i.requesterId 	= requester.id " +
			"   and t.originalId 	= delegator.id " +
			"   and t.action is null " +
			"   and t.assigneeId       = :P_ASSIGNEE_ID " +
			"   and t.assigneeWfRole 	= :P_ASSIGNEE_WF_ROLE " +
			"   order by t.taskId ")
})
@SuppressWarnings("serial")
@Entity
@Table(name = "ETR_VW_WF_MISSIONS")
public class WFMissionData extends BaseEntity {

    private Long instanceId;
    private String referring;
    private Integer locationFlag;
    private Boolean locationFlagBoolean;
    private String location;
    private String regionsCodes;
    private String destination;
    private String purpose;
    private Integer empExternalFlag;
    private Boolean empExternalFlagBoolean;
    private Integer empExternalStatus;
    private Integer period;
    private Integer roadPeriod;
    private Date startDate;
    private String startDateString;
    private Date endDate;
    private String endDateString;
    private String ministeryApprovalNumber;
    private Date ministeryApprovalDate;
    private String ministeryApprovalDateString;
    private String roadLine;
    private Integer doubleBalanceFlag;
    private Boolean doubleBalanceFlagBoolean;
    private Integer hajjFlag;
    private Boolean hajjFlagBoolean;
    private String remarks;
    private String internalCopies;
    private String externalCopies;
    private Long approvedId;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private Long decisionRegionId;
    private String directedToJobName;

    private Integer previousFlag;
    private String previousDescription;
    private Integer previousExecutedFlag;
    private Boolean previousExecutedFlagBoolean;
    private Integer relatedFlag;
    private Boolean relatedFlagBoolean;
    private Integer notExecutedRelatedFlag;
    private Boolean notExecutedRelatedFlagBoolean;
    private String relatedDescription;
    private Integer basedOnFlag;
    private String basedOnNumber;
    private Date basedOnDate;
    private String basedOnDateString;
    private Integer resultReportFlag;
    private Boolean resultReportFlagBoolean;
    private Integer resultLetterFlag;
    private Boolean resultLetterFlagBoolean;
    private Integer resultOtherFlag;
    private Boolean resultOtherFlagBoolean;
    private String resultLetterDescription;
    private String resultOtherDescription;

    private WFMission wfMission;

    public WFMissionData() {
	wfMission = new WFMission();
    }

    @Basic
    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
	wfMission.setInstanceId(instanceId);
    }

    @Basic
    @Column(name = "REFERRING")
    @XmlTransient
    public String getReferring() {
	return referring;
    }

    public void setReferring(String referring) {
	this.referring = referring;
	wfMission.setReferring(referring);
    }

    @Basic
    @Column(name = "LOCATION_FLAG")
    public Integer getLocationFlag() {
	return locationFlag;
    }

    public void setLocationFlag(Integer locationFlag) {
	this.locationFlag = locationFlag;
	wfMission.setLocationFlag(locationFlag);
	if (this.locationFlag == null || this.locationFlag == FlagsEnum.OFF.getCode()) {
	    this.locationFlagBoolean = false;
	} else {
	    this.locationFlagBoolean = true;
	}
    }

    @Transient
    @XmlTransient
    public Boolean getLocationFlagBoolean() {
	return locationFlagBoolean;
    }

    public void setLocationFlagBoolean(Boolean locationFlagBoolean) {
	this.locationFlagBoolean = locationFlagBoolean;
	if (this.locationFlagBoolean == false) {
	    setLocationFlag(FlagsEnum.OFF.getCode());
	} else {
	    setLocationFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "LOCATION")
    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
	wfMission.setLocation(location);
    }

    @Basic
    @Column(name = "REGIONS_CODES")
    @XmlTransient
    public String getRegionsCodes() {
	return regionsCodes;
    }

    public void setRegionsCodes(String regionsCodes) {
	this.regionsCodes = regionsCodes;
	wfMission.setRegionsCodes(regionsCodes);
    }

    @Basic
    @Column(name = "DESTINATION")
    public String getDestination() {
	return destination;
    }

    public void setDestination(String destination) {
	this.destination = destination;
	wfMission.setDestination(destination);
    }

    @Basic
    @Column(name = "PURPOSE")
    public String getPurpose() {
	return purpose;
    }

    public void setPurpose(String purpose) {
	this.purpose = purpose;
	wfMission.setPurpose(purpose);
    }

    @Basic
    @Column(name = "EMP_EXTERNAL_FLAG")
    @XmlTransient
    public Integer getEmpExternalFlag() {
	return empExternalFlag;
    }

    public void setEmpExternalFlag(Integer empExternalFlag) {
	this.empExternalFlag = empExternalFlag;
	wfMission.setEmpExternalFlag(empExternalFlag);
	if (this.empExternalFlag == null || this.empExternalFlag == FlagsEnum.OFF.getCode()) {
	    this.empExternalFlagBoolean = false;
	} else {
	    this.empExternalFlagBoolean = true;
	}
    }

    @Transient
    @XmlTransient
    public Boolean getEmpExternalFlagBoolean() {
	return empExternalFlagBoolean;
    }

    public void setEmpExternalFlagBoolean(Boolean empExternalFlagBoolean) {
	this.empExternalFlagBoolean = empExternalFlagBoolean;
	if (this.empExternalFlagBoolean == false) {
	    setEmpExternalFlag(FlagsEnum.OFF.getCode());
	} else {
	    setEmpExternalFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "EMP_EXTERNAL_STATUS")
    @XmlTransient
    public Integer getEmpExternalStatus() {
	return empExternalStatus;
    }

    public void setEmpExternalStatus(Integer empExternalStatus) {
	this.empExternalStatus = empExternalStatus;
	wfMission.setEmpExternalStatus(empExternalStatus);
    }

    @Basic
    @Column(name = "PERIOD")
    public Integer getPeriod() {
	return period;
    }

    public void setPeriod(Integer period) {
	this.period = period;
	wfMission.setPeriod(period);
    }

    @Basic
    @Column(name = "ROAD_PERIOD")
    public Integer getRoadPeriod() {
	return roadPeriod;
    }

    public void setRoadPeriod(Integer roadPeriod) {
	if (roadPeriod == null) {
	    roadPeriod = FlagsEnum.OFF.getCode();
	}
	this.roadPeriod = roadPeriod;
	wfMission.setRoadPeriod(roadPeriod);
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
	wfMission.setStartDate(startDate);
    }

    @Transient
    public String getStartDateString() {
	return startDateString;
    }

    public void setStartDateString(String startDateString) {
	this.startDateString = startDateString;
	this.setStartDate(HijriDateService.getHijriDate(startDateString));
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
	wfMission.setEndDate(endDate);
    }

    @Transient
    @XmlTransient
    public String getEndDateString() {
	return endDateString;
    }

    public void setEndDateString(String endDateString) {
	this.endDateString = endDateString;
	this.setEndDate(HijriDateService.getHijriDate(endDateString));
    }

    @Basic
    @Column(name = "MINISTERY_APPROVAL_NUMBER")
    @XmlElement(nillable = true)
    public String getMinisteryApprovalNumber() {
	return ministeryApprovalNumber;
    }

    public void setMinisteryApprovalNumber(String ministeryApprovalNumber) {
	this.ministeryApprovalNumber = ministeryApprovalNumber;
	wfMission.setMinisteryApprovalNumber(ministeryApprovalNumber);
    }

    @Basic
    @Column(name = "MINISTERY_APPROVAL_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getMinisteryApprovalDate() {
	return ministeryApprovalDate;
    }

    public void setMinisteryApprovalDate(Date ministeryApprovalDate) {
	this.ministeryApprovalDate = ministeryApprovalDate;
	this.ministeryApprovalDateString = HijriDateService.getHijriDateString(ministeryApprovalDate);
	wfMission.setMinisteryApprovalDate(ministeryApprovalDate);
    }

    @Transient
    @XmlElement(nillable = true)
    public String getMinisteryApprovalDateString() {
	return ministeryApprovalDateString;
    }

    public void setMinisteryApprovalDateString(String ministeryApprovalDateString) {
	this.ministeryApprovalDateString = ministeryApprovalDateString;
	this.setMinisteryApprovalDate(HijriDateService.getHijriDate(ministeryApprovalDateString));
    }

    @Basic
    @Column(name = "ROAD_LINE")
    @XmlElement(nillable = true)
    public String getRoadLine() {
	return roadLine;
    }

    public void setRoadLine(String roadLine) {
	this.roadLine = roadLine;
	wfMission.setRoadLine(roadLine);
    }

    @Basic
    @Column(name = "DOUBLE_BALANCE_FLAG")
    @XmlTransient
    public Integer getDoubleBalanceFlag() {
	return doubleBalanceFlag;
    }

    public void setDoubleBalanceFlag(Integer doubleBalanceFlag) {
	this.doubleBalanceFlag = doubleBalanceFlag;
	wfMission.setDoubleBalanceFlag(doubleBalanceFlag);
	if (this.doubleBalanceFlag == null || this.doubleBalanceFlag == FlagsEnum.OFF.getCode()) {
	    this.doubleBalanceFlagBoolean = false;
	} else {
	    this.doubleBalanceFlagBoolean = true;
	}
    }

    @Transient
    @XmlTransient
    public Boolean getDoubleBalanceFlagBoolean() {
	return doubleBalanceFlagBoolean;
    }

    public void setDoubleBalanceFlagBoolean(Boolean doubleBalanceFlagBoolean) {
	this.doubleBalanceFlagBoolean = doubleBalanceFlagBoolean;
	if (this.doubleBalanceFlagBoolean == false) {
	    setDoubleBalanceFlag(FlagsEnum.OFF.getCode());
	} else {
	    setDoubleBalanceFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "HAJJ_FLAG")
    public Integer getHajjFlag() {
	return hajjFlag;
    }

    public void setHajjFlag(Integer hajjFlag) {
	this.hajjFlag = hajjFlag;
	wfMission.setHajjFlag(hajjFlag);
	if (this.hajjFlag == null || this.hajjFlag == FlagsEnum.OFF.getCode()) {
	    this.hajjFlagBoolean = false;
	} else {
	    this.hajjFlagBoolean = true;
	}
    }

    @Transient
    @XmlTransient
    public Boolean getHajjFlagBoolean() {
	return hajjFlagBoolean;
    }

    public void setHajjFlagBoolean(Boolean hajjFlagBoolean) {
	this.hajjFlagBoolean = hajjFlagBoolean;
	if (this.hajjFlagBoolean == false) {
	    setHajjFlag(FlagsEnum.OFF.getCode());
	} else {
	    setHajjFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "REMARKS")
    @XmlTransient
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
	wfMission.setRemarks(remarks);
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    @XmlTransient
    public String getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	this.internalCopies = internalCopies;
	wfMission.setInternalCopies(internalCopies);
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    @XmlTransient
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
	wfMission.setExternalCopies(externalCopies);
    }

    @Basic
    @Column(name = "APPROVED_ID")
    @XmlTransient
    public Long getApprovedId() {
	return approvedId;
    }

    public void setApprovedId(Long approvedId) {
	this.approvedId = approvedId;
	wfMission.setApprovedId(approvedId);
    }

    @Basic
    @Column(name = "DECISION_APPROVED_ID")
    @XmlTransient
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    public void setDecisionApprovedId(Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
	wfMission.setDecisionApprovedId(decisionApprovedId);
    }

    @Basic
    @Column(name = "ORIGINAL_DECISION_APPROVED_ID")
    @XmlTransient
    public Long getOriginalDecisionApprovedId() {
	return originalDecisionApprovedId;
    }

    public void setOriginalDecisionApprovedId(Long originalDecisionApprovedId) {
	this.originalDecisionApprovedId = originalDecisionApprovedId;
	wfMission.setOriginalDecisionApprovedId(originalDecisionApprovedId);
    }

    @Basic
    @Column(name = "PREVIOUS_FLAG")
    @XmlTransient
    public Integer getPreviousFlag() {
	return previousFlag;
    }

    public void setPreviousFlag(Integer previousFlag) {
	this.previousFlag = previousFlag;
	wfMission.setPreviousFlag(previousFlag);
    }

    @Basic
    @Column(name = "PREVIOUS_DESC")
    @XmlTransient
    public String getPreviousDescription() {
	return previousDescription;
    }

    public void setPreviousDescription(String previousDescription) {
	this.previousDescription = previousDescription;
	wfMission.setPreviousDescription(previousDescription);
    }

    @Basic
    @Column(name = "PREVIOUS_EXECUTED_FLAG")
    @XmlTransient
    public Integer getPreviousExecutedFlag() {
	return previousExecutedFlag;
    }

    public void setPreviousExecutedFlag(Integer previousExecutedFlag) {
	this.previousExecutedFlag = previousExecutedFlag;
	wfMission.setPreviousExecutedFlag(previousExecutedFlag);
	if (this.previousExecutedFlag == null || this.previousExecutedFlag == FlagsEnum.OFF.getCode()) {
	    this.previousExecutedFlagBoolean = false;
	} else {
	    this.previousExecutedFlagBoolean = true;
	}
    }

    @Transient
    @XmlTransient
    public Boolean getPreviousExecutedFlagBoolean() {
	return previousExecutedFlagBoolean;
    }

    public void setPreviousExecutedFlagBoolean(Boolean previousExecutedFlagBoolean) {
	this.previousExecutedFlagBoolean = previousExecutedFlagBoolean;
	if (this.previousExecutedFlagBoolean == false) {
	    setPreviousExecutedFlag(FlagsEnum.OFF.getCode());
	} else {
	    setPreviousExecutedFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "RELATED_FLAG")
    @XmlTransient
    public Integer getRelatedFlag() {
	return relatedFlag;
    }

    public void setRelatedFlag(Integer relatedFlag) {
	this.relatedFlag = relatedFlag;
	wfMission.setRelatedFlag(relatedFlag);
	if (this.relatedFlag == null || this.relatedFlag == FlagsEnum.OFF.getCode()) {
	    this.relatedFlagBoolean = false;
	} else {
	    this.relatedFlagBoolean = true;
	}
    }

    @Transient
    @XmlTransient
    public Boolean getRelatedFlagBoolean() {
	return relatedFlagBoolean;
    }

    public void setRelatedFlagBoolean(Boolean relatedFlagBoolean) {
	this.relatedFlagBoolean = relatedFlagBoolean;
	if (this.relatedFlagBoolean == false) {
	    setRelatedFlag(FlagsEnum.OFF.getCode());
	} else {
	    setRelatedFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "NOT_EXECUTED_RELATED_FLAG")
    @XmlTransient
    public Integer getNotExecutedRelatedFlag() {
	return notExecutedRelatedFlag;
    }

    public void setNotExecutedRelatedFlag(Integer notExecutedRelatedFlag) {
	this.notExecutedRelatedFlag = notExecutedRelatedFlag;
	wfMission.setNotExecutedRelatedFlag(notExecutedRelatedFlag);
	if (this.notExecutedRelatedFlag == null || this.notExecutedRelatedFlag == FlagsEnum.OFF.getCode()) {
	    this.notExecutedRelatedFlagBoolean = false;
	} else {
	    this.notExecutedRelatedFlagBoolean = true;
	}
    }

    @Transient
    @XmlTransient
    public Boolean getNotExecutedRelatedFlagBoolean() {
	return notExecutedRelatedFlagBoolean;
    }

    public void setNotExecutedRelatedFlagBoolean(Boolean notExecutedRelatedFlagBoolean) {
	this.notExecutedRelatedFlagBoolean = notExecutedRelatedFlagBoolean;
	if (this.notExecutedRelatedFlagBoolean == false) {
	    setNotExecutedRelatedFlag(FlagsEnum.OFF.getCode());
	} else {
	    setNotExecutedRelatedFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "RELATED_DESC")
    @XmlTransient
    public String getRelatedDescription() {
	return relatedDescription;
    }

    public void setRelatedDescription(String relatedDescription) {
	this.relatedDescription = relatedDescription;
	wfMission.setRelatedDescription(relatedDescription);
    }

    @Basic
    @Column(name = "BASED_ON_FLAG")
    @XmlTransient
    public Integer getBasedOnFlag() {
	return basedOnFlag;
    }

    public void setBasedOnFlag(Integer basedOnFlag) {
	this.basedOnFlag = basedOnFlag;
	wfMission.setBasedOnFlag(basedOnFlag);
    }

    @Basic
    @Column(name = "BASED_ON_NUMBER")
    @XmlTransient
    public String getBasedOnNumber() {
	return basedOnNumber;
    }

    public void setBasedOnNumber(String basedOnNumber) {
	this.basedOnNumber = basedOnNumber;
	wfMission.setBasedOnNumber(basedOnNumber);
    }

    @Basic
    @Column(name = "BASED_ON_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getBasedOnDate() {
	return basedOnDate;
    }

    public void setBasedOnDate(Date basedOnDate) {
	this.basedOnDate = basedOnDate;
	this.basedOnDateString = HijriDateService.getHijriDateString(basedOnDate);
	wfMission.setBasedOnDate(basedOnDate);
    }

    @Transient
    @XmlTransient
    public String getBasedOnDateString() {
	return basedOnDateString;
    }

    public void setBasedOnDateString(String basedOnDateString) {
	this.basedOnDateString = basedOnDateString;
	this.setBasedOnDate(HijriDateService.getHijriDate(basedOnDateString));
    }

    @Basic
    @Column(name = "RESULT_REPORT_FLAG")
    @XmlTransient
    public Integer getResultReportFlag() {
	return resultReportFlag;
    }

    public void setResultReportFlag(Integer resultReportFlag) {
	this.resultReportFlag = resultReportFlag;
	wfMission.setResultReportFlag(resultReportFlag);
	if (this.resultReportFlag == null || this.resultReportFlag == FlagsEnum.OFF.getCode()) {
	    this.resultReportFlagBoolean = false;
	} else {
	    this.resultReportFlagBoolean = true;
	}
    }

    @Transient
    @XmlTransient
    public Boolean getResultReportFlagBoolean() {
	return resultReportFlagBoolean;
    }

    public void setResultReportFlagBoolean(Boolean resultReportFlagBoolean) {
	this.resultReportFlagBoolean = resultReportFlagBoolean;
	if (this.resultReportFlagBoolean == false) {
	    setResultReportFlag(FlagsEnum.OFF.getCode());
	} else {
	    setResultReportFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "RESULT_LETTER_FLAG")
    @XmlTransient
    public Integer getResultLetterFlag() {
	return resultLetterFlag;
    }

    public void setResultLetterFlag(Integer resultLetterFlag) {
	this.resultLetterFlag = resultLetterFlag;
	wfMission.setResultLetterFlag(resultLetterFlag);
	if (this.resultLetterFlag == null || this.resultLetterFlag == FlagsEnum.OFF.getCode()) {
	    this.resultLetterFlagBoolean = false;
	} else {
	    this.resultLetterFlagBoolean = true;
	}
    }

    @Transient
    @XmlTransient
    public Boolean getResultLetterFlagBoolean() {
	return resultLetterFlagBoolean;
    }

    public void setResultLetterFlagBoolean(Boolean resultLetterFlagBoolean) {
	this.resultLetterFlagBoolean = resultLetterFlagBoolean;
	if (this.resultLetterFlagBoolean == false) {
	    setResultLetterFlag(FlagsEnum.OFF.getCode());
	} else {
	    setResultLetterFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "RESULT_OTHER_FLAG")
    @XmlTransient
    public Integer getResultOtherFlag() {
	return resultOtherFlag;
    }

    public void setResultOtherFlag(Integer resultOtherFlag) {
	this.resultOtherFlag = resultOtherFlag;
	wfMission.setResultOtherFlag(resultOtherFlag);
	if (this.resultOtherFlag == null || this.resultOtherFlag == FlagsEnum.OFF.getCode()) {
	    this.resultOtherFlagBoolean = false;
	} else {
	    this.resultOtherFlagBoolean = true;
	}
    }

    @Transient
    @XmlTransient
    public Boolean getResultOtherFlagBoolean() {
	return resultOtherFlagBoolean;
    }

    public void setResultOtherFlagBoolean(Boolean resultOtherFlagBoolean) {
	this.resultOtherFlagBoolean = resultOtherFlagBoolean;
	if (this.resultOtherFlagBoolean == false) {
	    setResultOtherFlag(FlagsEnum.OFF.getCode());
	} else {
	    setResultOtherFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "RESULT_LETTER_DESC")
    @XmlTransient
    public String getResultLetterDescription() {
	return resultLetterDescription;
    }

    public void setResultLetterDescription(String resultLetterDescription) {
	this.resultLetterDescription = resultLetterDescription;
	wfMission.setResultLetterDescription(resultLetterDescription);
    }

    @Basic
    @Column(name = "RESULT_OTHER_DESC")
    @XmlTransient
    public String getResultOtherDescription() {
	return resultOtherDescription;
    }

    public void setResultOtherDescription(String resultOtherDescription) {
	this.resultOtherDescription = resultOtherDescription;
	wfMission.setResultOtherDescription(resultOtherDescription);
    }

    @Transient
    @XmlTransient
    public WFMission getWfMission() {
	return wfMission;
    }

    public void setWfMission(WFMission wfMission) {
	this.wfMission = wfMission;
    }

    @Basic
    @Column(name = "DECISION_REGION_ID")
    @XmlTransient
    public Long getDecisionRegionId() {
	return decisionRegionId;
    }

    public void setDecisionRegionId(Long decisionRegionId) {
	this.decisionRegionId = decisionRegionId;
	wfMission.setDecisionRegionId(decisionRegionId);
    }

    @Basic
    @Column(name = "DIRECTED_TO_JOB_NAME")
    @XmlTransient
    public String getDirectedToJobName() {
	return directedToJobName;
    }

    public void setDirectedToJobName(String directedToJobName) {
	this.directedToJobName = directedToJobName;
	wfMission.setDirectedToJobName(directedToJobName);
    }

}
