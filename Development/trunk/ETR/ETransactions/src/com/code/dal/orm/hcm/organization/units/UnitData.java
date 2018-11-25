package com.code.dal.orm.hcm.organization.units;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import com.code.dal.orm.BaseEntity;
import com.code.enums.FlagsEnum;

/**
 * The <code>UnitData</code> class represents the organization hierarchy unit data in detailed format.
 * 
 */
@SuppressWarnings("serial")
@NamedQueries({
	@NamedQuery(name = "hcm_unitData_getUnitData",
		query = " select u from UnitData u " +
			" where (:P_UNIT_ID = -1 or u.id = :P_UNIT_ID) " +
			" and (:P_UNIT_NAME = '-1' or u.name like :P_UNIT_NAME)" +
			" and (:P_UNIT_FULL_NAME = '-1' or u.fullName like :P_UNIT_FULL_NAME)" +
			" and (:P_UNIT_TYPE_ID = -1 or u.unitTypeId = :P_UNIT_TYPE_ID)" +
			" and (:P_PARENT_ID = -1 or u.parentUnitId = :P_PARENT_ID)" +
			" and (:P_REGION_ID = -1 or u.regionId = :P_REGION_ID)" +
			// " and (:P_CODE = '-1' or u.code = :P_CODE)" +
			" and (:P_ACTIVE_FLAG = -1 or u.activeFlag = :P_ACTIVE_FLAG)" +
			" order by u.id"),

	@NamedQuery(name = "hcm_unitData_getUnitDataByFullName",
		query = " select u from UnitData u " +
			" where (:P_FULL_NAME = '-1' or u.fullName = :P_FULL_NAME)" +
			" and u.activeFlag = 1 " +
			" order by u.id"),

	@NamedQuery(name = "hcm_unitData_getUnitDataParentChildren",
		query = " select distinct u.id from UnitData u, UnitData up " +
			" where u.id <> :P_UNIT_ID " +
			" and u.parentUnitId = :P_UNIT_ID " +
			" and u.id = up.parentUnitId " +
			" and u.activeFlag = 1 and up.activeFlag = 1 " +
			" order by u.id "),

	@NamedQuery(name = "hcm_unitData_getUnitDataChildren",
		query = " select u from UnitData u " +
			" where u.id <> :P_PARENT_UNIT_ID " +
			" and u.parentUnitId = :P_PARENT_UNIT_ID " +
			" and (:P_ACTIVE_FLAG = -1 or u.activeFlag = :P_ACTIVE_FLAG)" +
			" order by u.orderUnderParent "),

	@NamedQuery(name = "hcm_unitData_getUnitDataByHKey",
		query = " select u from UnitData u " +
			" where u.hKey in (:P_HKEY_LIST) " +
			" and u.activeFlag = 1 " +
			" order by u.parentHKey, u.orderUnderParent, u.hKey"),

	@NamedQuery(name = "hcm_unitData_getAllLevelChildrenByHKey",
		query = " select u from UnitData u " +
			" where u.hKey like :P_PREFIX_HKEY " +
			" and u.activeFlag = 1 " +
			" and (:P_UNIT_FULL_NAME = '-1' or u.fullName like :P_UNIT_FULL_NAME) " +
			" order by u.parentHKey, u.orderUnderParent, u.hKey"),

	@NamedQuery(name = "hcm_unitData_getUnitDataByChildType",
		query = " select u from UnitData u, UnitType ut " +
			" where u.unitTypeId = ut.id " +
			" and (:P_UNIT_FULL_NAME = '-1' or u.fullName like :P_UNIT_FULL_NAME) " +
			" and (:P_REGION_ID = -1 or u.regionId = :P_REGION_ID)" +
			" and ut.childrenUnitTypeCodes like :P_CHILD_TYPE_CODE" +
			" and u.activeFlag = 1 " +
			" order by u.id"),

	@NamedQuery(name = "hcm_unitData_getMaxOrderUnderParent",
		query = " select max(u.orderUnderParent) from UnitData u " +
			" where u.id <> :P_PARENT_UNIT_ID " +
			" and u.parentUnitId = :P_PARENT_UNIT_ID"),

	@NamedQuery(name = "hcm_unitData_getUnitsByTypeAndName",
		query = " select u from UnitData u " +
			" where u.unitTypeId in (:P_TYPE_IDS) " +
			" and (:P_UNIT_FULL_NAME = '-1' or u.fullName like :P_UNIT_FULL_NAME) " +
			" and u.activeFlag = 1 " +
			" order by u.id"),

	@NamedQuery(name = "hcm_unitData_getUnitsByPhysicalManager",
		query = " select u from UnitData u " +
			" where u.physicalManagerId = :P_PHYSICAL_MANAGER_ID " +
			" and u.activeFlag = 1 " +
			" order by u.hKey"),

	@NamedQuery(name = "hcm_unitData_getTrainingCentersAndInstitutesByName",
		query = " select u from UnitData u " +
			" where (u.unitTypeId in (:P_TYPE_IDS) or (u.unitTypeId = :P_ACADEMY_TYPE_ID and u.regionId = :P_ACADEMY_REGION_ID)) " +
			" and (:P_UNIT_FULL_NAME = '-1' or u.fullName like :P_UNIT_FULL_NAME) " +
			" and u.activeFlag = 1  " +
			" order by u.id"),

	@NamedQuery(name = "hcm_unitData_getAncestorUnderPresidencyByLevel",
		query = " select au from UnitData au " +
			" where au.hKey = (select substring(u.hKey, 1, :P_END_INDEX) from UnitData u where u.id = :P_UNIT_ID) || :P_ZEROS_SUFFIX " +
			" and au.activeFlag = 1 " +
			" order by au.id"),

	@NamedQuery(name = "hcm_unitData_searchUnitsByUnitsIds",
		query = " select u from UnitData u " +
			" where u.id in (:P_UNITS_IDS) " +
			" order by u.id "),

	@NamedQuery(name = "hcm_unitData_searchUnitsByDisclaimerDetailsInstanceId",
		query = " select u from UnitData u , WFDisclaimerDetail dd "
			+ " where :P_INSTANCE_ID = dd.instanceId "
			+ " and u.fullName = dd.managerUnitFullName "
			+ " order by u.fullName")
})
@Entity
@Table(name = "HCM_VW_ORG_UNITS")
public class UnitData extends BaseEntity implements Serializable {
    private Long id;
    // private String code;
    private Long unitTypeId;
    private Integer unitTypeCode;
    private String unitTypeDesc;
    private String childrenUnitTypesCodes;
    private String name;
    private String fullName;
    private Long parentUnitId;
    private String parentUnitName;
    private String parentHKey;
    private String hKey;
    private Long regionId;
    private String regionCode;
    private String regionDesc;
    private Integer targetsArchivedFlag;
    private Integer dutiesArchivedFlag;
    private Integer jobsArchivedFlag;
    private Integer organizationalArchivedFlag;
    private Integer equipmentsArchivedFlag;
    private String remarks;
    private Long officialManagerId;
    private String officialManagerName;
    private Long physicalManagerId;
    private String physicalManagerName;
    private Integer orderUnderParent;
    private Integer activeFlag;
    private String attachments;

