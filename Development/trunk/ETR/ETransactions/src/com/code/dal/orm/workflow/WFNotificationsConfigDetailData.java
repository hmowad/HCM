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
	@NamedQuery(name = "wf_notificationsConfigDetailData_searchWFNotificationsConfigDetailData",
		query = "select d from WFNotificationsConfigDetailData d " +
			" where d.wfNotificationsConfigId = :P_WF_NOTIFICATIONS_CONFIG_ID " +
			" order by d.categoryId, d.unitFullName, d.jobName ")
})
@Entity
@Table(name = "ETR_VW_WF_NOTIFICS_CONFIG_DTLS")
public class WFNotificationsConfigDetailData extends BaseEntity {

    private Long id;
    private Long wfNotificationsConfigId;

    private Long categoryId;
    private String categoryDesc;

    private Long unitId;
    private String unitFullName;

    private Long jobId;
    private String jobName;

    private WFNotificationsConfigDetail wfNotificationsConfigDetail;

    public WFNotificationsConfigDetailData() {
	wfNotificationsConfigDetail = new WFNotificationsConfigDetail();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	wfNotificationsConfigDetail.setId(id);
    }

    @Basic
    @Column(name = "WF_NOTIFICATIONS_CONFIG_ID")
    public Long getWfNotificationsConfigId() {
	return wfNotificationsConfigId;
    }

    public void setWfNotificationsConfigId(Long wfNotificationsConfigId) {
	this.wfNotificationsConfigId = wfNotificationsConfigId;
	wfNotificationsConfigDetail.setWfNotificationsConfigId(wfNotificationsConfigId);
    }

    @Basic
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
	wfNotificationsConfigDetail.setCategoryId(categoryId);
    }

    @Basic
    @Column(name = "CATEGORY_DESC")
    public String getCategoryDesc() {
	return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
	this.categoryDesc = categoryDesc;
    }

    @Basic
    @Column(name = "UNIT_ID")
    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
	wfNotificationsConfigDetail.setUnitId(unitId);
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
    @Column(name = "JOB_ID")
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
	wfNotificationsConfigDetail.setJobId(jobId);
    }

    @Basic
    @Column(name = "JOB_NAME")
    public String getJobName() {
	return jobName;
    }

    public void setJobName(String jobDesc) {
	this.jobName = jobDesc;
    }

    @Transient
    public WFNotificationsConfigDetail getWfNotificationsConfigDetail() {
	return wfNotificationsConfigDetail;
    }

    public void setWfNotificationsConfigDetail(WFNotificationsConfigDetail wfNotificationsConfigDetail) {
	this.wfNotificationsConfigDetail = wfNotificationsConfigDetail;
    }

}
