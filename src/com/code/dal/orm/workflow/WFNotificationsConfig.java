package com.code.dal.orm.workflow;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@NamedQueries({
	@NamedQuery(name = "wf_notificationsConfig_countWFNotificationsConfig",
		query = "select count(n.id) from WFNotificationsConfig n where  " +
			"(:P_EXCLUDED_ID = -1 or n.id <> :P_EXCLUDED_ID)" +
			"and ( n.wfProcessGroupId = :P_PROCESS_GROUP_ID)" +
			"and ( ( :P_PROCESS_ID = -1 and n.wfProcessId is null )  or n.wfProcessId = :P_PROCESS_ID)" +
			"and ( n.beneficiaryCategoryId = :P_BENEFICIARY_CATEGORY_ID)" +
			"and ( ( :P_DECISION_REGION_ID = -1 and n.decisionRegionId is null )  or n.decisionRegionId = :P_DECISION_REGION_ID)" +
			"and ( ( :P_BENEFICIARY_REGION_ID = -1 and n.beneficiaryRegionId is null )  or n.beneficiaryRegionId = :P_BENEFICIARY_REGION_ID)" +
			"and ( ( :P_BENEFICIARY_UNIT_ID = -1 and n.beneficiaryUnitId is null )  or n.beneficiaryUnitId = :P_BENEFICIARY_UNIT_ID)"
	)
})
@Entity
@Table(name = "WF_NOTIFICATIONS_CONFIG")
public class WFNotificationsConfig extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {
    private Long id;
    private String description;
    private Long decisionRegionId;
    private Long beneficiaryRegionId;
    private Long beneficiaryUnitId;
    private Long beneficiaryCategoryId;
    private Long wfProcessGroupId;
    private Long wfProcessId;

    @SequenceGenerator(name = "WFNotificationsConfigSeq", sequenceName = "WF_NOTIFICATIONS_CONFIG_SEQ")
    @Id
    @GeneratedValue(generator = "WFNotificationsConfigSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @Basic
    @Column(name = "DESICION_REGION_ID")
    public Long getDecisionRegionId() {
	return decisionRegionId;
    }

    public void setDecisionRegionId(Long decisionRegionId) {
	this.decisionRegionId = decisionRegionId;
    }

    @Basic
    @Column(name = "BENEFICIARY_REGION_ID")
    public Long getBeneficiaryRegionId() {
	return beneficiaryRegionId;
    }

    public void setBeneficiaryRegionId(Long beneficiaryRegionId) {
	this.beneficiaryRegionId = beneficiaryRegionId;
    }

    @Basic
    @Column(name = "BENEFICIARY_UNIT_ID")
    public Long getBeneficiaryUnitId() {
	return beneficiaryUnitId;
    }

    public void setBeneficiaryUnitId(Long beneficiaryUnitId) {
	this.beneficiaryUnitId = beneficiaryUnitId;
    }

    @Basic
    @Column(name = "BENEFICAIRY_CATEGORY_ID")
    public Long getBeneficiaryCategoryId() {
	return beneficiaryCategoryId;
    }

    public void setBeneficiaryCategoryId(Long beneficiaryCategoryId) {
	this.beneficiaryCategoryId = beneficiaryCategoryId;
    }

    @Basic
    @Column(name = "WF_PROCESS_GROUP_ID")
    public Long getWfProcessGroupId() {
	return wfProcessGroupId;
    }

    public void setWfProcessGroupId(Long wfProcessGroupId) {
	this.wfProcessGroupId = wfProcessGroupId;
    }

    @Basic
    @Column(name = "WF_PROCESS_ID")
    public Long getWfProcessId() {
	return wfProcessId;
    }

    public void setWfProcessId(Long wfProcessId) {
	this.wfProcessId = wfProcessId;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "description:" + description + AUDIT_SEPARATOR +
		"decisionRegionId:" + decisionRegionId + AUDIT_SEPARATOR +
		"beneficiaryRegionId:" + beneficiaryRegionId + AUDIT_SEPARATOR +
		"beneficiaryUnitId:" + beneficiaryUnitId + AUDIT_SEPARATOR +
		"beneficiaryCategoryId:" + beneficiaryCategoryId + AUDIT_SEPARATOR +
		"wfProcessGroupId:" + wfProcessGroupId + AUDIT_SEPARATOR +
		"wfProcessId:" + wfProcessId;
    }
}