    private Long newUnitTypeId;
    private Long moveToUnitId;
    private String moveToUnitFullName;
    private Long mergeWithUnitId;
    private String newName;
    private boolean cancelUnit;
    private Integer transactionRequiredFlag;
    private Boolean transactionRequiredFlagBoolean;

    private Unit unit;

    public UnitData() {
	unit = new Unit();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	unit.setId(id);
	this.id = id;
    }

    // @Basic
    // @Column(name = "CODE")
    // public String getCode() {
    // return code;
    // }
    //
    // public void setCode(String code) {
    // this.code = code;
    // this.unit.setCode(code);
    // }

    @Basic
    @Column(name = "UNIT_TYPE_DESC")
    public String getUnitTypeDesc() {
	return unitTypeDesc;
    }

    public void setUnitTypeDesc(String unitTypeDesc) {
	this.unitTypeDesc = unitTypeDesc;
    }

    @Basic
    @Column(name = "UNIT_TYPE_ID")
    public Long getUnitTypeId() {
	return unitTypeId;
    }

    public void setUnitTypeId(Long unitTypeId) {
	this.unitTypeId = unitTypeId;
	this.unit.setUnitTypeId(unitTypeId);
	this.newUnitTypeId = unitTypeId;
    }

    @Basic
    @Column(name = "CHILDREN_UNIT_TYPES_CODES")
    @XmlTransient
    public String getChildrenUnitTypesCodes() {
	return childrenUnitTypesCodes;
    }

    public void setChildrenUnitTypesCodes(String childrenUnitTypesCodes) {
	this.childrenUnitTypesCodes = childrenUnitTypesCodes;
    }

    @Basic
    @Column(name = "UNIT_TYPE_CODE")
    @XmlTransient
    public Integer getUnitTypeCode() {
	return unitTypeCode;
    }

    public void setUnitTypeCode(Integer unitTypeCode) {
	this.unitTypeCode = unitTypeCode;
    }

