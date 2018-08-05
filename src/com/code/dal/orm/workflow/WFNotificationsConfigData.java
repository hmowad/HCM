package com.code.dal.orm.workflow;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "wf_notificationsConfigData_searchWFNotificationsConfigData",
		query = "select n from WFNotificationsConfigData n " +
			" where (:P_PROCESS_GROUP_ID = -1 or n.wfProcessGroupId = :P_PROCESS_GROUP_ID) " +
			" and (:P_DESCRIPTION ='-1' or n.description like :P_DESCRIPTION) " +
			" and (:P_CATEGORY_ID = -1 or n.beneficiaryCategoryId = :P_CATEGORY_ID) ) " +
			" order by n.id desc")
})
@Entity
@Table(name = "ETR_VW_WF_NOTIFICATIONS_CONFIG")
public class WFNotificationsConfigData extends BaseEntity {

    private Long id;
    private String description;
    private Long decisionRegionId;
    private String decisionRegionDescription;
    private Long beneficiaryRegionId;
    private String beneficiaryRegionDescription;
    private Long beneficiaryUnitId;
    private String beneficiaryUnitHKeyPrefix;
    private String beneficiaryUnitFullName;
    private Long beneficiaryCategoryId;
    private String beneficiaryCategoryDescription;
    private Long wfProcessGroupId;
    private String wfProcessGroupName;
    private Long wfProcessId;
    private String wfProcessName;

    private WFNotificationsConfig wfNotificationConfig;

    public WFNotificationsConfigData() {
	wfNotificationConfig = new WFNotificationsConfig();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	wfNotificationConfig.setId(id);
    }

    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
	wfNotificationConfig.setDescription(description);
    }

    @Basic
    @Column(name = "DESICION_REGION_ID")
    public Long getDecisionRegionId() {
	return decisionRegionId;
    }

    public void setDecisionRegionId(Long decisionRegionId) {
	this.decisionRegionId = decisionRegionId;
	wfNotificationConfig.setDecisionRegionId(decisionRegionId);
    }

    @Basic
    @Column(name = "DESICION_REGION_DESC")
    public String getDecisionRegionDescription() {
	return decisionRegionDescription;
    }

    public void setDecisionRegionDescription(String decisionRegionDescription) {
	this.decisionRegionDescription = decisionRegionDescription;
    }

    @Basic
    @Column(name = "BENEFICIARY_REGION_ID")
    public Long getBeneficiaryRegionId() {
	return beneficiaryRegionId;
    }

    public void setBeneficiaryRegionId(Long beneficiaryRegionId) {
	this.beneficiaryRegionId = beneficiaryRegionId;
	wfNotificationConfig.setBeneficiaryRegionId(beneficiaryRegionId);
    }

    @Basic
    @Column(name = "BENEFICIARY_REGION_DESC")
    public String getBeneficiaryRegionDescription() {
	return beneficiaryRegionDescription;
    }

    public void setBeneficiaryRegionDescription(String beneficiaryRegionDescription) {
	this.beneficiaryRegionDescription = beneficiaryRegionDescription;
    }

    @Basic
    @Column(name = "BENEFICIARY_UNIT_ID")
    public Long getBeneficiaryUnitId() {
	return beneficiaryUnitId;
    }

    public void setBeneficiaryUnitId(Long beneficiaryUnitId) {
	this.beneficiaryUnitId = beneficiaryUnitId;
	wfNotificationConfig.setBeneficiaryUnitId(beneficiaryUnitId);
    }

    @Basic
    @Column(name = "BENEFICIARY_UNIT_HKEY_PREFIX")
    public String getBeneficiaryUnitHKeyPrefix() {
	return beneficiaryUnitHKeyPrefix;
    }

    public void setBeneficiaryUnitHKeyPrefix(String beneficiaryUnitHKeyPrefix) {
	this.beneficiaryUnitHKeyPrefix = beneficiaryUnitHKeyPrefix;
    }

    @Basic
    @Column(name = "BENEFICIARY_UNIT_FULL_NAME")
    public String getBeneficiaryUnitFullName() {
	return beneficiaryUnitFullName;
    }

    public void setBeneficiaryUnitFullName(String beneficiaryUnitFullName) {
	this.beneficiaryUnitFullName = beneficiaryUnitFullName;
    }

    @Basic
    @Column(name = "BENEFICAIRY_CATEGORY_ID")
    public Long getBeneficiaryCategoryId() {
	return beneficiaryCategoryId;
    }

    public void setBeneficiaryCategoryId(Long beneficiaryCategoryId) {
	this.beneficiaryCategoryId = beneficiaryCategoryId;
	wfNotificationConfig.setBeneficiaryCategoryId(beneficiaryCategoryId);
    }

    @Basic
    @Column(name = "BENEFICAIRY_CATEGORY_DESC")
    public String getBeneficiaryCategoryDescription() {
	return beneficiaryCategoryDescription;
    }

    public void setBeneficiaryCategoryDescription(String beneficiaryCategoryDescription) {
	this.beneficiaryCategoryDescription = beneficiaryCategoryDescription;
    }

    @Basic
    @Column(name = "WF_PROCESS_GROUP_ID")
    public Long getWfProcessGroupId() {
	return wfProcessGroupId;
    }

    public void setWfProcessGroupId(Long wfProcessGroupId) {
	this.wfProcessGroupId = wfProcessGroupId;
	wfNotificationConfig.setWfProcessGroupId(wfProcessGroupId);
    }

    @Basic
    @Column(name = "WF_PROCESS_GROUP_NAME")
    public String getWfProcessGroupName() {
	return wfProcessGroupName;
    }

    public void setWfProcessGroupName(String wfProcessGroupName) {
	this.wfProcessGroupName = wfProcessGroupName;
    }

    @Basic
    @Column(name = "WF_PROCESS_ID")
    public Long getWfProcessId() {
	return wfProcessId;
    }

    public void setWfProcessId(Long wfProcessId) {
	this.wfProcessId = wfProcessId;
	wfNotificationConfig.setWfProcessId(wfProcessId);
    }

    @Basic
    @Column(name = "WF_PROCESS_NAME")
    public String getWfProcessName() {
	return wfProcessName;
    }

    public void setWfProcessName(String wfProcessName) {
	this.wfProcessName = wfProcessName;
    }

    @Transient
    public WFNotificationsConfig getWfNotificationConfig() {
	return wfNotificationConfig;
    }

    public void setWfNotificationConfig(WFNotificationsConfig wfNotificationConfig) {
	this.wfNotificationConfig = wfNotificationConfig;
    }

}
