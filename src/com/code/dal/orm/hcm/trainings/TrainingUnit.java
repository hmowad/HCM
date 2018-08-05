package com.code.dal.orm.hcm.trainings;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@Entity
@Table(name = "HCM_TRN_UNITS")
public class TrainingUnit extends AuditEntity implements UpdatableAuditEntity {
    private Long unitId;
    private String name;
    private Long regionId;
    private String regionsTrainingConfig;

    @Id
    @Column(name = "UNIT_ID")
    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
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
    @Column(name = "REGION_ID")
    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
    }

    @Basic
    @Column(name = "REGIONS_TRAINING_CONFIG")
    public String getRegionsTrainingConfig() {
	return regionsTrainingConfig;
    }

    public void setRegionsTrainingConfig(String regionsTrainingConfig) {
	this.regionsTrainingConfig = regionsTrainingConfig;
    }

    @Override
    public Long calculateContentId() {
	return unitId;
    }

    @Override
    public String calculateContent() {
	return "unitId:" + unitId + AUDIT_SEPARATOR +
		"name:" + name + AUDIT_SEPARATOR +
		"regionId:" + regionId + AUDIT_SEPARATOR +
		"regionsTrainingConfig:" + regionsTrainingConfig + AUDIT_SEPARATOR;
    }
}
