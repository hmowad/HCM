package com.code.dal.orm.hcm.trainings;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "hcm_trainingYear_searchTrainingYears",
		query = "select y from TrainingYear y " +
			" where (:P_YEAR_ID = -1 or y.id = :P_YEAR_ID) "
			+ "and (:P_EXCLUDED_YEAR_ID = -1 or y.id != :P_EXCLUDED_YEAR_ID) " +
			" and (:P_STATUS_IDS_FLAG = -1 or y.status in (:P_STATUS_IDS )) " +
			" and (:P_START_YEAR = '-1' or :P_START_YEAR = to_char(y.startDate, 'YYYY')) " +
			" order by y.startDate"),

	@NamedQuery(name = "hcm_trainingYear_countNumberOfSemestersInTrainingYear",
		query = "select count(y.id) from TrainingYear y " +
			" where (:P_RELATED_YEAR_ID = -1 or y.relatedYearId = :P_RELATED_YEAR_ID) ")

})
@Entity
@Table(name = "HCM_TRN_YEARS")
public class TrainingYear extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity {

    private Long id;
    private String name;
    private Date startDate;
    private String startDateString;
    private Date endDate;
    private String endDateString;
    private Date needsStartDate;
    private String needsStartDateString;
    private Date needsEndDate;
    private String needsEndDateString;
    private Integer status;
    private String decisionNumber;
    private Date decisioinDate;
    private Long originalDecisionApprovedId;
    private Long decisionApprovedId;
    private Integer eFlag;
    private Integer migFlag;
    private Long relatedYearId;
    private String semesterName;

    @SequenceGenerator(name = "HCMTrainingPlanSeq",
	    sequenceName = "HCM_TRN_PLAN_SEQ")
    @Id
    @GeneratedValue(generator = "HCMTrainingPlanSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(
	    Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
	return name;
    }

    public void setName(
	    String name) {
	this.name = name;
    }

    @Basic
    @Column(name = "START_DATE")
    public Date getStartDate() {
	return startDate;
    }

    public void setStartDate(
	    Date startDate) {
	this.startDate = startDate;
	this.startDateString = HijriDateService.getHijriDateString(startDate);
    }

    @Transient
    public String getStartDateString() {
	return startDateString;
    }

    public void setStartDateString(String startDateString) {
	this.startDateString = startDateString;
	this.startDate = HijriDateService.getHijriDate(startDateString);
    }

    @Basic
    @Column(name = "END_DATE")
    public Date getEndDate() {
	return endDate;
    }

    public void setEndDate(
	    Date endDate) {
	this.endDate = endDate;
	this.endDateString = HijriDateService.getHijriDateString(endDate);
    }

    @Transient
    public String getEndDateString() {
	return endDateString;
    }

    public void setEndDateString(String endDateString) {
	this.endDateString = startDateString;
	this.endDate = HijriDateService.getHijriDate(endDateString);
    }

    @Basic
    @Column(name = "NEEDS_START_DATE")
    public Date getNeedsStartDate() {
	return needsStartDate;
    }

    public void setNeedsStartDate(
	    Date needsStartDate) {
	this.needsStartDate = needsStartDate;
	this.needsStartDateString = HijriDateService.getHijriDateString(needsStartDate);
    }

    @Transient
    public String getNeedsStartDateString() {
	return needsStartDateString;
    }

    public void setNeedsStartDateString(String needsStartDateString) {
	this.needsStartDateString = needsStartDateString;
	this.needsStartDate = HijriDateService.getHijriDate(needsStartDateString);
    }

    @Basic
    @Column(name = "NEEDS_END_DATE")
    public Date getNeedsEndDate() {
	return needsEndDate;
    }

    public void setNeedsEndDate(
	    Date needsEndDate) {
	this.needsEndDate = needsEndDate;
	this.needsEndDateString = HijriDateService.getHijriDateString(needsEndDate);
    }

    @Transient
    public String getNeedsEndDateString() {
	return needsEndDateString;
    }

    public void setNeedsEndDateString(String needsEndDateString) {
	this.needsEndDateString = needsEndDateString;
	this.needsEndDate = HijriDateService.getHijriDate(needsEndDateString);
    }

    @Basic
    @Column(name = "STATUS")
    public Integer getStatus() {
	return status;
    }

    public void setStatus(
	    Integer status) {
	this.status = status;
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(
	    String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    @Basic
    @Column(name = "DECISION_DATE")
    public Date getDecisioinDate() {
	return decisioinDate;
    }

    public void setDecisioinDate(
	    Date decisioinDate) {
	this.decisioinDate = decisioinDate;
    }

    @Basic
    @Column(name = "ORIGINAL_DECISION_APPROVED_ID")
    public Long getOriginalDecisionApprovedId() {
	return originalDecisionApprovedId;
    }

    public void setOriginalDecisionApprovedId(
	    Long originalDecisionApprovedId) {
	this.originalDecisionApprovedId = originalDecisionApprovedId;
    }

    @Basic
    @Column(name = "DECISION_APPROVED_ID")
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    public void setDecisionApprovedId(
	    Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
    }

    @Basic
    @Column(name = "EFLAG")
    public Integer getEFlag() {
	return eFlag;
    }

    public void setEFlag(
	    Integer eFlag) {
	this.eFlag = eFlag;
    }

    @Basic
    @Column(name = "MIG_FLAG")
    public Integer getMigFlag() {
	return migFlag;
    }

    public void setMigFlag(
	    Integer migFlag) {
	this.migFlag = migFlag;
    }

    @Basic
    @Column(name = "RELATED_YEAR_ID")
    public Long getRelatedYearId() {
	return relatedYearId;
    }

    public void setRelatedYearId(Long relatedYearId) {
	this.relatedYearId = relatedYearId;
    }

    @Basic
    @Column(name = "SEMESTER_NAME")
    public String getSemesterName() {
	return semesterName;
    }

    public void setSemesterName(String semesterName) {
	this.semesterName = semesterName;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "name:" + name + AUDIT_SEPARATOR +
		"startDate:" + startDate + AUDIT_SEPARATOR +
		"endDate:" + endDate + AUDIT_SEPARATOR +
		"needsStartDate:" + needsStartDate + AUDIT_SEPARATOR +
		"needsEndDate:" + needsEndDate + AUDIT_SEPARATOR +
		"status:" + status + AUDIT_SEPARATOR +
		"decisionNumber:" + decisionNumber + AUDIT_SEPARATOR +
		"decisioinDate:" + decisioinDate + AUDIT_SEPARATOR +
		"originalDecisionApprovedId:" + originalDecisionApprovedId + AUDIT_SEPARATOR +
		"decisionApprovedId:" + decisionApprovedId + AUDIT_SEPARATOR +
		"relatedYearId:" + relatedYearId + AUDIT_SEPARATOR +
		"semesterName:" + semesterName + AUDIT_SEPARATOR +
		"eFlag:" + eFlag + AUDIT_SEPARATOR +
		"migFlag:" + migFlag + AUDIT_SEPARATOR;
    }
}
