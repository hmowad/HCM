package com.code.dal.orm.hcm.trainings;

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
	@NamedQuery(name = "hcm_trainingunitdata_searchTrainingUnitData",
		query = " select t from TrainingUnitData t " +
			" where (:P_TRAINING_UNITS_REGIONS_IDS_FLAG = -1 or t.regionId in (:P_TRAINING_UNITS_REGIONS_IDS)) " +
			" and (:P_ALLOWED_NOMINATION_REGION_ID = '-1' or t.regionsTrainingConfig like :P_ALLOWED_NOMINATION_REGION_ID) " +
			" and (:P_UNIT_ID = -1 or t.unitId = :P_UNIT_ID )" +
			" order by t.regionId"),
	@NamedQuery(name = "hcm_trainingunitdata_searchMissingTrainingUnitsPlanApproval",
		query = " select t from TrainingUnitData t " +
			" where t.unitId not in ( " +
			" select distinct (e.trainingUnitId) from TrainingCourseEventData e " +
			" where (e.trainingYearId = :P_TRAINING_YEAR_ID )) "),

	@NamedQuery(name = "hcm_trainingUnitData_getTrainingUnitDataByTransactionDetailId",
		query = "select tu from TrainingUnitData tu, TrainingCourseEvent ce, TrainingTransaction t, TrainingTransactionDetail td "
			+ "where (td.trainingTransactionId = t.id) "
			+ " and (t.courseEventId = ce.id) "
			+ " and (ce.trainingUnitId = tu.id) "
			+ "and (td.id = :P_TRAINING_TRANSACTION_DETAIL_ID) ")

})

@Entity
@Table(name = "HCM_VW_TRN_UNITS")
public class TrainingUnitData extends BaseEntity {
    private Long unitId;
    private String name;
    private Long regionId;
    private String regionsTrainingConfig;
    private TrainingUnit trainingUnit;

    public TrainingUnitData() {
	trainingUnit = new TrainingUnit();
    }

    @Id
    @Column(name = "UNIT_ID")
    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
	this.trainingUnit.setUnitId(unitId);
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
	this.trainingUnit.setName(name);
    }

    @Basic
    @Column(name = "REGION_ID")
    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
	this.trainingUnit.setRegionId(regionId);
    }

    @Basic
    @Column(name = "REGIONS_TRAINING_CONFIG")
    public String getRegionsTrainingConfig() {
	return regionsTrainingConfig;
    }

    public void setRegionsTrainingConfig(String regionsTrainingConfig) {
	this.regionsTrainingConfig = regionsTrainingConfig;
	this.trainingUnit.setRegionsTrainingConfig(regionsTrainingConfig);
    }

    @Transient
    public TrainingUnit getTrainingUnit() {
	return trainingUnit;
    }

    public void setTrainingUnit(TrainingUnit trainingUnit) {
	this.trainingUnit = trainingUnit;
    }
}
