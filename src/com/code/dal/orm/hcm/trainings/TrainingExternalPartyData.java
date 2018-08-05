package com.code.dal.orm.hcm.trainings;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_trainingExternalPartyData_searchTrainingExternalPartyData",
		query = " select e from TrainingExternalPartyData e" +
			" where (:P_ID = -1 or e.id = :P_ID) " +
			" and (:P_PARTY_TYPE = -1 or e.type = :P_PARTY_TYPE )" +
			" and (:P_PARTY_DESCRIPTION = '-1' or e.description like :P_PARTY_DESCRIPTION )" +
			" and (:P_COUNTRY_ID = -1 or e.countryId = :P_COUNTRY_ID )" +
			" and (:P_EXCLUDED_ID = -1 or e.id <> :P_EXCLUDED_ID)" +
			" and (:P_PARTY_ADDRESS = '-1'  or e.address like :P_PARTY_ADDRESS)")
})
@Entity
@Table(name = "HCM_VW_TRN_EXTERNAL_PARTIES")
public class TrainingExternalPartyData extends BaseEntity {
    private Long id;
    private String description;
    private Integer type;
    private Long countryId;
    private String countryName;
    private String address;
    private TrainingExternalParty trainingExternalParty;

    public TrainingExternalPartyData() {
	trainingExternalParty = new TrainingExternalParty();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.trainingExternalParty.setId(id);
    }

    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
	this.trainingExternalParty.setDescription(description);
    }

    @Basic
    @Column(name = "TYPE")
    @XmlTransient
    public Integer getType() {
	return type;
    }

    public void setType(Integer type) {
	this.type = type;
	this.trainingExternalParty.setType(type);
    }

    @Basic
    @Column(name = "COUNTRY_ID")
    @XmlTransient
    public Long getCountryId() {
	return countryId;
    }

    public void setCountryId(Long countryId) {
	this.countryId = countryId;
	this.trainingExternalParty.setCountryId(countryId);
    }

    @Basic
    @Column(name = "COUNTRY_NAME")
    public String getCountryName() {
	return countryName;
    }

    public void setCountryName(String countryName) {
	this.countryName = countryName;
    }

    @Basic
    @Column(name = "ADDRESS")
    @XmlElement(nillable = true)
    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
	this.trainingExternalParty.setAddress(address);
    }

    @Transient
    @XmlTransient
    public TrainingExternalParty getTrainingExternalParty() {
	return trainingExternalParty;
    }

    public void setTrainingExternalParty(TrainingExternalParty trainingExternalParty) {
	this.trainingExternalParty = trainingExternalParty;
    }

}
