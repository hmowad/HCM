package com.code.dal.orm.hcm.specializations;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

/**
 * The <code>MinorSpecialization</code> class represents the data of the minor specialization which describe the specific task or description for the job.
 * 
 */
@Entity
@Table(name = "HCM_MINOR_SPECIALIZATIONS")
public class MinorSpecialization extends BaseEntity {
    private Long id;
    private Integer code;
    private String description;
    private Long majorSpecializationId;
    private Integer generalSpecialization;
    private Integer generalType;

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "CODE")
    public Integer getCode() {
	return code;
    }

    public void setCode(Integer code) {
	this.code = code;
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
    @Column(name = "MAJOR_SPECIALIZATION_ID")
    public Long getMajorSpecializationId() {
	return majorSpecializationId;
    }

    public void setMajorSpecializationId(Long majorSpecializationId) {
	this.majorSpecializationId = majorSpecializationId;
    }

    @Basic
    @Column(name = "GENERAL_SPECIALIZATION")
    public Integer getGeneralSpecialization() {
	return generalSpecialization;
    }

    public void setGeneralSpecialization(Integer generalSpecialization) {
	this.generalSpecialization = generalSpecialization;
    }

    @Basic
    @Column(name = "GENERAL_TYPE")
    public Integer getGeneralType() {
	return generalType;
    }

    public void setGeneralType(Integer generalType) {
	this.generalType = generalType;
    }
}
