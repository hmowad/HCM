package com.code.dal.orm.hcm.organization.jobs;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

/**
 * The <code>BasicJobName</code> class represents the basic jobs names (the jobs titles).
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_basicJobName_countBasicJobsNames",
		query = " select count(b.id) from BasicJobName b " +
			" where b.basicJobName = :P_BASIC_JOB_NAME " +
			" and b.categoryId = :P_CATEGORY_ID "
	)
})
@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_ORG_BASIC_JOBS_NAMES")
public class BasicJobName extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {

    private Long id;
    private String basicJobName;
    private Long categoryId;

    @SequenceGenerator(name = "HCMOrgBasicJobNameSeq", sequenceName = "HCM_ORG_BASIC_JOBS_NAMES_SEQ")
    @Id
    @GeneratedValue(generator = "HCMOrgBasicJobNameSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "BASIC_JOB_NAME")
    public String getBasicJobName() {
	return basicJobName;
    }

    public void setBasicJobName(String basicJobName) {
	this.basicJobName = basicJobName;
    }

    @Basic
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "basicJobName:" + basicJobName + AUDIT_SEPARATOR +
		"categoryId:" + categoryId;
    }

}
