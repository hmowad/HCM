package com.code.dal.orm.hcm.trainings;

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

@NamedQueries({
	@NamedQuery(name = "hcm_qualificationMajorSpec_searchQualificationMajorSpec",
		query = " select qMajorSpec from QualificationMajorSpec qMajorSpec where " +
			" (:P_MILITARY_CLASSIFICATION_FLAG = -1 or qMajorSpec.militaryClassificationFlag = :P_MILITARY_CLASSIFICATION_FLAG) " +
			" and ((:P_EXACT_DESC_FLAG = 0 and (:P_QUAL_MAJOR_SPEC_DESC = '-1' or qMajorSpec.description like :P_QUAL_MAJOR_SPEC_DESC)or (qMajorSpec.description = :P_QUAL_MAJOR_SPEC_DESC and :P_EXACT_DESC_FLAG = 1))) " +
			" and (:P_EXCLUDED_ID = -1 or qMajorSpec.id <> :P_EXCLUDED_ID )" +
			" order by qMajorSpec.id "),

	@NamedQuery(name = "hcm_qualificationMajorSpec_searchQualificationMajorAndMinorSpec",
		query = " select qMajorSpec from QualificationMajorSpec qMajorSpec where " +
			"(:P_MINOR_INCLUDED_FLAG = 0" +
			" and (:P_QUAL_MAJOR_SPEC_DESC = '-1' or qMajorSpec.description like :P_QUAL_MAJOR_SPEC_DESC) " +
			" and (qMajorSpec.militaryClassificationFlag = :P_MILITARY_CLASSIFICATION_FLAG))" +
			" or  (:P_MINOR_INCLUDED_FLAG = 1 and qMajorSpec.id in ( " +
			" select qMinSpec.qualificationMajorSpecId from QualificationMinorSpecData qMinSpec where" +
			" (qMinSpec.militaryClassificationFlag = :P_MILITARY_CLASSIFICATION_FLAG) " +
			" and (:P_QUAL_MINOR_SPEC_DESC = '-1' or qMinSpec.description like :P_QUAL_MINOR_SPEC_DESC) " +
			" and (:P_QUAL_MAJOR_SPEC_DESC = '-1' or qMinSpec.qualificationMajorSpecDesc like :P_QUAL_MAJOR_SPEC_DESC) ))" +
			" order by qMajorSpec.id ")
})
@Entity
@Table(name = "HCM_QUALIFICATION_MAJOR_SPECS")
public class QualificationMajorSpec extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {
    private Long id;
    private String description;
    private Integer militaryClassificationFlag;

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
    @Column(name = "MILITARY_CLASSIFICATION_FLAG")
    public Integer getMilitaryClassificationFlag() {
	return militaryClassificationFlag;
    }

    public void setMilitaryClassificationFlag(Integer militaryClassificationFlag) {
	this.militaryClassificationFlag = militaryClassificationFlag;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "description:" + description + AUDIT_SEPARATOR +
		"militaryClassificationFlag:" + militaryClassificationFlag + AUDIT_SEPARATOR;
    }

}
