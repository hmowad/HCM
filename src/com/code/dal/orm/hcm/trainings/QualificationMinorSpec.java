package com.code.dal.orm.hcm.trainings;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@Entity
@Table(name = "HCM_QUALIFICATION_MINOR_SPECS")
public class QualificationMinorSpec extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {
    private Long id;
    private String description;
    private Long qualificationMajorSpecId;

    @SequenceGenerator(name = "HCMTRNQualSeq",
	    sequenceName = "HCM_TRN_QUAL_SEQ")
    @Id
    @GeneratedValue(generator = "HCMTRNQualSeq")
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

    @Basic
    @Column(name = "QUALIFICATION_MAJOR_SPEC_ID")
    public Long getQualificationMajorSpecId() {
	return qualificationMajorSpecId;
    }

    public void setQualificationMajorSpecId(Long qualificationMajorSpecId) {
	this.qualificationMajorSpecId = qualificationMajorSpecId;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "description:" + description + AUDIT_SEPARATOR +
		"qualificationMajorSpecId:" + qualificationMajorSpecId + AUDIT_SEPARATOR;
    }
}
