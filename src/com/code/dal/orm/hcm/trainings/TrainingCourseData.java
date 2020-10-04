package com.code.dal.orm.hcm.trainings;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import com.code.dal.orm.BaseEntity;
import com.code.enums.FlagsEnum;

@NamedQueries({
	@NamedQuery(name = "hcm_trainingCourseData_searchTrainingCourseData",
		query = "select c from TrainingCourseData c "
			+ "where (:P_COURSE_ID = -1 or c.id = :P_COURSE_ID) "
			+ "and (:P_EXCLUDED_ID = -1 OR c.id != :P_EXCLUDED_ID) "
			+ "and (:P_COURSE_NAME ='-1' OR c.name like :P_COURSE_NAME) "
			+ "and (:P_COURSE_TYPE =-1 OR c.type =:P_COURSE_TYPE) "
			+ "and (:P_QUAL_MINOR_SPEC_ID =-1 or c.qualificationMinorSpecId=:P_QUAL_MINOR_SPEC_ID) "
			+ "and (:P_QUAL_MINOR_SPEC_DESC = '-1' or c.qualificationMinorSpecDesc LIKE :P_QUAL_MINOR_SPEC_DESC)"
			+ "and (:P_QUAL_MAJOR_SPEC_ID = -1 or c.qualificationMajorSpecId = :P_QUAL_MAJOR_SPEC_ID)"
			+ "and (:P_QUAL_MAJOR_SPEC_DESC = '-1' or c.qualificationMajorSpecDesc LIKE :P_QUAL_MAJOR_SPEC_DESC)"
			+ "order by c.id"),

	@NamedQuery(name = "hcm_trainingCourseData_getTrainingCourseDataByTransactionDetailId",
		query = "select c from TrainingCourseData c, TrainingCourseEvent ce, TrainingTransaction t, TrainingTransactionDetail td "
			+ "where (td.trainingTransactionId = t.id) "
			+ "and (t.courseEventId = ce.id) "
			+ "and (ce.courseId = c.id) "
			+ "and (td.id = :P_TRAINING_TRANSACTION_DETAIL_ID) ")

})
@Entity
@Table(name = "HCM_VW_TRN_COURSES")
public class TrainingCourseData extends BaseEntity {
    private Long id;
    private String name;
    private String nameEnglish;
    private Integer type;
    private Long qualificationMinorSpecId;
    private String qualificationMinorSpecDesc;
    private Long qualificationMajorSpecId;
    private String qualificationMajorSpecDesc;
    private Integer candidatesMin;
    private Integer candidatesMax;
    private Integer weeksCount;
    private Integer exclusiveFlag;
    private Boolean exclusiveFlagBoolean;
    private String prerquisites;
    private Long categoryId;
    private Long fromRankId;
    private String fromRankDesc;
    private Long toRankId;
    private String toRankDesc;
    private Integer generalSpecialization;
    private String minorSpecializationIds;
    private Integer jobGeneralType;
    private String qualificationLevelIds;
    private Integer promotionWitninMonths;
    private String syllabusAttachments;
    private String syllabusAttachmentsHistory;
    private Integer rankingFlag;
    private Integer electronicCertificateFlag;
    private Boolean rankingFlagBoolean;
    private Boolean electronicCertificateBoolean;
    private TrainingCourse trainingCourse;

    public TrainingCourseData() {
	trainingCourse = new TrainingCourse();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.trainingCourse.setId(id);
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
	this.trainingCourse.setName(name);
    }

    @Basic
    @Column(name = "NAME_ENGLISH")
    public String getNameEnglish() {
	return nameEnglish;
    }

    public void setNameEnglish(String nameEnglish) {
	this.nameEnglish = nameEnglish;
	this.trainingCourse.setNameEnglish(nameEnglish);
    }

    @Basic
    @XmlTransient
    @Column(name = "TYPE")
    public Integer getType() {
	return type;
    }

    public void setType(Integer type) {
	this.type = type;
	this.trainingCourse.setType(type);
    }

    @Basic
    @XmlTransient
    @Column(name = "QUALIFICATION_MINOR_SPEC_ID")
    public Long getQualificationMinorSpecId() {
	return qualificationMinorSpecId;
    }

    public void setQualificationMinorSpecId(Long qualificationMinorSpecId) {
	this.qualificationMinorSpecId = qualificationMinorSpecId;
	this.trainingCourse.setQualificationMinorSpecId(qualificationMinorSpecId);
    }

    @Basic
    @Column(name = "QUALIFICATION_MINOR_SPEC_DESC")
    public String getQualificationMinorSpecDesc() {
	return qualificationMinorSpecDesc;
    }

    public void setQualificationMinorSpecDesc(String qualificationMinorSpecDesc) {
	this.qualificationMinorSpecDesc = qualificationMinorSpecDesc;
    }

    @Basic
    @XmlTransient
    @Column(name = "QUALIFICATION_MAJOR_SPEC_ID")
    public Long getQualificationMajorSpecId() {
	return qualificationMajorSpecId;
    }

