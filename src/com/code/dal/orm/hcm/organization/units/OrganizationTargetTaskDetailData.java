package com.code.dal.orm.hcm.organization.units;

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

/**
 * The <code>TargetTaskDetailData</code> class represents the targets and tasks details of a unit in detailed format.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_organiztionTargetsTasksDetails_countActiveTargetTaskDetailsByUnitId",
		query = " select count(t.id) from OrganizationTargetTaskDetailData t " +
			" where t.unitId = :P_UNIT_ID " +
			" and t.activeFlag = 1 "
	),
	@NamedQuery(name = "hcm_organiztionTargetsTasksDetails_getActiveTargetTaskDetailsByUnitId",
		query = " select t from OrganizationTargetTaskDetailData t " +
			" where t.unitId = :P_UNIT_ID " +
			" and t.activeFlag = 1 "
	),
	@NamedQuery(name = "hcm_organiztionTargetsTasksDetails_getIdenticalTargetsTasksDetails",
		query = " select t from OrganizationTargetTaskDetailData t " +
			" where t.activeFlag = 1 " +
			" and t.organizationTargetTaskId in " +
			" ( " +
			" select rt.organizationTargetTaskId " +
			" from OrganizationTargetTaskDetailData st, OrganizationTargetTaskDetailData rt " +
			" where st.unitId =:P_UNIT_ID " +
			" and st.activeFlag = 1 " +
			" and rt.activeFlag = 1 " +
			" and rt.decisionNumber = st.decisionNumber " +
			" and rt.decisionDate = st.decisionDate " +
			" and rt.description= st.description " +
			" and rt.organizationTargetTaskFlag = st.organizationTargetTaskFlag " +
			" and (select count(td.unitId) from OrganizationTargetTaskDetailData td where td.unitId = rt.unitId and td.activeFlag = 1) = :P_TARGETS_TASKS_COUNT " +
			" group by rt.organizationTargetTaskId " +
			" having count(rt.organizationTargetTaskId) = :P_TARGETS_TASKS_COUNT " +
			" ) " +
			" order by t.organizationTargetTaskId, t.organizationTargetTaskFlag, t.description "
	)
})
@Entity
@Table(name = "HCM_VW_ORG_TARGETS_TASKS_DTLS")
public class OrganizationTargetTaskDetailData extends BaseEntity {

    private Long id;
    private Long organizationTargetTaskId;
    private Long unitId;
    private Long unitTypeId;
    private String unitHKey;
    private String unitFullName;
    private Integer organizationTargetTaskFlag;
    private String description;
    private Integer activeFlag;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;

    private OrganizationTargetTaskDetail organizationTargetTaskDetail;

    public OrganizationTargetTaskDetailData() {
	organizationTargetTaskDetail = new OrganizationTargetTaskDetail();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.organizationTargetTaskDetail.setId(id);
    }

    @Basic
    @Column(name = "TARGET_TASK_ID")
    public Long getOrganizationTargetTaskId() {
	return organizationTargetTaskId;
    }

    public void setOrganizationTargetTaskId(Long organizationTargetTaskId) {
	this.organizationTargetTaskId = organizationTargetTaskId;
	this.organizationTargetTaskDetail.setOrganizationTargetTaskId(organizationTargetTaskId);
    }

    @Basic
    @Column(name = "TARGET_TASK_FLAG")
    public Integer getOrganizationTargetTaskFlag() {
	return organizationTargetTaskFlag;
    }

    public void setOrganizationTargetTaskFlag(Integer organizationTargetTaskFlag) {
	this.organizationTargetTaskFlag = organizationTargetTaskFlag;
	this.organizationTargetTaskDetail.setOrganizationTargetTaskFlag(organizationTargetTaskFlag);
    }

    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
	this.organizationTargetTaskDetail.setDescription(description);
    }

    @Basic
    @Column(name = "ACTIVE_FLAG")
    public Integer getActiveFlag() {
	return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
	this.activeFlag = activeFlag;
	this.organizationTargetTaskDetail.setActiveFlag(activeFlag);
    }

    @Basic
    @Column(name = "UNIT_TYPE_ID")
    public Long getUnitTypeId() {
	return unitTypeId;
    }

    public void setUnitTypeId(Long unitTypeId) {
	this.unitTypeId = unitTypeId;
    }

    @Basic
    @Column(name = "UNIT_ID")
    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
    }

    @Basic
    @Column(name = "UNIT_HKEY")
    public String getUnitHKey() {
	return unitHKey;
    }

    public void setUnitHKey(String unitHKey) {
	this.unitHKey = unitHKey;
    }

    @Basic
    @Column(name = "UNIT_FULL_NAME")
    public String getUnitFullName() {
	return unitFullName;
    }

    public void setUnitFullName(String unitFullName) {
	this.unitFullName = unitFullName;
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

    @Transient
    public OrganizationTargetTaskDetail getOrganizationTargetTaskDetail() {
	return organizationTargetTaskDetail;
    }

    public void setOrganizationTargetTaskDetail(OrganizationTargetTaskDetail organizationTargetTaskDetail) {
	this.organizationTargetTaskDetail = organizationTargetTaskDetail;
    }

}