package com.code.dal.orm.workflow.hcm.promotions;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@Entity
@Table(name = "WF_PROMOTION_NOTIFICATIONS")
public class WFPromotionNotification extends BaseEntity {

    private Long instanceId;
    private Long reportDetailId;

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    @Id
    @Column(name = "REPORT_DETAIL_ID")
    public Long getReportDetailId() {
	return reportDetailId;
    }

    public void setReportDetailId(Long reportDetailId) {
	this.reportDetailId = reportDetailId;
    }

}
