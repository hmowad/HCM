package com.code.dal.orm.hcm.trainings;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_qualificationMinorSpecData_searchQualificationMinorSpec",
		query = " from QualificationMinorSpecData qMinSpec where " +
			" (:P_MILITARY_CLASSIFICATION_FLAG = -1 or qMinSpec.militaryClassificationFlag = :P_MILITARY_CLASSIFICATION_FLAG) " +
			" and (:P_DESC = '-1' or qMinSpec.description like :P_DESC) " +
			" and (:P_QUAL_MAJOR_SPEC_ID = -1 or qMinSpec.qualificationMajorSpecId = :P_QUAL_MAJOR_SPEC_ID) " +
			" and (:P_QUAL_MAJOR_SPEC_DESC = '-1' or qMinSpec.qualificationMajorSpecDesc like :P_QUAL_MAJOR_SPEC_DESC) " +
			" and (:P_EXCLUDED_ID = -1 or qMinSpec.id <> :P_EXCLUDED_ID) " +
			" order by qMinSpec.id ")
})
@Entity
@Table(name = "HCM_VW_QUAL_MINOR_SPECS")
public class QualificationMinorSpecData extends BaseEntity {
    private Long id;
    private String description;
    private Integer militaryClassificationFlag;
    private Long qualificationMajorSpecId;
    private String qualificationMajorSpecDesc;

    private QualificationMinorSpec QualificationMinoSpec;

    public QualificationMinorSpecData() {
	QualificationMinoSpec = new QualificationMinorSpec();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.QualificationMinoSpec.setId(id);
    }

    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
	this.QualificationMinoSpec.setDescription(description);

    }

    @Basic
    @Column(name = "MILITARY_CLASSIFICATION_FLAG")
    public Integer getMilitaryClassificationFlag() {
	return militaryClassificationFlag;
    }

    public void setMilitaryClassificationFlag(Integer militaryClassificationFlag) {
	this.militaryClassificationFlag = militaryClassificationFlag;
    }

    @Basic
    @Column(name = "QUALIFICATION_MAJOR_SPEC_ID")
    public Long getQualificationMajorSpecId() {
	return qualificationMajorSpecId;
    }

    public void setQualificationMajorSpecId(Long qualificationMajorSpecId) {
	this.qualificationMajorSpecId = qualificationMajorSpecId;
	this.QualificationMinoSpec.setQualificationMajorSpecId(qualificationMajorSpecId);
    }

    @Basic
    @Column(name = "QUALIFICATION_MAJOR_SPEC_DESC")
    public String getQualificationMajorSpecDesc() {
	return qualificationMajorSpecDesc;
    }

    public void setQualificationMajorSpecDesc(String qualificationMajorSpecDesc) {
	this.qualificationMajorSpecDesc = qualificationMajorSpecDesc;
    }

    @Transient
    public QualificationMinorSpec getQualificationMinoSpec() {
	return QualificationMinoSpec;
    }

    public void setQualificationMinoSpec(QualificationMinorSpec qualificationMinoSpec) {
	QualificationMinoSpec = qualificationMinoSpec;
    }
}
