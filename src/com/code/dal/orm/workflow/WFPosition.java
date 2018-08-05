package com.code.dal.orm.workflow;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

/**
 * The <code>WFPosition</code> class represents the data for any specific position that will be used by the WorkFlow tasks <code>WFTask</code>.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "wf_position_getWFPosition",
		query = " select p from WFPosition p " +
			" where p.code = :P_CODE " +
			" and p.regionId = :P_REGION_ID"
	)
})
@Entity
@Table(name = "WF_POSITIONS")
public class WFPosition extends BaseEntity {
    private Long id;
    private Integer code;
    private String description;
    private Long regionId;
    private Long unitId;
    private Long empId;

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
    @Column(name = "REGION_ID")
    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
    }

    @Basic
    @Column(name = "UNIT_ID")
    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }
}
