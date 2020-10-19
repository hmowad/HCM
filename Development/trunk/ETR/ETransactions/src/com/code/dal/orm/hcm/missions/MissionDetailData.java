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
import javax.xml.bind.annotation.XmlTransient;

import com.code.dal.orm.BaseEntity;
import com.code.enums.FlagsEnum;
import com.code.services.util.HijriDateService;

/**
 * Provides all information regarding mission detail transaction view which is the view of {@link MissionDetail} with additional data such as String-formatted dates to be readable for the users. <br/>
 * There is one or more {@link MissionDetailData} for every {@link MissionData} linked by missionId <br/>
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_missionDetailData_searchMissionDetailDataByMissionId",
		query = " select m from MissionDetailData m" +
			" where m.missionId = :P_MISSION_ID " +
			" order by m.militaryNumber, m.rankId, m.recruitmentDate, m.empName "),

	@NamedQuery(name = "hcm_missionDetailData_searchMissionDetailDataByEmpId",
		query = " select m from MissionDetailData m" +
			" where m.empId = :P_EMP_ID" +
			" and ( m.eflag = 1 ) " +
			" order by m.missionDecisionDate desc "),

	@NamedQuery(name = "hcm_missionDetailData_searchMissionDetailData",
		query = " select m from MissionDetailData m" +
			" where (:P_EMP_ID = -1 or m.empId = :P_EMP_ID)" +
			"   and (:P_ID = -1 or m.id = :P_ID) " +
			"   and (:P_ABSENCE_FLAG = -1 or NVL(m.absenceFlag,0) = :P_ABSENCE_FLAG) " +
			" order by m.actualStartDate desc "),

	@NamedQuery(name = "hcm_missionDetailData_sumActualPariodAndRoadPariod",
		query = " select nvl(sum(nvl(m.actualPeriod,0)+nvl(d.roadPeriod,0)),0) " +
			" from MissionDetailData m, MissionData d" +
			" where (d.id = m.missionId) " +
			" and (:P_MISSION_ID = -1 or m.missionId != :P_MISSION_ID) " +
			" and (m.empId = :P_EMP_ID) " +
			" and (to_date(:P_FROM_DATE, 'MI/MM/YYYY') <= m.actualStartDate) " +
			" and (to_date(:P_TO_DATE, 'MI/MM/YYYY') >= m.actualStartDate) " +
			" and (NVL(m.absenceFlag,0) = 0) " +
			" and (d.hajjFlag = 0) "),

	@NamedQuery(name = "hcm_missionDetailData_getMissionTransactionAfterDecisionDate",
		query = (" select m from MissionDetailData m "
			+ " where (:P_EMP_ID = -1 or m.empId = :P_EMP_ID) "
			+ " and (:P_MISSION_DECISION_DATE_FLAG = -1 or to_date(:P_MISSION_DECISION_DATE, 'MI/MM/YYYY') <= m.missionDecisionDate) "
			+ " order by m.missionDecisionDate ")),

	@NamedQuery(name = "hcm_missionDetailData_getMissionsDetailsWithNoIncentivesByEmpId",
		query = (" select m from MissionDetailData m "
			+ " where m.empId = :P_EMP_ID "
			+ " and (select count(i.id) from IncentiveTransaction i where i.missionTransactionId = m.id) = 0 "
			+ " order by m.id "))
})
@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_VW_MSN_DETAILS")
public class MissionDetailData extends BaseEntity {

    private Long id;
    private Long missionId;
    private String missionDecisionNumber;
    private Date missionDecisionDate;
    private String missionDecisionDateString;
    private String missionDestination;
    private Integer missionLocationFlag;
    private String missionPurpose;
    private String missionAttachments;

    private Long empId;
    private String empName;
    private Long empPhysicalRegionId;
    private String transEmpUnitDesc;
    private String transEmpJobCode;
    private String transEmpJobDesc;
    private String transEmpRankDesc;
    private Integer balance;
    private Integer period;
    private Integer roadPeriod;
    private Date startDate;
    private String startDateString;
    private Date endDate;
    private String endDateString;
    private Integer actualPeriod;
    private Date actualStartDate;
    private String actualStartDateString;
    private Date actualEndDate;
    private String actualEndDateString;
    private String exceptionalApprovalNumber;
    private Date exceptionalApprovalDate;
    private String exceptionalApprovalDateString;
    private String roadLine;
    private Integer absenceFlag;
    private Boolean absenceFlagBoolean;
    private String absenceReasons;
    private String remarks;
    private Integer militaryNumber;
    private Long rankId;
    private Date recruitmentDate;
    private Date joiningDate;
    private String joiningDateString;
    private Integer eflag;
    private String closingAttachments;
    private Integer paymentDecisionIssuedFlag;
    private Boolean paymentDecisionIssuedFlagBoolean;
    private String paymentDecisionNumber;
    private Date paymentDecisionDate;
    private String paymentDecisionDateString;
    private Integer actualDataSavedFlag;
    private Boolean actualDataSavedFlagBoolean;

    private MissionDetail missionDetail;

    public MissionDetailData() {
	missionDetail = new MissionDetail();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	missionDetail.setId(id);
    }

    @Basic
    @Column(name = "MISSION_ID")
    @XmlTransient
    public Long getMissionId() {
	return missionId;
    }

    public void setMissionId(Long missionId) {
	this.missionId = missionId;
	missionDetail.setMissionId(missionId);
    }

    @Basic
    @Column(name = "MISSION_DECISION_NUMBER")
    public String getMissionDecisionNumber() {
	return missionDecisionNumber;
    }

    public void setMissionDecisionNumber(String missionDecisionNumber) {
	this.missionDecisionNumber = missionDecisionNumber;
    }

    @Basic
    @Column(name = "MISSION_DECISION_DATE")
    @XmlTransient
    public Date getMissionDecisionDate() {
	return missionDecisionDate;
    }

    public void setMissionDecisionDate(Date missionDecisionDate) {
	this.missionDecisionDate = missionDecisionDate;
	this.missionDecisionDateString = HijriDateService.getHijriDateString(missionDecisionDate);
    }

    @Transient
    public String getMissionDecisionDateString() {
	return missionDecisionDateString;
    }

    public void setMissionDecisionDateString(String missionDecisionDateString) {
	this.missionDecisionDateString = missionDecisionDateString;
	this.missionDecisionDate = HijriDateService.getHijriDate(missionDecisionDateString);
    }

    @Basic
    @Column(name = "MISSION_DESTINATION")
    public String getMissionDestination() {
	return missionDestination;
    }

    public void setMissionDestination(String missionDestination) {
	this.missionDestination = missionDestination;
    }

    @Basic
    @Column(name = "MISSION_LOCATION_FLAG")
    public Integer getMissionLocationFlag() {
	return missionLocationFlag;
    }

    public void setMissionLocationFlag(Integer missionLocationFlag) {
	this.missionLocationFlag = missionLocationFlag;
    }

    @Basic
    @Column(name = "MISSION_PURPOSE")
    public String getMissionPurpose() {
	return missionPurpose;
    }

    public void setMissionPurpose(String missionPurpose) {
	this.missionPurpose = missionPurpose;
    }

    @Basic
    @Column(name = "MISSION_ATTACHMENTS")
    @XmlTransient
    public String getMissionAttachments() {
	return missionAttachments;
    }

    public void setMissionAttachments(String missionAttachments) {
	this.missionAttachments = missionAttachments;
    }

    @Basic
    @Column(name = "EMP_ID")
    @XmlTransient
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	missionDetail.setEmpId(empId);
    }

    @Basic
    @Column(name = "EMP_NAME")
    @XmlTransient
    public String getEmpName() {
	return empName;
    }

    public void setEmpName(String empName) {
	this.empName = empName;
    }

    @Basic
    @Column(name = "EMP_PHYSICAL_REGION_ID")
    @XmlTransient
    public Long getEmpPhysicalRegionId() {
	return empPhysicalRegionId;
    }

    public void setEmpPhysicalRegionId(Long empPhysicalRegionId) {
	this.empPhysicalRegionId = empPhysicalRegionId;
    }

    @Basic
    @Column(name = "TRANS_EMP_UNIT_DESC")
    @XmlTransient
    public String getTransEmpUnitDesc() {
	return transEmpUnitDesc;
    }

    public void setTransEmpUnitDesc(String transEmpUnitDesc) {
	this.transEmpUnitDesc = transEmpUnitDesc;
	missionDetail.setTransEmpUnitDesc(transEmpUnitDesc);
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_CODE")
    @XmlTransient
    public String getTransEmpJobCode() {
	return transEmpJobCode;
    }

    public void setTransEmpJobCode(String transEmpJobCode) {
	this.transEmpJobCode = transEmpJobCode;
	missionDetail.setTransEmpJobCode(transEmpJobCode);
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_DESC")
    @XmlTransient
    public String getTransEmpJobDesc() {
	return transEmpJobDesc;
    }

    public void setTransEmpJobDesc(String transEmpJobDesc) {
	this.transEmpJobDesc = transEmpJobDesc;
	missionDetail.setTransEmpJobDesc(transEmpJobDesc);
    }

    @Basic
    @Column(name = "TRANS_EMP_RANK_DESC")
    @XmlTransient
    public String getTransEmpRankDesc() {
	return transEmpRankDesc;
    }

    public void setTransEmpRankDesc(String transEmpRankDesc) {
	this.transEmpRankDesc = transEmpRankDesc;
	missionDetail.setTransEmpRankDesc(transEmpRankDesc);
    }

    @Basic
    @Column(name = "BALANCE")
    @XmlTransient
    public Integer getBalance() {
	return balance;
    }

    public void setBalance(Integer balance) {
	this.balance = balance;
	missionDetail.setBalance(balance);
    }

    @Basic
    @Column(name = "PERIOD")
    public Integer getPeriod() {
	return period;
    }

    public void setPeriod(Integer period) {
	this.period = period;
	missionDetail.setPeriod(period);
    }

    @Basic
    @Column(name = "ROAD_PERIOD")
    @XmlTransient
    public Integer getRoadPeriod() {
	return roadPeriod;
    }

    public void setRoadPeriod(Integer roadPeriod) {
	this.roadPeriod = roadPeriod;
	missionDetail.setRoadPeriod(roadPeriod);
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
	missionDetail.setStartDate(startDate);
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
	missionDetail.setEndDate(endDate);
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
    @Column(name = "ACTUAL_PERIOD")
    @XmlTransient
    public Integer getActualPeriod() {
	return actualPeriod;
    }

    public void setActualPeriod(Integer actualPeriod) {
	this.actualPeriod = actualPeriod;
	missionDetail.setActualPeriod(actualPeriod);
    }

    @Basic
    @Column(name = "ACTUAL_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getActualStartDate() {
	return actualStartDate;
    }

    public void setActualStartDate(Date actualStartDate) {
	this.actualStartDate = actualStartDate;
	this.actualStartDateString = HijriDateService.getHijriDateString(actualStartDate);
	missionDetail.setActualStartDate(actualStartDate);
    }

    @Transient
    @XmlTransient
    public String getActualStartDateString() {
	return actualStartDateString;
    }

    public void setActualStartDateString(String actualStartDateString) {
	this.actualStartDateString = actualStartDateString;
	this.setActualStartDate(HijriDateService.getHijriDate(actualStartDateString));
    }

    @Basic
    @Column(name = "ACTUAL_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getActualEndDate() {
	return actualEndDate;
    }

    public void setActualEndDate(Date actualEndDate) {
	this.actualEndDate = actualEndDate;
	this.actualEndDateString = HijriDateService.getHijriDateString(actualEndDate);
	missionDetail.setActualEndDate(actualEndDate);
    }

    @Transient
    @XmlTransient
    public String getActualEndDateString() {
	return actualEndDateString;
    }

    public void setActualEndDateString(String actualEndDateString) {
	this.actualEndDateString = actualEndDateString;
	this.setActualEndDate(HijriDateService.getHijriDate(actualEndDateString));
    }

    @Basic
    @Column(name = "EXCEPTIONAL_APPROVAL_NUMBER")
    @XmlTransient
    public String getExceptionalApprovalNumber() {
	return exceptionalApprovalNumber;
    }

    public void setExceptionalApprovalNumber(String exceptionalApprovalNumber) {
	this.exceptionalApprovalNumber = exceptionalApprovalNumber;
	missionDetail.setExceptionalApprovalNumber(exceptionalApprovalNumber);
    }

    @Basic
    @Column(name = "EXCEPTIONAL_APPROVAL_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getExceptionalApprovalDate() {
	return exceptionalApprovalDate;
    }

    public void setExceptionalApprovalDate(Date exceptionalApprovalDate) {
	this.exceptionalApprovalDate = exceptionalApprovalDate;
	this.exceptionalApprovalDateString = HijriDateService.getHijriDateString(exceptionalApprovalDate);
	missionDetail.setExceptionalApprovalDate(exceptionalApprovalDate);
    }

    @Transient
    @XmlTransient
    public String getExceptionalApprovalDateString() {
	return exceptionalApprovalDateString;
    }

    public void setExceptionalApprovalDateString(String exceptionalApprovalDateString) {
	this.exceptionalApprovalDateString = exceptionalApprovalDateString;
	this.setExceptionalApprovalDate(HijriDateService.getHijriDate(exceptionalApprovalDateString));
    }

    @Basic
    @Column(name = "ROAD_LINE")
    @XmlTransient
    public String getRoadLine() {
	return roadLine;
    }

    public void setRoadLine(String roadLine) {
	this.roadLine = roadLine;
	missionDetail.setRoadLine(roadLine);
    }

    @Basic
    @Column(name = "ABSENCE_FLAG")
    @XmlTransient
    public Integer getAbsenceFlag() {
	return absenceFlag;
    }

    public void setAbsenceFlag(Integer absenceFlag) {
	this.absenceFlag = absenceFlag;
	missionDetail.setAbsenceFlag(absenceFlag);
	if (this.absenceFlag == null || this.absenceFlag == FlagsEnum.OFF.getCode())
	    this.absenceFlagBoolean = false;
	else
	    this.absenceFlagBoolean = true;
    }

    @Transient
    @XmlTransient
    public Boolean getAbsenceFlagBoolean() {
	return absenceFlagBoolean;
    }

    public void setAbsenceFlagBoolean(Boolean absenceFlagBoolean) {
	this.absenceFlagBoolean = absenceFlagBoolean;
	if (this.absenceFlagBoolean == false)
	    setAbsenceFlag(FlagsEnum.OFF.getCode());
	else
	    setAbsenceFlag(FlagsEnum.ON.getCode());
    }

    @Basic
    @Column(name = "ABSENCE_REASONS")
    @XmlTransient
    public String getAbsenceReasons() {
	return absenceReasons;
    }

    public void setAbsenceReasons(String absenceReasons) {
	this.absenceReasons = absenceReasons;
	missionDetail.setAbsenceReasons(absenceReasons);
    }

    @Basic
    @Column(name = "REMARKS")
    @XmlTransient
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
	missionDetail.setRemarks(remarks);
    }

    @Basic
    @Column(name = "MILITARY_NUMBER")
    @XmlTransient
    public Integer getMilitaryNumber() {
	return militaryNumber;
    }

    public void setMilitaryNumber(Integer militaryNumber) {
	this.militaryNumber = militaryNumber;
    }

    @Basic
    @Column(name = "RANK_ID")
    @XmlTransient
    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
    }

    @Basic
    @Column(name = "RECRUITMENT_DATE")
    @XmlTransient
    public Date getRecruitmentDate() {
	return recruitmentDate;
    }

    public void setRecruitmentDate(Date recruitmentDate) {
	this.recruitmentDate = recruitmentDate;
    }

    @Basic
    @Column(name = "JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getJoiningDate() {
	return joiningDate;
    }

    public void setJoiningDate(Date joiningDate) {
	this.joiningDate = joiningDate;
	this.joiningDateString = HijriDateService.getHijriDateString(joiningDate);
	missionDetail.setJoiningDate(joiningDate);
    }

    @Transient
    @XmlTransient
    public String getJoiningDateString() {
	return joiningDateString;
    }

    public void setJoiningDateString(String joiningDateString) {
	this.joiningDateString = joiningDateString;
	this.setJoiningDate(HijriDateService.getHijriDate(joiningDateString));
    }

    @Transient
    @XmlTransient
    public MissionDetail getMissionDetail() {
	return missionDetail;
    }

    public void setMissionDetail(MissionDetail missionDetail) {
	this.missionDetail = missionDetail;
    }

    @Basic
    @Column(name = "EFLAG")
    @XmlTransient
    public Integer getEflag() {
	return eflag;
    }

    public void setEflag(Integer eflag) {
	this.eflag = eflag;
    }

    @Basic
    @Column(name = "CLOSING_ATTACHMENTS")
    @XmlTransient
    public String getClosingAttachments() {
	return closingAttachments;
    }

    public void setClosingAttachments(String closingAttachments) {
	this.closingAttachments = closingAttachments;
	missionDetail.setClosingAttachments(closingAttachments);
    }

    @Basic
    @Column(name = "PAYMENT_DECISION_ISSUED_FLAG")
    @XmlTransient
    public Integer getPaymentDecisionIssuedFlag() {
	return paymentDecisionIssuedFlag;
    }

    public void setPaymentDecisionIssuedFlag(Integer paymentDecisionIssuedFlag) {
	this.paymentDecisionIssuedFlag = paymentDecisionIssuedFlag;
	this.missionDetail.setPaymentDecisionIssuedFlag(paymentDecisionIssuedFlag);
	if (this.paymentDecisionIssuedFlag == null || this.paymentDecisionIssuedFlag == FlagsEnum.OFF.getCode()) {
	    this.paymentDecisionIssuedFlagBoolean = false;
	} else {
	    this.paymentDecisionIssuedFlagBoolean = true;
	}

    }

    @Transient
    @XmlTransient
    public Boolean getPaymentDecisionIssuedFlagBoolean() {
	return paymentDecisionIssuedFlagBoolean;
    }

    public void setPaymentDecisionIssuedFlagBoolean(Boolean paymentDecisionIssuedFlagBoolean) {
	this.paymentDecisionIssuedFlagBoolean = paymentDecisionIssuedFlagBoolean;
	if (this.paymentDecisionIssuedFlagBoolean == false) {
	    setPaymentDecisionIssuedFlag(FlagsEnum.OFF.getCode());
	} else {
	    setPaymentDecisionIssuedFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "PAYMENT_DECISION_NUMBER")
    @XmlTransient
    public String getPaymentDecisionNumber() {
	return paymentDecisionNumber;
    }

    public void setPaymentDecisionNumber(String paymentDecisionNumber) {
	this.paymentDecisionNumber = paymentDecisionNumber;
	this.missionDetail.setPaymentDecisionNumber(paymentDecisionNumber);
    }

    @Basic
    @Column(name = "PAYMENT_DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getPaymentDecisionDate() {
	return paymentDecisionDate;
    }

    public void setPaymentDecisionDate(Date paymentDecisionDate) {
	this.paymentDecisionDate = paymentDecisionDate;
	this.paymentDecisionDateString = HijriDateService.getHijriDateString(paymentDecisionDate);
	this.missionDetail.setPaymentDecisionDate(paymentDecisionDate);
    }

    @Transient
    @XmlTransient
    public String getPaymentDecisionDateString() {
	return paymentDecisionDateString;
    }

    public void setPaymentDecisionDateString(String paymentDecisionDateString) {
	this.paymentDecisionDateString = paymentDecisionDateString;
	this.setPaymentDecisionDate(HijriDateService.getHijriDate(paymentDecisionDateString));
    }

    @Basic
    @Column(name = "ACTUAL_DATA_SAVED_FLAG")
    @XmlTransient
    public Integer getActualDataSavedFlag() {
	return actualDataSavedFlag;
    }

    public void setActualDataSavedFlag(Integer actualDataSavedFlag) {
	this.actualDataSavedFlag = actualDataSavedFlag;
	this.missionDetail.setActualDataSavedFlag(actualDataSavedFlag);
	if (this.actualDataSavedFlag == null || this.actualDataSavedFlag == FlagsEnum.OFF.getCode()) {
	    this.actualDataSavedFlagBoolean = false;
	} else {
	    this.actualDataSavedFlagBoolean = true;
	}

    }

    @Transient
    @XmlTransient
    public Boolean getActualDataSavedFlagBoolean() {
	return actualDataSavedFlagBoolean;
    }

    public void setActualDataSavedFlagBoolean(Boolean savedFlagBoolean) {
	this.actualDataSavedFlagBoolean = savedFlagBoolean;
	if (this.actualDataSavedFlagBoolean == false) {
	    this.actualDataSavedFlag = FlagsEnum.OFF.getCode();
	} else {
	    this.actualDataSavedFlag = FlagsEnum.ON.getCode();
	}
    }
}
