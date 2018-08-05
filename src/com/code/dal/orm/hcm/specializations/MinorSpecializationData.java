package com.code.dal.orm.hcm.specializations;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

/**
 * The <code>MinorSpecializationData</code> class represents the data of the minor specialization which describe the specific task or description for the job in detailed format.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_minorSpecializationData_searchMinorSpecialization",
		query = " from MinorSpecializationData minSpec where " +
			" (:P_CODE = -1 or minSpec.code = :P_CODE) " +
			" and (:P_DESC = '-1' or minSpec.description like :P_DESC) " +
			" and (:P_MAJOR_SPEC_ID = -1 or minSpec.majorSpecializationId = :P_MAJOR_SPEC_ID) " +
			" and (:P_MAJOR_SPEC_DESC = '-1' or minSpec.majorSpecializationDesc like :P_MAJOR_SPEC_DESC) " +
			" and (:P_GENERAL_SPECIALIZATION = -1 or minSpec.generalSpecialization = :P_GENERAL_SPECIALIZATION) " +
			" and (:P_GENERAL_TYPES_FLAG = -1 or minSpec.generalType in (:P_GENERAL_TYPES)) " +
			" order by minSpec.id "),

	@NamedQuery(name = "hcm_minorSpecializationData_searchMinorSpecializationByIds",
		query = " select m from MinorSpecializationData m " +
			" where m.id in (:P_MINOR_SPECIALIZATIONS_IDS) " +
			" order by m.id ")
})
@Entity
@Table(name = "HCM_VW_MINOR_SPECIALIZATIONS")
public class MinorSpecializationData extends BaseEntity {
    private Long id;
    private Integer code;
    private String description;
    private Long majorSpecializationId;
    private Integer majorSpecializationCode;
    private String majorSpecializationDesc;
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
    @Column(name = "MAJOR_SPECIALIZATION_CODE")
    public Integer getMajorSpecializationCode() {
	return majorSpecializationCode;
    }

    public void setMajorSpecializationCode(Integer majorSpecializationCode) {
	this.majorSpecializationCode = majorSpecializationCode;
    }

    @Basic
    @Column(name = "MAJOR_SPECIALIZATION_DESC")
    public String getMajorSpecializationDesc() {
	return majorSpecializationDesc;
    }

    public void setMajorSpecializationDesc(String majorSpecializationDesc) {
	this.majorSpecializationDesc = majorSpecializationDesc;
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