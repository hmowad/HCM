package com.code.dal.orm.hcm.trainings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.orm.AuditEntity;

@Entity
@Table(name = "HCM_TRN_ANNUAL_PARTIES")
public class TrainingAnnualParty extends AuditEntity implements InsertableAuditEntity, DeletableAuditEntity {
    private Long trainingUnitId;
    private Long externalPartyId;

    @Id
    @Column(name = "TRAINING_UNIT_ID")
    public Long getTrainingUnitId() {
	return trainingUnitId;
    }

    public void setTrainingUnitId(Long trainingUnitId) {
	this.trainingUnitId = trainingUnitId;
    }

    @Id
    @Column(name = "EXTERNAL_PARTY_ID")
    public Long getExternalPartyId() {
	return externalPartyId;
    }

    public void setExternalPartyId(Long externalPartyId) {
	this.externalPartyId = externalPartyId;
    }

    @Override
    public Long calculateContentId() {
	return trainingUnitId;
    }

    @Override
    public String calculateContent() {
	return "trainingUnitId:" + trainingUnitId + AUDIT_SEPARATOR +
		"externalPartyId:" + externalPartyId + AUDIT_SEPARATOR;
    }
}
