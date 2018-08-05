package com.code.dal.orm.hcm.specializations;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

/**
 * The <code>MajorSpecialization</code> class represents the data of the majors specialization.</br> 
 * The major specialization denotes one or more <code>MinorSpecialization</code>.</br> 
 * The major specialization categorize the minors specialization in different groups based on the specialization.</br>
 * 
 */
@Entity
@Table(name = "HCM_MAJOR_SPECIALIZATIONS")
public class MajorSpecialization extends BaseEntity implements Serializable {
    private Long id;
    private Integer code;
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
}
