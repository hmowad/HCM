package com.code.dal.orm.hcm;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
    @NamedQuery(name = "hcm_graduationGroupPlaces_getAllGraduationGroupPlaces",
	    	query = " from GraduationGroupPlace g" +
	    		" where (:P_ID = -1 or g.id = :P_ID) " +
	    		" order by g.id ")
})
@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_GRADUATION_GROUP_PLACES")
public class GraduationGroupPlace extends BaseEntity implements Serializable {
    private Long id;
    private String description;

    @Id
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
}