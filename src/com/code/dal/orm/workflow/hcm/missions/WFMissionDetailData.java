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
 * Provides all information regarding mission detail workflow view which is the view of {@link WFMissionDetail} with additional data such as String-formatted dates to be readable for the users. <br/>
 * There is one or more {@link WFMissionDetailData} for every {@link WFMissionData} linked by missionId
 */
@NamedQueries({
	@NamedQuery(name = "wf_missionDetails_getWFMissionDetailsByInstanceId",
		query = " select md from WFMissionDetailData md " +
			" where md.instanceId in (:P_INSTANCE_ID) " +
			" order by md.empMilitaryNumber, md.empRankId, md.empRecruitmentDate, md.empName")

})
@SuppressWarnings("serial")
@Entity
@Table(name = "ETR_VW_WF_MISSIONS_DETAILS")
public class WFMissionDetailData extends BaseEntity {

    private Long instanceId;
    private Long empId;
    private String empName;
    private Integer empMilitaryNumber;
    private String empUnitDesc;
    private String empPhysicalUnitHKey;
    private Long empPhysicalRegionId;
    private String empJobCode;
    private String empJobDesc;
    private Long empRankId;
    private String empRankDesc;
    private String empRankTitleDesc;
    private Date empRecruitmentDate;
    private Integer balance;
    private Integer period;
    private Integer roadPeriod;
    private Date startDate;
    private String startDateString;
    private Date endDate;
    private String endDateString;
    private String exceptionalApprovalNumber;
    private Date exceptionalApprovalDate;
    private String exceptionalApprovalDateString;
    private String roadLine;
    private String remarks;

    private WFMissionDetail wfMissionDetail;

    public WFMissionDetailData() {
	wfMissionDetail = new WFMissionDetail();
    }

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
	wfMissionDetail.setInstanceId(instanceId);
    }

    @Id
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	wfMissionDetail.setEmpId(empId);
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
    @Column(name = "EMP_MILITARY_NUMBER")
    @XmlTransient
    public Integer getEmpMilitaryNumber() {
	return empMilitaryNumber;
    }

    public void setEmpMilitaryNumber(Integer empMilitaryNumber) {
	this.empMilitaryNumber = empMilitaryNumber;
    }

    @Basic
    @Column(name = "EMP_UNIT_DESC")
    public String getEmpUnitDesc() {
	return empUnitDesc;
    }

    public void setEmpUnitDesc(String empUnitDesc) {
	this.empUnitDesc = empUnitDesc;
    }

    @Basic
    @Column(name = "EMP_PHYSICAL_UNIT_HKEY")
    @XmlTransient
    public String getEmpPhysicalUnitHKey() {
	return empPhysicalUnitHKey;
    }

    public void setEmpPhysicalUnitHKey(String empPhysicalUnitHKey) {
	this.empPhysicalUnitHKey = empPhysicalUnitHKey;
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
    @Column(name = "EMP_JOB_CODE")
    @XmlTransient
    public String getEmpJobCode() {
	return empJobCode;
    }

    public void setEmpJobCode(String empJobCode) {
	this.empJobCode = empJobCode;
    }

    @Basic
    @Column(name = "EMP_JOB_DESC")
    public String getEmpJobDesc() {
	return empJobDesc;
    }

    public void setEmpJobDesc(String empJobDesc) {
	this.empJobDesc = empJobDesc;
    }

    @Basic
    @Column(name = "EMP_RANK_ID")
    public Long getEmpRankId() {
	return empRankId;
    }

    public void setEmpRankId(Long empRankId) {
	this.empRankId = empRankId;
    }

    @Basic
    @Column(name = "EMP_RANK_DESC")
    public String getEmpRankDesc() {
	return empRankDesc;
    }

    public void setEmpRankDesc(String empRankDesc) {
	this.empRankDesc = empRankDesc;
    }

    @Basic
    @Column(name = "EMP_RANK_TITLE_DESC")
    @XmlTransient
    public String getEmpRankTitleDesc() {
	return empRankTitleDesc;
    }

    public void setEmpRankTitleDesc(String empRankTitleDesc) {
	this.empRankTitleDesc = empRankTitleDesc;
    }

    @Basic
    @Column(name = "EMP_RECRUITMENT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getEmpRecruitmentDate() {
	return empRecruitmentDate;
    }

    public void setEmpRecruitmentDate(Date empRecruitmentDate) {
	this.empRecruitmentDate = empRecruitmentDate;
    }

    @Basic
    @Column(name = "BALANCE")
    public Integer getBalance() {
	return balance;
    }

    public void setBalance(Integer balance) {
	this.balance = balance;
	wfMissionDetail.setBalance(balance);
    }

    @Basic
    @Column(name = "PERIOD")
    public Integer getPeriod() {
	return period;
    }

    public void setPeriod(Integer period) {
	this.period = period;
	wfMissionDetail.setPeriod(period);
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
	wfMissionDetail.setRoadPeriod(roadPeriod);
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
	wfMissionDetail.setStartDate(startDate);
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
	wfMissionDetail.setEndDate(endDate);
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
    @Column(name = "EXCEPTIONAL_APPROVAL_NUMBER")
    @XmlElement(nillable = true)
    public String getExceptionalApprovalNumber() {
	return exceptionalApprovalNumber;
    }

    public void setExceptionalApprovalNumber(String exceptionalApprovalNumber) {
	this.exceptionalApprovalNumber = exceptionalApprovalNumber;
	wfMissionDetail.setExceptionalApprovalNumber(exceptionalApprovalNumber);
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
	wfMissionDetail.setExceptionalApprovalDate(exceptionalApprovalDate);
    }

    @Transient
    @XmlElement(nillable = true)
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
	wfMissionDetail.setRoadLine(roadLine);
    }

    @Basic
    @Column(name = "REMARKS")
    @XmlTransient
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
	wfMissionDetail.setRemarks(remarks);
    }

    @Transient
    @XmlTransient
    public WFMissionDetail getWfMissionDetail() {
	return wfMissionDetail;
    }

    public void setWfMissionDetail(WFMissionDetail wfMissionDetail) {
	this.wfMissionDetail = wfMissionDetail;
    }

}