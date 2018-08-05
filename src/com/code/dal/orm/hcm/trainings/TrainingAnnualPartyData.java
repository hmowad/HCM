package com.code.dal.orm.hcm.trainings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_trainingAnnualPartyData_searchTrainingAnnualPartyData",
		query = " select p from TrainingAnnualPartyData p" +
			" where (:P_TRAINING_UNIT_ID = -1 or p.trainingUnitId = :P_TRAINING_UNIT_ID)" +
			" and (:P_TRAINING_EXTERNAL_PARTY_ID = -1 or p.externalPartyId = :P_TRAINING_EXTERNAL_PARTY_ID )" +
			" order by p.externalPartyId")
})

@Entity
@Table(name = "HCM_VW_TRN_ANNUAL_PARTIES")
public class TrainingAnnualPartyData extends BaseEntity {
    private Long externalPartyId;
    private Long trainingUnitId;
    private String externalPartyDesc;
    private TrainingAnnualParty trainingAnnualParty;

    public TrainingAnnualPartyData() {
	trainingAnnualParty = new TrainingAnnualParty();
    }

    @Id
    @Column(name = "TRAINING_UNIT_ID")
    public Long getTrainingUnitId() {
	return trainingUnitId;
    }

    public void setTrainingUnitId(Long trainingUnitId) {
	this.trainingUnitId = trainingUnitId;
	this.trainingAnnualParty.setTrainingUnitId(trainingUnitId);
    }

    @Id
    @Column(name = "EXTERNAL_PARTY_ID")
    public Long getExternalPartyId() {
	return externalPartyId;
    }

    public void setExternalPartyId(Long externalPartyId) {
	this.externalPartyId = externalPartyId;
	this.trainingAnnualParty.setExternalPartyId(externalPartyId);
    }

    @Id
    @Column(name = "DESCRIPTION")
    public String getExternalPartyDesc() {
	return externalPartyDesc;
    }

    public void setExternalPartyDesc(String externalPartyDesc) {
	this.externalPartyDesc = externalPartyDesc;
    }

    @Transient
    public TrainingAnnualParty getTrainingAnnualParty() {
	return trainingAnnualParty;
    }

    public void setTrainingAnnualParty(TrainingAnnualParty trainingAnnualParty) {
	this.trainingAnnualParty = trainingAnnualParty;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	TrainingAnnualPartyData other = (TrainingAnnualPartyData) obj;
	if (externalPartyId == null) {
	    if (other.externalPartyId != null)
		return false;
	} else if (!externalPartyId.equals(other.externalPartyId))
	    return false;
	if (trainingUnitId == null) {
	    if (other.trainingUnitId != null)
		return false;
	} else if (!trainingUnitId.equals(other.trainingUnitId))
	    return false;
	return true;
    }

}
