package com.code.dal.orm.hcm.organization.jobs;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;

/**
 * The <code>BasicJobNameData</code> class represents the basic jobs names (the jobs titles) in detailed format.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_basicJobNameData_searchBasicJobNameData",
		query = " select b from BasicJobNameData b " +
			" where (:P_BASIC_JOB_NAME_ID = -1 or b.id = :P_BASIC_JOB_NAME_ID) " +
			" and (:P_BASIC_JOB_NAME = '-1' or b.basicJobName like :P_BASIC_JOB_NAME) " +
			" and (b.categoryId in ( :P_CATEGORIES_IDS )) " +
			" and (:P_BASIC_JOBS_NAMES_EXCLUDED_IDS_FLAG = -1 or b.id not in ( :P_BASIC_JOBS_NAMES_EXCLUDED_IDS )) " +
			" order by b.categoryId, b.basicJobName "
	)
})
@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_VW_ORG_BASIC_JOBS_NAMES")
public class BasicJobNameData extends BaseEntity {

    private Long id;
    private String basicJobName;
    private Long categoryId;
    private String categoryDescription;

    private BasicJobName basicJobNameEntity;

    public BasicJobNameData() {
	basicJobNameEntity = new BasicJobName();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.basicJobNameEntity.setId(id);
    }

    @Basic
    @Column(name = "BASIC_JOB_NAME")
    public String getBasicJobName() {
	return basicJobName;
    }

    public void setBasicJobName(String basicJobName) {
	this.basicJobName = basicJobName;
	this.basicJobNameEntity.setBasicJobName(basicJobName);
    }

    @Basic
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
	this.basicJobNameEntity.setCategoryId(categoryId);
    }

    @Basic
    @Column(name = "CATEGORY_DESC")
    public String getCategoryDescription() {
	return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
	this.categoryDescription = categoryDescription;
    }

    @Transient
    public BasicJobName getBasicJobNameEntity() {
	return basicJobNameEntity;
    }

    public void setBasicJobNameEntity(BasicJobName basicJobNameEntity) {
	this.basicJobNameEntity = basicJobNameEntity;
    }

}
