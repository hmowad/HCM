package com.code.dal.orm.hcm.vacations;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

/**
 * The <code>VacationType</code> class represents the type of the vacation which is one of the types listed in the <code>VacationTypesEnum</code>.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_vacType_getVacationTypes",
		query = " select v from VacationType v " +
			" where (:P_CATEGORY_ID = '-1' or v.allowedCategoriesIds like :P_CATEGORY_ID) " +
			" order by v.vacationTypeId "),
	@NamedQuery(name = "hcm_vacType_searchVacationTypes",
		query = " select v from VacationType v " +
			" where (:P_CATEGORY_ID = '-1' or v.allowedCategoriesIds like :P_CATEGORY_ID) " +
			" and (:P_VACATION_TYPE_CODE = -1 or v.vacationTypeCode = :P_VACATION_TYPE_CODE) " +
			" order by v.vacationTypeId ")
})
@Entity
@Table(name = "HCM_VAC_TYPES")
public class VacationType extends BaseEntity implements Serializable {
    private Long vacationTypeId;
    private Integer vacationTypeCode;
    private String vacationTypeDesc;
    private String vacationTypeDescForEmployees;
    private String allowedCategoriesIds;

    @Id
    @Column(name = "ID")
    public Long getVacationTypeId() {
	return vacationTypeId;
    }

    public void setVacationTypeId(Long vacationTypeId) {
	this.vacationTypeId = vacationTypeId;
    }

    @Basic
    @Column(name = "CODE")
    public Integer getVacationTypeCode() {
	return vacationTypeCode;
    }

    public void setVacationTypeCode(Integer vacationTypeCode) {
	this.vacationTypeCode = vacationTypeCode;
    }

    @Basic
    @Column(name = "DESCRIPTION")
    public String getVacationTypeDesc() {
	return vacationTypeDesc;
    }

    public void setVacationTypeDesc(String vacationTypeDesc) {
	this.vacationTypeDesc = vacationTypeDesc;
    }

    @Basic
    @Column(name = "DESCRIPTION_FOR_EMPLOYEES")
    public String getVacationTypeDescForEmployees() {
	return vacationTypeDescForEmployees;
    }

    public void setVacationTypeDescForEmployees(String vacationTypeDescForEmployees) {
	this.vacationTypeDescForEmployees = vacationTypeDescForEmployees;
    }

    @Basic
    @Column(name = "ALLOWED_CATEGORIES_IDS")
    public String getAllowedCategoriesIds() {
	return allowedCategoriesIds;
    }

    public void setAllowedCategoriesIds(String allowedCategoriesIds) {
	this.allowedCategoriesIds = allowedCategoriesIds;
    }
}