    public void setQualificationMajorSpecId(Long qualificationMajorSpecId) {
	this.qualificationMajorSpecId = qualificationMajorSpecId;
    }

    @Basic
    @Column(name = "QUALIFICATION_MAJOR_SPEC_DESC")
    public String getQualificationMajorSpecDesc() {
	return qualificationMajorSpecDesc;
    }

    public void setQualificationMajorSpecDesc(String qualificationMajorSpecDesc) {
	this.qualificationMajorSpecDesc = qualificationMajorSpecDesc;
    }

    @Basic
    @XmlTransient
    @Column(name = "CANDIDATES_MIN")
    public Integer getCandidatesMin() {
	return candidatesMin;
    }

    public void setCandidatesMin(Integer candidatesMin) {
	this.candidatesMin = candidatesMin;
	this.trainingCourse.setCandidatesMin(candidatesMin);
    }

    @Basic
    @XmlTransient
    @Column(name = "CANDIDATES_MAX")
    public Integer getCandidatesMax() {
	return candidatesMax;
    }

    public void setCandidatesMax(Integer candidatesMax) {
	this.candidatesMax = candidatesMax;
	this.trainingCourse.setCandidatesMax(candidatesMax);
    }

    @Basic
    @XmlTransient
    @Column(name = "WEEKS_COUNT")
    public Integer getWeeksCount() {
	return weeksCount;
    }

    public void setWeeksCount(Integer weeksCount) {
	this.weeksCount = weeksCount;
	this.trainingCourse.setWeeksCount(weeksCount);
    }

    @Basic
    @XmlTransient
    @Column(name = "EXCLUSIVE_FLAG")
    public Integer getExclusiveFlag() {
	return exclusiveFlag;
    }

    public void setExclusiveFlag(Integer exclusiveFlag) {
	this.exclusiveFlag = exclusiveFlag;

	if (this.exclusiveFlag == null || this.exclusiveFlag == FlagsEnum.OFF.getCode())
	    this.exclusiveFlagBoolean = false;
	else
	    this.exclusiveFlagBoolean = true;

	trainingCourse.setExclusiveFlag(exclusiveFlag);
    }

    @Transient
    @XmlTransient
    public Boolean getExclusiveFlagBoolean() {
	return exclusiveFlagBoolean;
    }

    public void setExclusiveFlagBoolean(Boolean exclusiveFlagBoolean) {
	this.exclusiveFlagBoolean = exclusiveFlagBoolean;

	if (this.exclusiveFlagBoolean == null || !this.exclusiveFlagBoolean) {
	    this.exclusiveFlag = FlagsEnum.OFF.getCode();
	} else {
	    this.exclusiveFlag = FlagsEnum.ON.getCode();
	}

	trainingCourse.setExclusiveFlag(exclusiveFlag);
    }

    @Basic
    @XmlTransient
    @Column(name = "PRERQUISITES")
    public String getPrerquisites() {
	return prerquisites;
    }

    public void setPrerquisites(String prerquisites) {
	this.prerquisites = prerquisites;
	this.trainingCourse.setPrerquisites(prerquisites);
    }

