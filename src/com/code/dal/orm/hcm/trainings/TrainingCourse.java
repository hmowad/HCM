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
@Table(name = "HCM_TRN_COURSES")
public class TrainingCourse extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {
    private Long id;
    private String name;
    private String nameEnglish;
    private Integer type;
    private Long qualificationMinorSpecId;
    private Integer candidatesMin;
    private Integer candidatesMax;
    private Integer weeksCount;
    private Integer exclusiveFlag;
    private String prerquisites;
    private Long categoryId;
    private Long fromRankId;
    private Long toRankId;
    private Integer generalSpecialization;
    private String minorSpecializationIds;
    private Integer jobGeneralType;
    private String qualificationLevelIds;
    private Integer promotionWitninMonths;
    private String syllabusAttachments;
    private String syllabusAttachmentsHistory;
    private Integer rankingFlag;
    private Integer graduationDecisionFlag;

    @SequenceGenerator(name = "HCMTrainingQualSeq",
	    sequenceName = "HCM_TRN_QUAL_SEQ")
    @Id
    @GeneratedValue(generator = "HCMTrainingQualSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Basic
    @Column(name = "NAME_ENGLISH")
    public String getNameEnglish() {
	return nameEnglish;
    }

    public void setNameEnglish(String nameEnglish) {
	this.nameEnglish = nameEnglish;
    }

    @Basic
    @Column(name = "TYPE")
    public Integer getType() {
	return type;
    }

    public void setType(Integer type) {
	this.type = type;
    }

    @Basic
    @Column(name = "QUALIFICATION_MINOR_SPEC_ID")
    public Long getQualificationMinorSpecId() {
	return qualificationMinorSpecId;
    }

    public void setQualificationMinorSpecId(Long qualificationMinorSpecId) {
	this.qualificationMinorSpecId = qualificationMinorSpecId;
    }

    @Basic
    @Column(name = "CANDIDATES_MIN")
    public Integer getCandidatesMin() {
	return candidatesMin;
    }

    public void setCandidatesMin(Integer candidatesMin) {
	this.candidatesMin = candidatesMin;
    }

    @Basic
    @Column(name = "CANDIDATES_MAX")
    public Integer getCandidatesMax() {
	return candidatesMax;
    }

    public void setCandidatesMax(Integer candidatesMax) {
	this.candidatesMax = candidatesMax;
    }

    @Basic
    @Column(name = "WEEKS_COUNT")
    public Integer getWeeksCount() {
	return weeksCount;
    }

    public void setWeeksCount(Integer weeksCount) {
	this.weeksCount = weeksCount;
    }

    @Basic
    @Column(name = "EXCLUSIVE_FLAG")
    public Integer getExclusiveFlag() {
	return exclusiveFlag;
    }

    public void setExclusiveFlag(Integer exclusiveFlag) {
	this.exclusiveFlag = exclusiveFlag;
    }

    @Basic
    @Column(name = "PRERQUISITES")
    public String getPrerquisites() {
	return prerquisites;
    }

    public void setPrerquisites(String prerquisites) {
	this.prerquisites = prerquisites;
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
    @Column(name = "FROM_RANK_ID")
    public Long getFromRankId() {
	return fromRankId;
    }

    public void setFromRankId(Long fromRankId) {
	this.fromRankId = fromRankId;
    }

    @Basic
    @Column(name = "TO_RANK_ID")
    public Long getToRankId() {
	return toRankId;
    }

    public void setToRankId(Long toRankId) {
	this.toRankId = toRankId;
    }

    @Basic
    @Column(name = "GENERAL_SPECIALIZATION")
    public Integer getGeneralSpecialization() {
	return generalSpecialization;
    }

    public void setGeneralSpecialization(Integer generalSpecialization) {
	this.generalSpecialization = generalSpecialization;
    }

    @Basic
    @Column(name = "MINOR_SPECIALIZATION_IDS")
    public String getMinorSpecializationIds() {
	return minorSpecializationIds;
    }

    public void setMinorSpecializationIds(String minorSpecializationIds) {
	this.minorSpecializationIds = minorSpecializationIds;
    }

    @Basic
    @Column(name = "JOB_GENERAL_TYPE")
    public Integer getJobGeneralType() {
	return jobGeneralType;
    }

    public void setJobGeneralType(Integer jobGeneralType) {
	this.jobGeneralType = jobGeneralType;
    }

    @Basic
    @Column(name = "QUALIFICATION_LEVEL_IDS")
    public String getQualificationLevelIds() {
	return qualificationLevelIds;
    }

    public void setQualificationLevelIds(String qualificationLevelIds) {
	this.qualificationLevelIds = qualificationLevelIds;
    }

    @Basic
    @Column(name = "PROMOTION_WITNIN_MONTHS")
    public Integer getPromotionWitninMonths() {
	return promotionWitninMonths;
    }

    public void setPromotionWitninMonths(Integer promotionWitninMonths) {
	this.promotionWitninMonths = promotionWitninMonths;
    }

    @Basic
    @Column(name = "SYLLABUS_ATTACHMENTS")
    public String getSyllabusAttachments() {
	return syllabusAttachments;
    }

    public void setSyllabusAttachments(String syllabusAttachments) {
	this.syllabusAttachments = syllabusAttachments;
    }

    @Basic
    @Column(name = "SYLLABUS_ATTACHMENTS_HISTORY")
    public String getSyllabusAttachmentsHistory() {
	return syllabusAttachmentsHistory;
    }

    public void setSyllabusAttachmentsHistory(String syllabusAttachmentsHistory) {
	this.syllabusAttachmentsHistory = syllabusAttachmentsHistory;
    }

    @Basic
    @Column(name = "RANKING_FLAG")
    public Integer getRankingFlag() {
	return rankingFlag;
    }

    public void setRankingFlag(Integer rankingFlag) {
	this.rankingFlag = rankingFlag;
    }

    @Basic
    @Column(name = "GRADUATION_DECISION_FLAG")
    public Integer getGraduationDecisionFlag() {
	return graduationDecisionFlag;
    }

    public void setGraduationDecisionFlag(Integer graduationDecisionFlag) {
	this.graduationDecisionFlag = graduationDecisionFlag;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "name:" + name + AUDIT_SEPARATOR +
		"nameEnglish:" + nameEnglish + AUDIT_SEPARATOR +
		"type:" + type + AUDIT_SEPARATOR +
		"qualificationMinorSpecId:" + qualificationMinorSpecId + AUDIT_SEPARATOR +
		"candidatesMin:" + candidatesMin + AUDIT_SEPARATOR +
		"candidatesMax:" + candidatesMax + AUDIT_SEPARATOR +
		"weeksCount:" + weeksCount + AUDIT_SEPARATOR +
		"exclusiveFlag:" + exclusiveFlag + AUDIT_SEPARATOR +
		"prerquisites:" + prerquisites + AUDIT_SEPARATOR +
		"categoryId:" + categoryId + AUDIT_SEPARATOR +
		"fromRankId:" + fromRankId + AUDIT_SEPARATOR +
		"toRankId:" + toRankId + AUDIT_SEPARATOR +
		"generalSpecialization:" + generalSpecialization + AUDIT_SEPARATOR +
		"minorSpecializationIds:" + minorSpecializationIds + AUDIT_SEPARATOR +
		"jobGeneralType:" + jobGeneralType + AUDIT_SEPARATOR +
		"qualificationLevelIds:" + qualificationLevelIds + AUDIT_SEPARATOR +
		"promotionWitninMonths:" + promotionWitninMonths + AUDIT_SEPARATOR +
		"syllabusAttachments:" + syllabusAttachments + AUDIT_SEPARATOR +
		"syllabusAttachmentsHistory" + syllabusAttachmentsHistory + AUDIT_SEPARATOR +
		"rankingFlag" + rankingFlag + AUDIT_SEPARATOR +
		"graduationDecisionFlag" + graduationDecisionFlag + AUDIT_SEPARATOR;
    }
}
