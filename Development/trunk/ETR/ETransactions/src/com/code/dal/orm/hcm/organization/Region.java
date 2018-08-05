package com.code.dal.orm.hcm.organization;

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
 * The <code>Region</code> class represents the data of the regions.</br>
 * It represents the several regions of the Saudia Arabia Kingdom.</br>
 * These regions have been listed in <code>RegionsEnum</code>.
 * 
 */
@SuppressWarnings("serial")
@NamedQueries({
	@NamedQuery(name = "hcm_Region_getRegion",
		query = " select r from Region r " +
			" order by r.id")
})
@Entity
@Table(name = "HCM_ORG_REGIONS")
public class Region extends BaseEntity implements Serializable {
    private Long id;
    private String code;
    private String description;
    private String shortDescription;

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
    public String getCode() {
	return code;
    }

    public void setCode(String code) {
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
    @Column(name = "SHORT_DESCRIPTION")
    public String getShortDescription() {
	return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
	this.shortDescription = shortDescription;
    }
}
