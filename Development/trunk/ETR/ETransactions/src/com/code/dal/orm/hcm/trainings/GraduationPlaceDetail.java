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
@Table(name = "HCM_GRADUATION_PLACES_DTLS")
public class GraduationPlaceDetail extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {

    private Long id;
    private String description;
    private Long graduationPlaceId;
    private String address;

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
    @Column(name = "GRADUATION_PLACE_ID")
    public Long getGraduationPlaceId() {
	return graduationPlaceId;
    }

    public void setGraduationPlaceId(Long graduationPlaceId) {
	this.graduationPlaceId = graduationPlaceId;
    }

    @Basic
    @Column(name = "ADDRESS")
    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "description:" + description + AUDIT_SEPARATOR +
		"graduationPlaceId:" + graduationPlaceId + AUDIT_SEPARATOR +
		"address:" + address + AUDIT_SEPARATOR;
    }
}
