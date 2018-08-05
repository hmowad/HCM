package com.code.dal.orm.hcm.additionalspecializations;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_additionalSpecialization_searchAdditionalSpecialization",
		query = " select s from AdditionalSpecialization s where " +
			" (:P_EXCLUDED_ID = -1 or  s.id <> :P_EXCLUDED_ID ) " +
			" and (:P_DESCRIPTION = '-1' or s.description like :P_DESCRIPTION) " +
			" order by s.description ")
})

@Entity
@Table(name = "HCM_ADDITIONAL_SPECIALIZATIONS")
public class AdditionalSpecialization extends BaseEntity implements Serializable {
    private Long id;
    private String description;

    @SequenceGenerator(name = "HCMSetupSeq",
	    sequenceName = "HCM_SETUP_SEQ")
    @Id
    @GeneratedValue(generator = "HCMSetupSeq")
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
