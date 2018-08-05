package com.code.dal.orm.hcm.organization.units;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

/**
 * The <code>Unit</code> class represents the organization hierarchy unit data.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_unit_countUnitChildrenExistInUnits",
		query = " select count(u.id) from Unit u" +
			" where u.hKey like :P_PREFIX_HKEY " +
			" and u.id in (:P_UNITS_IDS) " +
			" and u.activeFlag = 1 ")
})
@Entity
@Table(name = "HCM_ORG_UNITS")
public class Unit extends AuditEntity implements UpdatableAuditEntity {
    private Long id;
    private Long physicalManagerId;
    private Long parentUnitId;
    private Long regionId;
    private Long officialManagerId;
    private Long unitTypeId;
    private Integer activeFlag;
    private Integer orderUnderParent;
    private String hKey;
    private String name;
    private String fullName;
    private String remarks;
    private Integer targetsArchivedFlag;
    private Integer dutiesArchivedFlag;
    private Integer jobsArchivedFlag;
    private Integer organizationalArchivedFlag;
    private Integer equipmentsArchivedFlag;
    private Integer transactionRequiredFlag;
    // private String code;
    private String attachments;

    @SequenceGenerator(name = "HCMUnitSeq",
	    sequenceName = "HCM_ORG_UNITS_SEQ")
    @Id
    @GeneratedValue(generator = "HCMUnitSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "PHYSICAL_MANAGER_ID")
    public Long getPhysicalManagerId() {
	return physicalManagerId;
    }

    public void setPhysicalManagerId(Long physicalManagerId) {
	this.physicalManagerId = physicalManagerId;
    }

    @Basic
    @Column(name = "PARENT_UNIT_ID")
    public Long getParentUnitId() {
	return parentUnitId;
    }

    public void setParentUnitId(Long parentUnitId) {
	this.parentUnitId = parentUnitId;
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
    @Column(name = "FULL_NAME")
    public String getFullName() {
	return fullName;
    }

    public void setFullName(String fullName) {
	this.fullName = fullName;
    }

    @Basic
    @Column(name = "REGION_ID")
    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
    }

    @Basic
    @Column(name = "OFFICIAL_MANAGER_ID")
    public Long getOfficialManagerId() {
	return officialManagerId;
    }

    public void setOfficialManagerId(Long officialManagerId) {
	this.officialManagerId = officialManagerId;
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
    @Column(name = "ORDER_UNDER_PARENT")
    public Integer getOrderUnderParent() {
	return orderUnderParent;
    }

    public void setOrderUnderParent(Integer orderUnderParent) {
	this.orderUnderParent = orderUnderParent;
    }

    @Basic
    @Column(name = "HKEY")
    public String gethKey() {
	return hKey;
    }

    public void sethKey(String hkey) {
	this.hKey = hkey;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
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
    @Column(name = "TARGETS_ARCHIVED_FLAG")
    public Integer getTargetsArchivedFlag() {
	return targetsArchivedFlag;
    }

    public void setTargetsArchivedFlag(Integer targetsArchivedFlag) {
	this.targetsArchivedFlag = targetsArchivedFlag;
    }

    @Basic
    @Column(name = "DUTIES_ARCHIVED_FLAG")
    public Integer getDutiesArchivedFlag() {
	return dutiesArchivedFlag;
    }

    public void setDutiesArchivedFlag(Integer dutiesArchivedFlag) {
	this.dutiesArchivedFlag = dutiesArchivedFlag;
    }

    @Basic
    @Column(name = "JOBS_ARCHIVED_FLAG")
    public Integer getJobsArchivedFlag() {
	return jobsArchivedFlag;
    }

    public void setJobsArchivedFlag(Integer jobsArchivedFlag) {
	this.jobsArchivedFlag = jobsArchivedFlag;
    }

    @Basic
    @Column(name = "ORGANIZATIONAL_ARCHIVED_FLAG")
    public Integer getOrganizationalArchivedFlag() {
	return organizationalArchivedFlag;
    }

    public void setOrganizationalArchivedFlag(Integer organizationalArchivedFlag) {
	this.organizationalArchivedFlag = organizationalArchivedFlag;
    }

    @Basic
    @Column(name = "EQUIPMENTS_ARCHIVED_FLAG")
    public Integer getEquipmentsArchivedFlag() {
	return equipmentsArchivedFlag;
    }

    public void setEquipmentsArchivedFlag(Integer equipmentsArchivedFlag) {
	this.equipmentsArchivedFlag = equipmentsArchivedFlag;
    }

    @Basic
    @Column(name = "TRANSACTION_REQUIRED_FLAG")
    public Integer getTransactionRequiredFlag() {
	return transactionRequiredFlag;
    }

    public void setTransactionRequiredFlag(Integer transactionRequiredFlag) {
	this.transactionRequiredFlag = transactionRequiredFlag;
    }

    // @Basic
    // @Column(name = "CODE")
    // public String getCode() {
    // return code;
    // }
    //
    // public void setCode(String code) {
    // this.code = code;
    // }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "name:" + name + AUDIT_SEPARATOR +
		"fullName:" + fullName + AUDIT_SEPARATOR +
		"parentUnitId:" + parentUnitId + AUDIT_SEPARATOR +
		"regionId:" + regionId + AUDIT_SEPARATOR +
		"unitTypeId:" + unitTypeId + AUDIT_SEPARATOR +
		"hKey:" + hKey + AUDIT_SEPARATOR +
		"orderUnderParent:" + orderUnderParent + AUDIT_SEPARATOR +
		"physicalManagerId:" + physicalManagerId + AUDIT_SEPARATOR +
		"officialManagerId:" + officialManagerId + AUDIT_SEPARATOR +
		"activeFlag:" + activeFlag + AUDIT_SEPARATOR +
		"targetsArchivedFlag:" + targetsArchivedFlag + AUDIT_SEPARATOR +
		"dutiesArchivedFlag:" + dutiesArchivedFlag + AUDIT_SEPARATOR +
		"jobsArchivedFlag:" + jobsArchivedFlag + AUDIT_SEPARATOR +
		"organizationalArchivedFlag:" + organizationalArchivedFlag + AUDIT_SEPARATOR +
		"equipmentsArchivedFlag:" + equipmentsArchivedFlag + AUDIT_SEPARATOR +
		"transactionRequiredFlag:" + transactionRequiredFlag + AUDIT_SEPARATOR +
		"remarks:" + remarks + AUDIT_SEPARATOR;
    }
}