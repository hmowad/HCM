package com.code.dal.orm.hcm.terminations;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_terminationReason_getReasons",
		query = " select t from TerminationReason t " +
			" where ( :P_CATEGORY_ID = -1 or t.categoryId = :P_CATEGORY_ID )" +
			" order by t.id")
})

@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_STE_REASONS")
public class TerminationReason extends BaseEntity {

    private Long id;
    private Long categoryId;
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
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
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
