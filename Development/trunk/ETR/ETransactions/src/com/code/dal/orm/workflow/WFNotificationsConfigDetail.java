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
	@NamedQuery(name = "wf_notificationsConfigDetail_countWFNotificationsConfigDetail",
		query = "select count(d.id) from WFNotificationsConfigDetail d   " +
			" where d.wfNotificationsConfigId = :P_NOTIFICATION_CONFIG_ID " +
			" and (:P_CATEGORY_ID = -1 or d.categoryId = :P_CATEGORY_ID) " +
			" and (:P_UNIT_ID = -1 or d.unitId = :P_UNIT_ID)" +
			" and (:P_JOB_ID = -1 or d.jobId = :P_JOB_ID)")
})
@Entity
@Table(name = "WF_NOTIFICATIONS_CONFIG_DTLS")
public class WFNotificationsConfigDetail extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {

    private Long id;
    private Long wfNotificationsConfigId;
    private Long categoryId;
    private Long unitId;
    private Long jobId;

    @SequenceGenerator(name = "WFNotificationsConfigSeq",
	    sequenceName = "WF_NOTIFICATIONS_CONFIG_SEQ")
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
    @Column(name = "WF_NOTIFICATIONS_CONFIG_ID")
    public Long getWfNotificationsConfigId() {
	return wfNotificationsConfigId;
    }

    public void setWfNotificationsConfigId(Long wfNotificationsConfigId) {
	this.wfNotificationsConfigId = wfNotificationsConfigId;
    }

    @Basic
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
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
    @Column(name = "JOB_ID")
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "wfNotificationsConfigId:" + wfNotificationsConfigId + AUDIT_SEPARATOR +
		"categoryId:" + categoryId + AUDIT_SEPARATOR +
		"unitId:" + unitId + AUDIT_SEPARATOR +
		"jobId:" + jobId;
    }
}
