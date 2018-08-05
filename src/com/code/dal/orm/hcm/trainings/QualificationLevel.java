package com.code.dal.orm.hcm.trainings;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_qualificationLevel_searchQualificationLevel",
		query = " SELECT l from QualificationLevel l where " +
			" (:P_IDS_FLAG = -1 or l.id in ( :P_IDS )) " +
			" order by l.id")
})

@Entity
@Table(name = "HCM_QUALIFICATION_LEVELS")
public class QualificationLevel extends BaseEntity {
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