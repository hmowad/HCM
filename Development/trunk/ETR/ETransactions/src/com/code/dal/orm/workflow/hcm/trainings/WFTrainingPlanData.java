package com.code.dal.orm.workflow.hcm.trainings;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "wf_trainingPlanData_searchWFTrainingPlanData",
		query = "select p from WFTrainingPlanData p , WFInstance i " +
			"where (p.instanceId = i.id)" +
			" and (:P_INSTANCE_ID =-1 or p.instanceId =:P_INSTANCE_ID )" +
			" and (:P_TRAINING_YEAR_ID = -1 or p.trainingYearId = :P_TRAINING_YEAR_ID)" +
			" and (:p_TRAINING_UNIT_ID = -1 or p.trainingUnitId = :p_TRAINING_UNIT_ID)" +
			" and (:P_REGION_ID = -1 or p.regionId = :P_REGION_ID)" +
			" and (:P_PROCESS_ID = -1 or i.processId = :P_PROCESS_ID)" +
			" and (:P_STATUS = -1 or i.status = :P_STATUS)" +
			" order by p.instanceId")
})
@Entity
@Table(name = "ETR_VW_WF_TRAINING_PLANS")
public class WFTrainingPlanData extends BaseEntity {
    private Long instanceId;
    private String remarks;
    private Long trainingYearId;
    private String trainingYearName;
    private Long trainingUnitId;
    private String trainingUnitName;
    private Long trainingUnitRegionId;
    private Long regionId;
    private String regionName;
    private WFTrainingPlan wfTrainingPlan;

    public WFTrainingPlanData() {
	wfTrainingPlan = new WFTrainingPlan();
    }

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
	this.wfTrainingPlan.setInstanceId(instanceId);
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
	this.wfTrainingPlan.setRemarks(remarks);
    }

    @Basic
    @Column(name = "TRAINING_YEAR_ID")
    public Long getTrainingYearId() {
	return trainingYearId;
    }

    public void setTrainingYearId(Long trainingYearId) {
	this.trainingYearId = trainingYearId;
	this.wfTrainingPlan.setTrainingYearId(trainingYearId);
    }

    @Basic
    @Column(name = "TRAINING_YEAR_NAME")
    public String getTrainingYearName() {
	return trainingYearName;
    }

    public void setTrainingYearName(String trainingYearName) {
	this.trainingYearName = trainingYearName;
    }

    @Basic
    @Column(name = "TRAINING_UNIT_ID")
    public Long getTrainingUnitId() {
	return trainingUnitId;
    }

    public void setTrainingUnitId(Long trainingUnitId) {
	this.trainingUnitId = trainingUnitId;
	this.wfTrainingPlan.setTrainingUnitId(trainingUnitId);
    }

    @Basic
    @Column(name = "TRAINING_UNIT_NAME")
    public String getTrainingUnitName() {
	return trainingUnitName;
    }

    public void setTrainingUnitName(String trainingUnitName) {
	this.trainingUnitName = trainingUnitName;
    }

    @Basic
    @Column(name = "TRAINING_UNIT_REGION_ID")
    public Long getTrainingUnitRegionId() {
	return trainingUnitRegionId;
    }

    public void setTrainingUnitRegionId(Long trainingUnitRegionId) {
	this.trainingUnitRegionId = trainingUnitRegionId;
    }

    @Basic
    @Column(name = "REGION_ID")
    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
	this.wfTrainingPlan.setRegionId(regionId);
    }

    @Basic
    @Column(name = "REGION_NAME")
    public String getRegionName() {
	return regionName;
    }

    public void setRegionName(String regionName) {
	this.regionName = regionName;
    }

    @Transient
    public WFTrainingPlan getWfTrainingPlan() {
	return wfTrainingPlan;
    }

    public void setWfTrainingPlan(WFTrainingPlan wfTrainingPlan) {
	this.wfTrainingPlan = wfTrainingPlan;
    }

}
