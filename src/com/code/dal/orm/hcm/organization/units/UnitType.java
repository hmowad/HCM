package com.code.dal.orm.hcm.organization.units;

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
 * The <code>UnitType</code> class represents the type of the unit which is one of the types listed in the <code>UnitTypesEnum</code>.
 * 
 */
@SuppressWarnings("serial")
@NamedQueries({
	@NamedQuery(name = "hcm_unitType_getUnitType",
		query = " select u from UnitType u " +
			" where (:P_ID = -1 or u.id = :P_ID) " +
			" and (:P_CODE = -1 or u.code = :P_CODE) " +
			" order by u.code desc"
	)
})
@Entity
@Table(name = "HCM_ORG_UNITS_TYPES")
public class UnitType extends BaseEntity implements Serializable {
    private Long id;
    private Integer code;
    private String description;
    private String childrenUnitTypeCodes;

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
    @Column(name = "CHILDREN_UNIT_TYPE_CODES")
    public String getChildrenUnitTypeCodes() {
	return childrenUnitTypeCodes;
    }

    public void setChildrenUnitTypeCodes(String childrenUnitTypeCodes) {
	this.childrenUnitTypeCodes = childrenUnitTypeCodes;
    }
}