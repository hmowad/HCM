package com.code.dal.orm.workflow.hcm.promotions;

import java.io.Serializable;
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

import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "hcm_wfPromotionNotificationData_searchWFPromotionNotificationData",
		query = " select d from WFPromotionNotificationData d " +
			" where (:P_INSTANCE_ID = -1 or d.instanceId = :P_INSTANCE_ID) " +
			" and (:P_REPORT_DETAIL_ID = -1 or d.reportDetailId = :P_REPORT_DETAIL_ID)" +
			" and (:P_EMP_ID = -1 or d.empId = :P_EMP_ID)" +
			" and (:P_UNIT_HKEY = '-1' or d.physicalUnitHkey like :P_UNIT_HKEY)" +
			" and (:P_REGION_ID = -1 or d.physicalRegionId = :P_REGION_ID)")
})
@Entity
@Table(name = "WF_VW_PROMOTION_NOTIFICATIONS")
public class WFPromotionNotificationData implements Serializable {

    private Long instanceId;
    private Long reportDetailId;
    private Long empId;
    private String empName;
    private Integer militaryNumber;
    private String rankDesc;
    private Date dueDate;
    private String dueDateString;
    private Long physicalUnitId;
    private String physicalUnitHkey;
    private String physicalUnitFullName;
    private Long officialUnitId;
    private String officialUnitFullName;
    private Long physicalRegionId;
    private String externalDecisionNumber;
    private Date externalDecisionDate;
    private String externalDecisionDateString;

    private WFPromotionNotification wfPromotionNotification;

    public WFPromotionNotificationData() {
	wfPromotionNotification = new WFPromotionNotification();
    }

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
	this.wfPromotionNotification.setInstanceId(instanceId);
    }

    @Id
    @Column(name = "REPORT_DETAIL_ID")
    public Long getReportDetailId() {
	return reportDetailId;
    }

    public void setReportDetailId(Long reportDetailId) {
	this.reportDetailId = reportDetailId;
	this.wfPromotionNotification.setReportDetailId(reportDetailId);
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
    @Column(name = "EMP_NAME")
    public String getEmpName() {
	return empName;
    }

    public void setEmpName(String empName) {
	this.empName = empName;
    }

    @Basic
    @Column(name = "MILITARY_NUMBER")
    public Integer getMilitaryNumber() {
	return militaryNumber;
    }

    public void setMilitaryNumber(Integer militaryNumber) {
	this.militaryNumber = militaryNumber;
    }

    @Basic
    @Column(name = "RANK_DESC")
    public String getRankDesc() {
	return rankDesc;
    }

    public void setRankDesc(String rankDesc) {
	this.rankDesc = rankDesc;
    }

    @Basic
    @Column(name = "PROMOTION_DUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getDueDate() {
	return dueDate;
    }

    public void setDueDate(Date dueDate) {
	this.dueDate = dueDate;
	this.dueDateString = HijriDateService.getHijriDateString(dueDate);
    }

    @Transient
    @XmlTransient
    public String getDueDateString() {
	return dueDateString;
    }

    public void setDueDateString(String dueDateString) {
	this.dueDateString = dueDateString;
	this.dueDate = HijriDateService.getHijriDate(dueDateString);
    }

    @Basic
    @Column(name = "PHYSICAL_UNIT_ID")
    public Long getPhysicalUnitId() {
	return physicalUnitId;
    }

    public void setPhysicalUnitId(Long physicalUnitId) {
	this.physicalUnitId = physicalUnitId;
    }

    @Basic
    @Column(name = "PHYSICAL_UNIT_HKEY")
    public String getPhysicalUnitHkey() {
	return physicalUnitHkey;
    }

    public void setPhysicalUnitHkey(String physicalUnitHkey) {
	this.physicalUnitHkey = physicalUnitHkey;
    }

    @Basic
    @Column(name = "PHYSICAL_UNIT_FULL_NAME")
    public String getPhysicalUnitFullName() {
	return physicalUnitFullName;
    }

    public void setPhysicalUnitFullName(String physicalUnitFullName) {
	this.physicalUnitFullName = physicalUnitFullName;
    }

    @Basic
    @Column(name = "OFFICIAL_UNIT_ID")
    public Long getOfficialUnitId() {
	return officialUnitId;
    }

    public void setOfficialUnitId(Long officialUnitId) {
	this.officialUnitId = officialUnitId;
    }

    @Basic
    @Column(name = "OFFICIAL_UNIT_FULL_NAME")
    public String getOfficialUnitFullName() {
	return officialUnitFullName;
    }

    public void setOfficialUnitFullName(String officialUnitFullName) {
	this.officialUnitFullName = officialUnitFullName;
    }

    @Basic
    @Column(name = "PHYSICAL_REGION_ID")
    public Long getPhysicalRegionId() {
	return physicalRegionId;
    }

    public void setPhysicalRegionId(Long physicalRegionId) {
	this.physicalRegionId = physicalRegionId;
    }

    @Basic
    @Column(name = "EXTERNAL_DECISION_NUMBER")
    public String getExternalDecisionNumber() {
	return externalDecisionNumber;
    }

    public void setExternalDecisionNumber(String externalDecisionNumber) {
	this.externalDecisionNumber = externalDecisionNumber;
    }

    @Basic
    @Column(name = "EXTERNAL_DECISION_DATE")
    public Date getExternalDecisionDate() {
	return externalDecisionDate;
    }

    public void setExternalDecisionDate(Date externalDecisionDate) {
	this.externalDecisionDate = externalDecisionDate;
	this.externalDecisionDateString = HijriDateService.getHijriDateString(externalDecisionDate);
    }

    @Transient
    @XmlTransient
    public String getExternalDecisionDateString() {
	return externalDecisionDateString;
    }

    public void setExternalDecisionDateString(String externalDecisionDateString) {
	this.externalDecisionDateString = externalDecisionDateString;
	this.externalDecisionDate = HijriDateService.getHijriDate(externalDecisionDateString);
    }

    @Transient
    @XmlTransient
    public WFPromotionNotification getWfPromotionNotification() {
	return wfPromotionNotification;
    }

    public void setWfPromotionNotification(WFPromotionNotification wfPromotionNotification) {
	this.wfPromotionNotification = wfPromotionNotification;
    }

}