    @Basic
    @XmlTransient
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
	this.trainingCourse.setCategoryId(categoryId);
    }

    @Basic
    @XmlTransient
    @Column(name = "FROM_RANK_ID")
    public Long getFromRankId() {
	return fromRankId;
    }

    public void setFromRankId(Long fromRankId) {
	this.fromRankId = fromRankId;
	this.trainingCourse.setFromRankId(fromRankId);
    }

    @Basic
    @XmlTransient
    @Column(name = "FROM_RANK_DESC")
    public String getFromRankDesc() {
	return fromRankDesc;
    }

    public void setFromRankDesc(String fromRankDesc) {
	this.fromRankDesc = fromRankDesc;
    }

    @Basic
    @XmlTransient
    @Column(name = "TO_RANK_ID")
    public Long getToRankId() {
	return toRankId;
    }

    public void setToRankId(Long toRankId) {
	this.toRankId = toRankId;
	this.trainingCourse.setToRankId(toRankId);
    }

    @Basic
    @XmlTransient
    @Column(name = "TO_RANK_DESC")
    public String getToRankDesc() {
	return toRankDesc;
    }

    public void setToRankDesc(String toRankDesc) {
	this.toRankDesc = toRankDesc;
    }

    @Basic
    @XmlTransient
    @Column(name = "GENERAL_SPECIALIZATION")
    public Integer getGeneralSpecialization() {
	return generalSpecialization;
    }

    public void setGeneralSpecialization(Integer generalSpecialization) {
	this.generalSpecialization = generalSpecialization;
	this.trainingCourse.setGeneralSpecialization(generalSpecialization);
    }

    @Basic
    @XmlTransient
    @Column(name = "MINOR_SPECIALIZATION_IDS")
    public String getMinorSpecializationIds() {
	return minorSpecializationIds;
    }

    public void setMinorSpecializationIds(String minorSpecializationIds) {
	this.minorSpecializationIds = minorSpecializationIds;
	this.trainingCourse.setMinorSpecializationIds(minorSpecializationIds);
    }

    @Basic
    @XmlTransient
    @Column(name = "JOB_GENERAL_TYPE")
    public Integer getJobGeneralType() {
	return jobGeneralType;
    }

    public void setJobGeneralType(Integer jobGeneralType) {
	this.jobGeneralType = jobGeneralType;
	this.trainingCourse.setJobGeneralType(jobGeneralType);
    }

    @Basic
    @XmlTransient
    @Column(name = "QUALIFICATION_LEVEL_IDS")
    public String getQualificationLevelIds() {
	return qualificationLevelIds;
    }

    public void setQualificationLevelIds(String qualificationLevelIds) {
	this.qualificationLevelIds = qualificationLevelIds;
	this.trainingCourse.setQualificationLevelIds(qualificationLevelIds);
    }

    @Basic
    @XmlTransient
    @Column(name = "PROMOTION_WITNIN_MONTHS")
    public Integer getPromotionWitninMonths() {
	return promotionWitninMonths;
    }

    public void setPromotionWitninMonths(Integer promotionWitninMonths) {
	this.promotionWitninMonths = promotionWitninMonths;
	this.trainingCourse.setPromotionWitninMonths(promotionWitninMonths);
    }

    @Basic
    @XmlTransient
    @Column(name = "SYLLABUS_ATTACHMENTS")
    public String getSyllabusAttachments() {
	return syllabusAttachments;
    }

    public void setSyllabusAttachments(String syllabusAttachments) {
	this.trainingCourse.setSyllabusAttachments(syllabusAttachments);
	this.syllabusAttachments = syllabusAttachments;
    }

    @Basic
    @XmlTransient
    @Column(name = "SYLLABUS_ATTACHMENTS_HISTORY")
    public String getSyllabusAttachmentsHistory() {
	return syllabusAttachmentsHistory;
    }

    public void setSyllabusAttachmentsHistory(String syllabusAttachmentsHistory) {
	this.trainingCourse.setSyllabusAttachmentsHistory(syllabusAttachmentsHistory);
	this.syllabusAttachmentsHistory = syllabusAttachmentsHistory;
    }

    @Basic
    @XmlTransient
    @Column(name = "RANKING_FLAG")
    public Integer getRankingFlag() {
	return rankingFlag;
    }

    public void setRankingFlag(Integer rankingFlag) {
	this.rankingFlag = rankingFlag;

	if (this.rankingFlag == null || this.rankingFlag == FlagsEnum.OFF.getCode())
	    this.rankingFlagBoolean = false;
	else
	    this.rankingFlagBoolean = true;

	this.trainingCourse.setRankingFlag(rankingFlag);
    }

    @Basic
    @XmlTransient
    @Column(name = "ELECTRONIC_CERTIFICATE_FLAG")
    public Integer getElectronicCertificateFlag() {
	return electronicCertificateFlag;
    }

    public void setElectronicCertificateFlag(Integer electronicCertificateFlag) {
	this.electronicCertificateFlag = electronicCertificateFlag;
	if (this.electronicCertificateFlag == null || this.electronicCertificateFlag == FlagsEnum.OFF.getCode())
	    this.electronicCertificateBoolean = false;
	else
	    this.electronicCertificateBoolean = true;

	this.trainingCourse.setElectronicCertificateFlag(electronicCertificateFlag);
    }

    @Transient
    @XmlTransient
    public boolean isRankingFlagBoolean() {
	return rankingFlagBoolean;
    }

    public void setRankingFlagBoolean(boolean rankingFlagBoolean) {
	this.rankingFlagBoolean = rankingFlagBoolean;

	if (this.rankingFlagBoolean == null || !this.rankingFlagBoolean) {
	    this.rankingFlag = FlagsEnum.OFF.getCode();
	} else {
	    this.rankingFlag = FlagsEnum.ON.getCode();
	}

	this.trainingCourse.setRankingFlag(rankingFlag);
    }

    @Transient
    @XmlTransient
    public boolean isElectronicCertificateBoolean() {
	if (electronicCertificateBoolean == null)
	    return false;
	else
	    return electronicCertificateBoolean;
    }

    public void setElectronicCertificateBoolean(boolean electronicCertificateBoolean) {
	this.electronicCertificateBoolean = electronicCertificateBoolean;
	if (this.electronicCertificateBoolean == null || !this.electronicCertificateBoolean) {
	    this.electronicCertificateFlag = FlagsEnum.OFF.getCode();
	} else {
	    this.electronicCertificateFlag = FlagsEnum.ON.getCode();
	}

	this.trainingCourse.setElectronicCertificateFlag(electronicCertificateFlag);
    }

    @Transient
    @XmlTransient
    public TrainingCourse getTrainingCourse() {
	return trainingCourse;
    }

    public void setTrainingCourse(TrainingCourse trainingCourse) {
	this.trainingCourse = trainingCourse;
    }

}