    @Basic
    @Column(name = "NAME")
    @XmlTransient
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
	this.unit.setName(name);
	this.newName = name;
    }

    @Basic
    @Column(name = "FULL_NAME")
    public String getFullName() {
	return fullName;
    }

    public void setFullName(String fullName) {
	this.fullName = fullName;
	this.unit.setFullName(fullName);
    }

    @Basic
    @Column(name = "PARENT_UNIT_ID")
    @XmlTransient
    public Long getParentUnitId() {
	return parentUnitId;
    }

    public void setParentUnitId(Long parentUnitId) {
	this.parentUnitId = parentUnitId;
	this.unit.setParentUnitId(parentUnitId);
    }

    @Basic
    @Column(name = "PARENT_UNIT_NAME")
    @XmlTransient
    public String getParentUnitName() {
	return parentUnitName;
    }

    public void setParentUnitName(String parentUnitName) {
	this.parentUnitName = parentUnitName;
    }

    @Basic
    @Column(name = "HKEY")
    @XmlTransient
    public String gethKey() {
	return hKey;
    }

    public void sethKey(String hKey) {
	this.hKey = hKey;
	this.unit.sethKey(hKey);
    }

    @Basic
    @Column(name = "REGION_ID")
    @XmlTransient
    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
	this.unit.setRegionId(regionId);
    }

    @Basic
    @Column(name = "REGION_CODE")
    @XmlTransient
    public String getRegionCode() {
	return regionCode;
    }

    public void setRegionCode(String regionCode) {
	this.regionCode = regionCode;
    }

    @Basic
    @Column(name = "REGION_DESCRIPTION")
    @XmlTransient
    public String getRegionDesc() {
	return regionDesc;
    }

    public void setRegionDesc(String regionDesc) {
	this.regionDesc = regionDesc;
    }

    @Basic
    @Column(name = "TARGETS_ARCHIVED_FLAG")
    @XmlTransient
    public Integer getTargetsArchivedFlag() {
	return targetsArchivedFlag;
    }

    public void setTargetsArchivedFlag(Integer targetsArchivedFlag) {
	this.targetsArchivedFlag = targetsArchivedFlag;
	this.unit.setTargetsArchivedFlag(targetsArchivedFlag);
    }

    @Basic
    @Column(name = "DUTIES_ARCHIVED_FLAG")
    @XmlTransient
    public Integer getDutiesArchivedFlag() {
	return dutiesArchivedFlag;
    }

    public void setDutiesArchivedFlag(Integer dutiesArchivedFlag) {
	this.dutiesArchivedFlag = dutiesArchivedFlag;
	this.unit.setDutiesArchivedFlag(dutiesArchivedFlag);
    }

    @Basic
    @Column(name = "JOBS_ARCHIVED_FLAG")
    @XmlTransient
    public Integer getJobsArchivedFlag() {
	return jobsArchivedFlag;
    }

    public void setJobsArchivedFlag(Integer jobsArchivedFlag) {
	this.jobsArchivedFlag = jobsArchivedFlag;
	this.unit.setJobsArchivedFlag(jobsArchivedFlag);
    }

    @Basic
    @Column(name = "ORGANIZATIONAL_ARCHIVED_FLAG")
    @XmlTransient
    public Integer getOrganizationalArchivedFlag() {
	return organizationalArchivedFlag;
    }

    public void setOrganizationalArchivedFlag(Integer organizationalArchivedFlag) {
	this.organizationalArchivedFlag = organizationalArchivedFlag;
	this.unit.setOrganizationalArchivedFlag(organizationalArchivedFlag);
    }

    @Basic
    @Column(name = "EQUIPMENTS_ARCHIVED_FLAG")
    @XmlTransient
    public Integer getEquipmentsArchivedFlag() {
	return equipmentsArchivedFlag;
    }

    public void setEquipmentsArchivedFlag(Integer equipmentsArchivedFlag) {
	this.equipmentsArchivedFlag = equipmentsArchivedFlag;
	this.unit.setEquipmentsArchivedFlag(equipmentsArchivedFlag);
    }

    @Basic
    @Column(name = "REMARKS")
    @XmlTransient
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
	this.unit.setRemarks(remarks);
    }

    @Basic
    @Column(name = "OFFICIAL_MANAGER_ID")
    @XmlTransient
    public Long getOfficialManagerId() {
	return officialManagerId;
    }

    public void setOfficialManagerId(Long officialManagerId) {
	this.officialManagerId = officialManagerId;
	this.unit.setOfficialManagerId(officialManagerId);
    }

    @Basic
    @Column(name = "OFFICIAL_MANAGER_NAME")
    @XmlTransient
    public String getOfficialManagerName() {
	return officialManagerName;
    }

    public void setOfficialManagerName(String officialManagerName) {
	this.officialManagerName = officialManagerName;
    }

    @Basic
    @Column(name = "PHYSICAL_MANAGER_ID")
    @XmlTransient
    public Long getPhysicalManagerId() {
	return physicalManagerId;
    }

    public void setPhysicalManagerId(Long physicalManagerId) {
	this.physicalManagerId = physicalManagerId;
	this.unit.setPhysicalManagerId(physicalManagerId);
    }

    @Basic
    @Column(name = "PHYSICAL_MANAGER_NAME")
    @XmlTransient
    public String getPhysicalManagerName() {
	return physicalManagerName;
    }

    public void setPhysicalManagerName(String physicalManagerName) {
	this.physicalManagerName = physicalManagerName;
    }

    @Basic
    @Column(name = "ORDER_UNDER_PARENT")
    @XmlTransient
    public Integer getOrderUnderParent() {
	return orderUnderParent;
    }

    public void setOrderUnderParent(Integer orderUnderParent) {
	this.orderUnderParent = orderUnderParent;
	this.unit.setOrderUnderParent(orderUnderParent);
    }

    @Basic
    @Column(name = "ACTIVE_FLAG")
    @XmlTransient
    public Integer getActiveFlag() {
	return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
	this.activeFlag = activeFlag;
	this.unit.setActiveFlag(activeFlag);
    }

    @Transient
    @XmlTransient
    public Unit getUnit() {
	return unit;
    }

    public void setUnit(Unit unit) {
	this.unit = unit;
    }

    @Basic
    @Column(name = "PARENT_HKEY")
    @XmlTransient
    public String getParentHKey() {
	return parentHKey;
    }

    public void setParentHKey(String parentHKey) {
	this.parentHKey = parentHKey;
    }

    @Transient
    @XmlTransient
    public String getNewName() {
	return newName;
    }

    public void setNewName(String newName) {
	this.newName = newName;
    }

    @Transient
    @XmlTransient
    public Long getMoveToUnitId() {
	return moveToUnitId;
    }

    public void setMoveToUnitId(Long moveToUnitId) {
	this.moveToUnitId = moveToUnitId;
    }

    @Transient
    @XmlTransient
    public Long getMergeWithUnitId() {
	return mergeWithUnitId;
    }

    public void setMergeWithUnitId(Long mergeWithUnitId) {
	this.mergeWithUnitId = mergeWithUnitId;
    }

    @Transient
    @XmlTransient
    public boolean isCancelUnit() {
	return cancelUnit;
    }

    public void setCancelUnit(boolean cancelUnit) {
	this.cancelUnit = cancelUnit;
    }

    @Transient
    @XmlTransient
    public Long getNewUnitTypeId() {
	return newUnitTypeId;
    }

    public void setNewUnitTypeId(Long newUnitTypeId) {
	this.newUnitTypeId = newUnitTypeId;
    }

    @Transient
    @XmlTransient
    public String getMoveToUnitFullName() {
	return moveToUnitFullName;
    }

    public void setMoveToUnitFullName(String moveToUnitFullName) {
	this.moveToUnitFullName = moveToUnitFullName;
    }

    @Basic
    @Column(name = "TRANSACTION_REQUIRED_FLAG")
    @XmlTransient
    public Integer getTransactionRequiredFlag() {
	return transactionRequiredFlag;
    }

    public void setTransactionRequiredFlag(Integer transactionRequiredFlag) {
	this.transactionRequiredFlag = transactionRequiredFlag;
	this.unit.setTransactionRequiredFlag(this.transactionRequiredFlag);
	if (this.transactionRequiredFlag == null || this.transactionRequiredFlag == FlagsEnum.OFF.getCode())
	    this.transactionRequiredFlagBoolean = false;
	else
	    this.transactionRequiredFlagBoolean = true;
    }

    @Transient
    @XmlTransient
    public Boolean getTransactionRequiredFlagBoolean() {
	return transactionRequiredFlagBoolean;
    }

    public void setTransactionRequiredFlagBoolean(Boolean transactionRequiredFlagBoolean) {
	if (transactionRequiredFlagBoolean == null || !transactionRequiredFlagBoolean)
	    this.setTransactionRequiredFlag(FlagsEnum.OFF.getCode());
	else
	    this.setTransactionRequiredFlag(FlagsEnum.ON.getCode());
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    @XmlTransient
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
	this.unit.setAttachments(attachments);
    }
}
