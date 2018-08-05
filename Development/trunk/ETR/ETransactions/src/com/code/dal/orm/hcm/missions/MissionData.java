package com.code.dal.orm.hcm.missions;

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
import com.code.enums.FlagsEnum;
import com.code.services.util.HijriDateService;

/**
 * Provides all information regarding mission transaction view which is the view of {@link Mission} with additional data such as String-formatted dates to be readable for the users. <br/>
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_missionData_searchMissionData",
		query = " select md" +
			" from MissionData md" +
			" where " +
			"     (:P_ID = -1 or md.id = :P_ID) " +
			" and (:P_EMP_ID = -1 or md.id = (select mdd.missionId from MissionDetailData mdd where mdd.missionId = md.id and mdd.empId = :P_EMP_ID)) " +
			" and (:P_DECISION_NUMBER = '-1' or md.decisionNumber = :P_DECISION_NUMBER)" +
			" and (:P_LOCATION_FLAG = -1 or md.locationFlag = :P_LOCATION_FLAG) " +
			" and (:P_DESTINATION = '-1' or md.destination like :P_DESTINATION) " +
			" and (:P_PURPOSE = '-1' or md.purpose like :P_PURPOSE) " +
			" and (:P_FROM_DATE_FLAG = -1 or to_date(:P_FROM_DATE, 'MI/MM/YYYY') <= md.decisionDate) " +
			" and (:P_TO_DATE_FLAG = -1  or to_date(:P_TO_DATE, 'MI/MM/YYYY') >= md.decisionDate) " +
			" and (:P_CATEGORY_MODE = -1  or md.categoryMode = :P_CATEGORY_MODE ) " +
			" and ( md.eflag = 1 ) " +
			" and (:P_REGION_ID = -1 or md.decisionRegionId = :P_REGION_ID or (select count(mdd.empPhysicalRegionId) from MissionDetailData mdd where mdd.missionId = md.id and mdd.empPhysicalRegionId = :P_REGION_ID) > 0)" +
			" order by md.id "),
	
	@NamedQuery(name = "hcm_missionData_searchMissionRequest",
		query = " select md" +
			" from MissionData md" +
			" where " +
			"     (md.id = (select mdd.missionId from MissionDetailData mdd where mdd.missionId = md.id and mdd.empId = :P_EMP_ID)) " +
			" and (to_date(:P_FROM_DATE, 'MI/MM/YYYY') = md.startDate) " +
			" and (to_date(:P_TO_DATE, 'MI/MM/YYYY') = md.endDate) " +
			" and ( md.eflag = 1 ) " +
			" order by md.id ")
})
@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_VW_MSN_TRANSACTIONS")
public class MissionData extends BaseEntity {

    private Long id;
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
    private Integer eflag;
    private Integer migFlag;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;
    private String attachments;
    private Integer categoryMode;
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

    private Mission mission;

    public MissionData() {
	mission = new Mission();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	mission.setId(id);
    }

    @Basic
    @Column(name = "REFERRING")
    public String getReferring() {
	return referring;
    }

    public void setReferring(String referring) {
	this.referring = referring;
	mission.setReferring(referring);
    }

    @Basic
    @Column(name = "LOCATION_FLAG")
    public Integer getLocationFlag() {
	return locationFlag;
    }

    public void setLocationFlag(Integer locationFlag) {
	this.locationFlag = locationFlag;
	mission.setLocationFlag(locationFlag);
	if (this.locationFlag == null || this.locationFlag == FlagsEnum.OFF.getCode()) {
	    this.locationFlagBoolean = false;
	} else {
	    this.locationFlagBoolean = true;
	}
    }

    @Transient
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
	mission.setLocation(location);
    }

    @Basic
    @Column(name = "REGIONS_CODES")
    public String getRegionsCodes() {
	return regionsCodes;
    }

    public void setRegionsCodes(String regionsCodes) {
	this.regionsCodes = regionsCodes;
	mission.setRegionsCodes(regionsCodes);
    }

    @Basic
    @Column(name = "DESTINATION")
    public String getDestination() {
	return destination;
    }

    public void setDestination(String destination) {
	this.destination = destination;
	mission.setDestination(destination);
    }

    @Basic
    @Column(name = "PURPOSE")
    public String getPurpose() {
	return purpose;
    }

    public void setPurpose(String purpose) {
	this.purpose = purpose;
	mission.setPurpose(purpose);
    }

    @Basic
    @Column(name = "EMP_EXTERNAL_FLAG")
    public Integer getEmpExternalFlag() {
	return empExternalFlag;
    }

    public void setEmpExternalFlag(Integer empExternalFlag) {
	this.empExternalFlag = empExternalFlag;
	mission.setEmpExternalFlag(empExternalFlag);
	if (this.empExternalFlag == null || this.empExternalFlag == FlagsEnum.OFF.getCode()) {
	    this.empExternalFlagBoolean = false;
	} else {
	    this.empExternalFlagBoolean = true;
	}
    }

    @Transient
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
    public Integer getEmpExternalStatus() {
	return empExternalStatus;
    }

    public void setEmpExternalStatus(Integer empExternalStatus) {
	this.empExternalStatus = empExternalStatus;
	mission.setEmpExternalStatus(empExternalStatus);
    }

    @Basic
    @Column(name = "PERIOD")
    public Integer getPeriod() {
	return period;
    }

    public void setPeriod(Integer period) {
	this.period = period;
	mission.setPeriod(period);
    }

    @Basic
    @Column(name = "ROAD_PERIOD")
    public Integer getRoadPeriod() {
	return roadPeriod;
    }

    public void setRoadPeriod(Integer roadPeriod) {
	this.roadPeriod = roadPeriod;
	mission.setRoadPeriod(roadPeriod);
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
	mission.setStartDate(startDate);
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
    public Date getEndDate() {
	return endDate;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
	this.endDateString = HijriDateService.getHijriDateString(endDate);
	mission.setEndDate(endDate);
    }

    @Transient
    public String getEndDateString() {
	return endDateString;
    }

    public void setEndDateString(String endDateString) {
	this.endDateString = endDateString;
	this.setEndDate(HijriDateService.getHijriDate(endDateString));
    }

    @Basic
    @Column(name = "MINISTERY_APPROVAL_NUMBER")
    public String getMinisteryApprovalNumber() {
	return ministeryApprovalNumber;
    }

    public void setMinisteryApprovalNumber(String ministeryApprovalNumber) {
	this.ministeryApprovalNumber = ministeryApprovalNumber;
	mission.setMinisteryApprovalNumber(ministeryApprovalNumber);
    }

    @Basic
    @Column(name = "MINISTERY_APPROVAL_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMinisteryApprovalDate() {
	return ministeryApprovalDate;
    }

    public void setMinisteryApprovalDate(Date ministeryApprovalDate) {
	this.ministeryApprovalDate = ministeryApprovalDate;
	this.ministeryApprovalDateString = HijriDateService.getHijriDateString(ministeryApprovalDate);
	mission.setMinisteryApprovalDate(ministeryApprovalDate);
    }

    @Transient
    public String getMinisteryApprovalDateString() {
	return ministeryApprovalDateString;
    }

    public void setMinisteryApprovalDateString(String ministeryApprovalDateString) {
	this.ministeryApprovalDateString = ministeryApprovalDateString;
	this.setMinisteryApprovalDate(HijriDateService.getHijriDate(ministeryApprovalDateString));
    }

    @Basic
    @Column(name = "ROAD_LINE")
    public String getRoadLine() {
	return roadLine;
    }

    public void setRoadLine(String roadLine) {
	this.roadLine = roadLine;
	mission.setRoadLine(roadLine);
    }

    @Basic
    @Column(name = "DOUBLE_BALANCE_FLAG")
    public Integer getDoubleBalanceFlag() {
	return doubleBalanceFlag;
    }

    public void setDoubleBalanceFlag(Integer doubleBalanceFlag) {
	this.doubleBalanceFlag = doubleBalanceFlag;
	mission.setDoubleBalanceFlag(doubleBalanceFlag);
	if (this.doubleBalanceFlag == null || this.doubleBalanceFlag == FlagsEnum.OFF.getCode()) {
	    this.doubleBalanceFlagBoolean = false;
	} else {
	    this.doubleBalanceFlagBoolean = true;
	}
    }

    @Transient
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
	mission.setHajjFlag(hajjFlag);
	if (this.hajjFlag == null || this.hajjFlag == FlagsEnum.OFF.getCode()) {
	    this.hajjFlagBoolean = false;
	} else {
	    this.hajjFlagBoolean = true;
	}
    }

    @Transient
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
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
	mission.setRemarks(remarks);
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    public String getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	this.internalCopies = internalCopies;
	mission.setInternalCopies(internalCopies);
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
	mission.setExternalCopies(externalCopies);
    }

    @Basic
    @Column(name = "APPROVED_ID")
    public Long getApprovedId() {
	return approvedId;
    }

    public void setApprovedId(Long approvedId) {
	this.approvedId = approvedId;
	mission.setApprovedId(approvedId);
    }

    @Basic
    @Column(name = "DECISION_APPROVED_ID")
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    public void setDecisionApprovedId(Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
	mission.setDecisionApprovedId(decisionApprovedId);
    }

    @Basic
    @Column(name = "ORIGINAL_DECISION_APPROVED_ID")
    public Long getOriginalDecisionApprovedId() {
	return originalDecisionApprovedId;
    }

    public void setOriginalDecisionApprovedId(Long originalDecisionApprovedId) {
	this.originalDecisionApprovedId = originalDecisionApprovedId;
	mission.setOriginalDecisionApprovedId(originalDecisionApprovedId);
    }

    @Basic
    @Column(name = "EFLAG")
    public Integer getEflag() {
	return eflag;
    }

    public void setEflag(Integer eflag) {
	this.eflag = eflag;
	mission.setEflag(eflag);
    }

    @Basic
    @Column(name = "MIG_FLAG")
    public Integer getMigFlag() {
	return migFlag;
    }

    public void setMigFlag(Integer migFlag) {
	this.migFlag = migFlag;
	mission.setMigFlag(migFlag);
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
	mission.setDecisionNumber(decisionNumber);
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
	mission.setDecisionDate(decisionDate);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.setDecisionDate(HijriDateService.getHijriDate(decisionDateString));
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
	mission.setAttachments(attachments);
    }

    @Basic
    @Column(name = "CATEGORY_MODE")
    public Integer getCategoryMode() {
	return categoryMode;
    }

    public void setCategoryMode(Integer categoryMode) {
	this.categoryMode = categoryMode;
	mission.setCategoryMode(categoryMode);
    }

    @Basic
    @Column(name = "PREVIOUS_FLAG")
    public Integer getPreviousFlag() {
	return previousFlag;
    }

    public void setPreviousFlag(Integer previousFlag) {
	this.previousFlag = previousFlag;
	mission.setPreviousFlag(previousFlag);
    }

    @Basic
    @Column(name = "PREVIOUS_DESC")
    public String getPreviousDescription() {
	return previousDescription;
    }

    public void setPreviousDescription(String previousDescription) {
	this.previousDescription = previousDescription;
	mission.setPreviousDescription(previousDescription);
    }

    @Basic
    @Column(name = "PREVIOUS_EXECUTED_FLAG")
    public Integer getPreviousExecutedFlag() {
	return previousExecutedFlag;
    }

    public void setPreviousExecutedFlag(Integer previousExecutedFlag) {
	this.previousExecutedFlag = previousExecutedFlag;
	mission.setPreviousExecutedFlag(previousExecutedFlag);
	if (this.previousExecutedFlag == null || this.previousExecutedFlag == FlagsEnum.OFF.getCode()) {
	    this.previousExecutedFlagBoolean = false;
	} else {
	    this.previousExecutedFlagBoolean = true;
	}
    }

    @Transient
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
    public Integer getRelatedFlag() {
	return relatedFlag;
    }

    public void setRelatedFlag(Integer relatedFlag) {
	this.relatedFlag = relatedFlag;
	mission.setRelatedFlag(relatedFlag);
	if (this.relatedFlag == null || this.relatedFlag == FlagsEnum.OFF.getCode()) {
	    this.relatedFlagBoolean = false;
	} else {
	    this.relatedFlagBoolean = true;
	}
    }

    @Transient
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
    public Integer getNotExecutedRelatedFlag() {
	return notExecutedRelatedFlag;
    }

    public void setNotExecutedRelatedFlag(Integer notExecutedRelatedFlag) {
	this.notExecutedRelatedFlag = notExecutedRelatedFlag;
	mission.setNotExecutedRelatedFlag(notExecutedRelatedFlag);
	if (this.notExecutedRelatedFlag == null || this.notExecutedRelatedFlag == FlagsEnum.OFF.getCode()) {
	    this.notExecutedRelatedFlagBoolean = false;
	} else {
	    this.notExecutedRelatedFlagBoolean = true;
	}
    }

    @Transient
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
    public String getRelatedDescription() {
	return relatedDescription;
    }

    public void setRelatedDescription(String relatedDescription) {
	this.relatedDescription = relatedDescription;
	mission.setRelatedDescription(relatedDescription);
    }

    @Basic
    @Column(name = "BASED_ON_FLAG")
    public Integer getBasedOnFlag() {
	return basedOnFlag;
    }

    public void setBasedOnFlag(Integer basedOnFlag) {
	this.basedOnFlag = basedOnFlag;
	mission.setBasedOnFlag(basedOnFlag);
    }

    @Basic
    @Column(name = "BASED_ON_NUMBER")
    public String getBasedOnNumber() {
	return basedOnNumber;
    }

    public void setBasedOnNumber(String basedOnNumber) {
	this.basedOnNumber = basedOnNumber;
	mission.setBasedOnNumber(basedOnNumber);
    }

    @Basic
    @Column(name = "BASED_ON_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getBasedOnDate() {
	return basedOnDate;
    }

    public void setBasedOnDate(Date basedOnDate) {
	this.basedOnDate = basedOnDate;
	this.basedOnDateString = HijriDateService.getHijriDateString(basedOnDate);
	mission.setBasedOnDate(basedOnDate);
    }

    @Transient
    public String getBasedOnDateString() {
	return basedOnDateString;
    }

    public void setBasedOnDateString(String basedOnDateString) {
	this.basedOnDateString = basedOnDateString;
	this.setBasedOnDate(HijriDateService.getHijriDate(basedOnDateString));
    }

    @Basic
    @Column(name = "RESULT_REPORT_FLAG")
    public Integer getResultReportFlag() {
	return resultReportFlag;
    }

    public void setResultReportFlag(Integer resultReportFlag) {
	this.resultReportFlag = resultReportFlag;
	mission.setResultReportFlag(resultReportFlag);
	if (this.resultReportFlag == null || this.resultReportFlag == FlagsEnum.OFF.getCode()) {
	    this.resultReportFlagBoolean = false;
	} else {
	    this.resultReportFlagBoolean = true;
	}
    }

    @Transient
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
    public Integer getResultLetterFlag() {
	return resultLetterFlag;
    }

    public void setResultLetterFlag(Integer resultLetterFlag) {
	this.resultLetterFlag = resultLetterFlag;
	mission.setResultLetterFlag(resultLetterFlag);
	if (this.resultLetterFlag == null || this.resultLetterFlag == FlagsEnum.OFF.getCode()) {
	    this.resultLetterFlagBoolean = false;
	} else {
	    this.resultLetterFlagBoolean = true;
	}
    }

    @Transient
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
    public Integer getResultOtherFlag() {
	return resultOtherFlag;
    }

    public void setResultOtherFlag(Integer resultOtherFlag) {
	this.resultOtherFlag = resultOtherFlag;
	mission.setResultOtherFlag(resultOtherFlag);
	if (this.resultOtherFlag == null || this.resultOtherFlag == FlagsEnum.OFF.getCode()) {
	    this.resultOtherFlagBoolean = false;
	} else {
	    this.resultOtherFlagBoolean = true;
	}
    }

    @Transient
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
    public String getResultLetterDescription() {
	return resultLetterDescription;
    }

    public void setResultLetterDescription(String resultLetterDescription) {
	this.resultLetterDescription = resultLetterDescription;
	mission.setResultLetterDescription(resultLetterDescription);
    }

    @Basic
    @Column(name = "RESULT_OTHER_DESC")
    public String getResultOtherDescription() {
	return resultOtherDescription;
    }

    public void setResultOtherDescription(String resultOtherDescription) {
	this.resultOtherDescription = resultOtherDescription;
	mission.setResultOtherDescription(resultOtherDescription);
    }

    @Basic
    @Column(name = "DECISION_REGION_ID")
    public Long getDecisionRegionId() {
	return decisionRegionId;
    }

    public void setDecisionRegionId(Long decisionRegionId) {
	this.decisionRegionId = decisionRegionId;
	mission.setDecisionRegionId(decisionRegionId);

    }

    @Transient
    public Mission getMission() {
	return mission;
    }

    public void setMission(Mission mission) {
	this.mission = mission;
    }

    @Basic
    @Column(name = "DIRECTED_TO_JOB_NAME")
    public String getDirectedToJobName() {
	return directedToJobName;
    }

    public void setDirectedToJobName(String directedToJobName) {
	this.directedToJobName = directedToJobName;
	mission.setDirectedToJobName(directedToJobName);
    }

}
