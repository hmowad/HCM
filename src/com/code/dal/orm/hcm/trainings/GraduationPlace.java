package com.code.dal.orm.hcm.trainings;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@Entity
@Table(name = "HCM_GRADUATION_PLACES")
public class GraduationPlace extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {
    private Long id;
    private String description;
    private Integer type;
    private Long countryId;

    @SequenceGenerator(name = "HCMTRNGradSeq",
	    sequenceName = "HCM_TRN_PLACES_SEQ")
    @Id
    @GeneratedValue(generator = "HCMTRNGradSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @Basic
    @Column(name = "TYPE")
    public Integer getType() {
	return type;
    }

    public void setType(Integer type) {
	this.type = type;
    }

    @Basic
    @Column(name = "COUNTRY_ID")
    public Long getCountryId() {
	return countryId;
    }

    public void setCountryId(Long countryId) {
	this.countryId = countryId;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "description:" + description + AUDIT_SEPARATOR +
		"type:" + type + AUDIT_SEPARATOR +
		"countryId:" + countryId + AUDIT_SEPARATOR;
    }
}