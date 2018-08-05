package com.code.dal.orm.workflow.hcm.trainings;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "wf_trainingPlan_countApprovedWFTrainingPlan",
		query = "select count( p.instanceId) from WFTrainingPlanData p , WFInstance i " +
			"where (p.instanceId = i.id)" +
			" and ( p.trainingYearId = :P_TRAINING_YEAR_ID)" +
			" and (:p_TRAINING_UNIT_ID = -1 or p.trainingUnitId = :p_TRAINING_UNIT_ID)" +
			" and (:P_REGION_ID = -1 or p.regionId = :P_REGION_ID)" +
			" and (i.processId = :P_PROCESS_ID)" +
			" and (i.status <> :P_EXCLUDED_STATUS)"
			+ "and((select count(t.id) from WFTask t where  t.action =:P_EXCLUDED_ACTION and t.instanceId =p.instanceId )=0)"),

})
@Entity
@Table(name = "WF_TRAINING_PLANS")
public class WFTrainingPlan extends BaseEntity {
    private Long instanceId;
    private Long trainingYearId;
    private Long trainingUnitId;
    private Long regionId;
    private String remarks;

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    @Basic
    @Column(name = "TRAINING_YEAR_ID")
    public Long getTrainingYearId() {
	return trainingYearId;
    }

    public void setTrainingYearId(Long trainingYearId) {
	this.trainingYearId = trainingYearId;
    }

    @Basic
    @Column(name = "TRAINING_UNIT_ID")
    public Long getTrainingUnitId() {
	return trainingUnitId;
    }

    public void setTrainingUnitId(Long trainingUnitId) {
	this.trainingUnitId = trainingUnitId;
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
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }
}
