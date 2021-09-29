package com.code.dal.orm.hcm.trainings;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "hcm_trainingTransactionDetailData_searchTrainingTransactionsDetailData",
		query = "select td from TrainingTransactionDetailData td " +
			"where (:P_EMPLOYEE_ID =-1 or td.employeeId = :P_EMPLOYEE_ID) " +
			"AND (:P_TRAINING_TYPE_IDS_FLAG = -1 or td.trainingTypeId in (:P_TRAINING_TYPE_IDS )) " +
			"AND (:P_TRANSACTION_TYPE_IDS_FLAG = -1 or td.transactionTypeId in (:P_TRANSACTION_TYPE_IDS )) " +
			"AND (:P_STATUS_IDS_FLAG = -1 or td.status in (:P_STATUS_IDS )) " +
			"AND (:P_DECISION_NUMBER ='-1' or td.decisionNumber like :P_DECISION_NUMBER ) " +
			"AND (:P_TRAINING_TRANSACTION_ID =-1 or td.trainingTransactionId = :P_TRAINING_TRANSACTION_ID) " +
			"AND (:P_TRAINING_UNIT_ID = -1 or td.trainingUnitId = :P_TRAINING_UNIT_ID )" +
			"AND (:P_EMP_PHYSC_UNIT_HKEY ='-1' or td.employeePhysicalUnitHKey LIKE :P_EMP_PHYSC_UNIT_HKEY)" +
			"AND (:P_START_DATE_FROM_FLAG = -1 or td.actualStartDate >= to_date(:P_START_DATE_FROM, 'MI/MM/YYYY') ) " +
			"AND (:P_START_DATE_TO_FLAG = -1 or td.actualStartDate <= to_date(:P_START_DATE_TO, 'MI/MM/YYYY'))" +
			"AND (:P_MINISTRY_DECISION_NUMBER ='-1' or td.ministryDecisionNumber like :P_MINISTRY_DECISION_NUMBER ) " +
			"AND (:P_MINISTRY_DECISION_DATE_FLAG = -1 or td.ministryDecisionDate = to_date(:P_MINISTRY_DECISION_DATE, 'MI/MM/YYYY') ) " +
			" order by td.decisionDate,td.id DESC")

})
@Entity
@Table(name = "HCM_VW_TRN_TRANSACTIONS_DTLS")
public class TrainingTransactionDetailData extends BaseEntity {
    private Long id;
    private Long trainingTransactionId;
    private Long transactionTypeId;
    private Integer transactionTypeCode;
    private String transactionTypeDesc;
    private Date decisionDate;
    private String decisionDateString;
    private String decisionNumber;
    private Long decisionRegionId;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private Long transEmpCategoryId;
    private String transEmpJobCode;
    private String transEmpJobName;
    private String transEmpRankDesc;
    private String transEmpUnitFullName;
    private String directedToJobName;
    private String ministryDecisionNumber;
    private Date ministryDecisionDate;
    private String ministryDecisionDateString;
    private String ministryReportNumber;
    private Date ministryReportDate;
    private String ministryReportDateString;
    private Date studyStartDate;
    private String studyStartDateString;
    private Date studyEndDate;
    private String studyEndDateString;
    private Integer studyMonthsCount;
    private Integer studyDaysCount;
    private String attachments;
    private String reasons;
    private String internalCopies;
    private String externalCopies;
    private Long trainingTypeId;
    private Long trainingUnitId;
    private Long employeeId;
    private String employeeName;
    private String employeePhysicalUnitHKey;
    private Integer status;
    private String courseName;
    private String trainingUnitName;
    private Date actualStartDate;
    private TrainingTransactionDetail trainingTransactionDetail;

    public TrainingTransactionDetailData() {
	trainingTransactionDetail = new TrainingTransactionDetail();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.trainingTransactionDetail.setId(id);
    }

    @Basic
    @Column(name = "TRAINING_TRANSACTION_ID")
    public Long getTrainingTransactionId() {
	return trainingTransactionId;
    }

    public void setTrainingTransactionId(Long trainingTransactionId) {
	this.trainingTransactionId = trainingTransactionId;
	this.trainingTransactionDetail.setTrainingTransactionId(trainingTransactionId);
    }

    @Basic
    @Column(name = "TRANSACTION_TYPE_ID")
    public Long getTransactionTypeId() {
	return transactionTypeId;
    }

    public void setTransactionTypeId(Long transactionTypeId) {
	this.transactionTypeId = transactionTypeId;
	this.trainingTransactionDetail.setTransactionTypeId(transactionTypeId);
    }

    @Basic
    @Column(name = "TRANSACTION_TYPE_CODE")
    public Integer getTransactionTypeCode() {
	return transactionTypeCode;
    }

    public void setTransactionTypeCode(Integer transactionTypeCode) {
	this.transactionTypeCode = transactionTypeCode;
    }

    @Basic
    @Column(name = "TRANSACTION_TYPE_DESC")
    public String getTransactionTypeDesc() {
	return transactionTypeDesc;
    }

    public void setTransactionTypeDesc(String transactionTypeDesc) {
	this.transactionTypeDesc = transactionTypeDesc;
    }

    @Basic
    @Column(name = "DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
	this.decisionDateString = HijriDateService.getHijriDateString(decisionDate);
	this.trainingTransactionDetail.setDecisionDate(decisionDate);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
	this.trainingTransactionDetail.setDecisionDate(decisionDate);
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
	this.trainingTransactionDetail.setDecisionNumber(decisionNumber);
    }

    @Basic
    @Column(name = "DECISION_REGION_ID")
    public Long getDecisionRegionId() {
	return decisionRegionId;
    }

    public void setDecisionRegionId(Long decisionRegionId) {
	this.decisionRegionId = decisionRegionId;
	this.trainingTransactionDetail.setDecisionRegionId(decisionRegionId);
    }

    @Basic
    @Column(name = "DECISION_APPROVED_ID")
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    public void setDecisionApprovedId(Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
	this.trainingTransactionDetail.setDecisionApprovedId(decisionApprovedId);
    }

    @Basic
    @Column(name = "ORIGINAL_DECISION_APPROVED_ID")
    public Long getOriginalDecisionApprovedId() {
	return originalDecisionApprovedId;
    }

    public void setOriginalDecisionApprovedId(Long originalDecisionApprovedId) {
	this.originalDecisionApprovedId = originalDecisionApprovedId;
	this.trainingTransactionDetail.setOriginalDecisionApprovedId(originalDecisionApprovedId);
    }

    @Basic
    @Column(name = "TRANS_EMP_CATEGORY_ID")
    public Long getTransEmpCategoryId() {
	return transEmpCategoryId;
    }

    public void setTransEmpCategoryId(Long transEmpCategoryId) {
	this.transEmpCategoryId = transEmpCategoryId;
	this.trainingTransactionDetail.setTransEmpCategoryId(transEmpCategoryId);
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_CODE")
    public String getTransEmpJobCode() {
	return transEmpJobCode;
    }

    public void setTransEmpJobCode(String transEmpJobCode) {
	this.transEmpJobCode = transEmpJobCode;
	this.trainingTransactionDetail.setTransEmpJobCode(transEmpJobCode);
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_NAME")
    public String getTransEmpJobName() {
	return transEmpJobName;
    }

    public void setTransEmpJobName(String transEmpJobName) {
	this.transEmpJobName = transEmpJobName;
	this.trainingTransactionDetail.setTransEmpJobName(transEmpJobName);
    }

    @Basic
    @Column(name = "TRANS_EMP_RANK_DESC")
    public String getTransEmpRankDesc() {
	return transEmpRankDesc;
    }

    public void setTransEmpRankDesc(String transEmpRankDesc) {
	this.transEmpRankDesc = transEmpRankDesc;
	this.trainingTransactionDetail.setTransEmpRankDesc(transEmpRankDesc);
    }

    @Basic
    @Column(name = "TRANS_EMP_UNIT_FULL_NAME")
    public String getTransEmpUnitFullName() {
	return transEmpUnitFullName;
    }

    public void setTransEmpUnitFullName(String transEmpUnitFullName) {
	this.transEmpUnitFullName = transEmpUnitFullName;
	this.trainingTransactionDetail.setTransEmpUnitFullName(transEmpUnitFullName);
    }

    @Basic
    @Column(name = "DIRECTED_TO_JOB_NAME")
    public String getDirectedToJobName() {
	return directedToJobName;
    }

    public void setDirectedToJobName(String directedToJobName) {
	this.directedToJobName = directedToJobName;
	this.trainingTransactionDetail.setDirectedToJobName(directedToJobName);
    }

    @Basic
    @Column(name = "MINISTRY_DECISION_NUMBER")
    public String getMinistryDecisionNumber() {
	return ministryDecisionNumber;
    }

    public void setMinistryDecisionNumber(String ministryDecisionNumber) {
	this.ministryDecisionNumber = ministryDecisionNumber;
	this.trainingTransactionDetail.setMinistryDecisionNumber(ministryDecisionNumber);
    }

    @Basic
    @Column(name = "MINISTRY_DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMinistryDecisionDate() {
	return ministryDecisionDate;
    }

    public void setMinistryDecisionDate(Date ministryDecisionDate) {
	this.ministryDecisionDate = ministryDecisionDate;
	this.ministryDecisionDateString = HijriDateService.getHijriDateString(ministryDecisionDate);
	this.trainingTransactionDetail.setMinistryDecisionDate(ministryDecisionDate);
    }

    @Transient
    public String getMinistryDecisionDateString() {
	return ministryDecisionDateString;
    }

    public void setMinistryDecisionDateString(String ministryDecisionDateString) {
	this.ministryDecisionDateString = ministryDecisionDateString;
	this.ministryDecisionDate = HijriDateService.getHijriDate(ministryDecisionDateString);
	this.trainingTransactionDetail.setMinistryDecisionDate(ministryDecisionDate);
    }

    @Basic
    @Column(name = "MINISTRY_REPORT_NUMBER")
    public String getMinistryReportNumber() {
	return ministryReportNumber;
    }

    public void setMinistryReportNumber(String ministryReportNumber) {
	this.ministryReportNumber = ministryReportNumber;
	this.trainingTransactionDetail.setMinistryReportNumber(ministryReportNumber);
    }

    @Basic
    @Column(name = "MINISTRY_REPORT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMinistryReportDate() {
	return ministryReportDate;
    }

    public void setMinistryReportDate(Date ministryReportDate) {
	this.ministryReportDate = ministryReportDate;
	this.ministryReportDateString = HijriDateService.getHijriDateString(ministryReportDate);
	this.trainingTransactionDetail.setMinistryReportDate(ministryReportDate);
    }

    @Transient
    public String getMinistryReportDateString() {
	return ministryReportDateString;
    }

    public void setMinistryReportDateString(String ministryReportDateString) {
	this.ministryReportDateString = ministryReportDateString;
	this.ministryReportDate = HijriDateService.getHijriDate(ministryReportDateString);
	this.trainingTransactionDetail.setMinistryReportDate(ministryReportDate);
    }

    @Basic
    @Column(name = "STUDY_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStudyStartDate() {
	return studyStartDate;
    }

    public void setStudyStartDate(Date studyStartDate) {
	this.studyStartDate = studyStartDate;
	this.studyStartDateString = HijriDateService.getHijriDateString(studyStartDate);
	this.trainingTransactionDetail.setStudyStartDate(studyStartDate);
    }

    @Transient
    public String getStudyStartDateString() {
	return studyStartDateString;
    }

    public void setStudyStartDateString(String studyStartDateString) {
	this.studyStartDateString = studyStartDateString;
	this.studyStartDate = HijriDateService.getHijriDate(studyStartDateString);
	this.trainingTransactionDetail.setStudyStartDate(studyStartDate);
    }

    @Basic
    @Column(name = "STUDY_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStudyEndDate() {
	return studyEndDate;
    }

    public void setStudyEndDate(Date studyEndDate) {
	this.studyEndDate = studyEndDate;
	this.studyEndDateString = HijriDateService.getHijriDateString(studyEndDate);
	this.trainingTransactionDetail.setStudyEndDate(studyEndDate);
    }

    @Transient
    public String getStudyEndDateString() {
	return studyEndDateString;
    }

    public void setStudyEndDateString(String studyEndDateString) {
	this.studyEndDateString = studyEndDateString;
	this.studyEndDate = HijriDateService.getHijriDate(studyEndDateString);
	this.trainingTransactionDetail.setStudyEndDate(studyEndDate);
    }

    @Basic
    @Column(name = "STUDY_MONTHS_COUNT")
    public Integer getStudyMonthsCount() {
	return studyMonthsCount;
    }

    public void setStudyMonthsCount(Integer studyMonthsCount) {
	this.studyMonthsCount = studyMonthsCount;
	this.trainingTransactionDetail.setStudyMonthsCount(studyMonthsCount);
    }

    @Basic
    @Column(name = "STUDY_DAYS_COUNT")
    public Integer getStudyDaysCount() {
	return studyDaysCount;
    }

    public void setStudyDaysCount(Integer studyDaysCount) {
	this.studyDaysCount = studyDaysCount;
	this.trainingTransactionDetail.setStudyDaysCount(studyDaysCount);
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
	this.trainingTransactionDetail.setAttachments(attachments);
    }

    @Basic
    @Column(name = "REASONS")
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
	this.trainingTransactionDetail.setReasons(reasons);
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    public String getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	this.internalCopies = internalCopies;
	this.trainingTransactionDetail.setInternalCopies(internalCopies);
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
	this.trainingTransactionDetail.setExternalCopies(externalCopies);
    }

    @Transient
    public TrainingTransactionDetail getTrainingTransactionDetail() {
	return trainingTransactionDetail;
    }

    public void setTrainingTransactionDetail(TrainingTransactionDetail trainingTransactionDetail) {
	this.trainingTransactionDetail = trainingTransactionDetail;
    }

    @Basic
    @Column(name = "TRAINING_TYPE_ID")
    public Long getTrainingTypeId() {
	return trainingTypeId;
    }

    public void setTrainingTypeId(Long trainingTypeId) {
	this.trainingTypeId = trainingTypeId;
    }

    @Basic
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    @Basic
    @Column(name = "EMPLOYEE_NAME")
    public String getEmployeeName() {
	return employeeName;
    }

    public void setEmployeeName(String employeeName) {
	this.employeeName = employeeName;
    }

    @Basic
    @Column(name = "EMPLOYEE_PHYCL_UNIT_HKEY")
    public String getEmployeePhysicalUnitHKey() {
	return employeePhysicalUnitHKey;
    }

    public void setEmployeePhysicalUnitHKey(String employeePhysicalUnitHKey) {
	this.employeePhysicalUnitHKey = employeePhysicalUnitHKey;
    }

    @Basic
    @Column(name = "TRAINING_UNIT_ID")
    public Long getTrainingUnitId() {
	return trainingUnitId;
    }

    public void setTrainingUnitId(Long trainingUnitId) {
	this.trainingUnitId = trainingUnitId;
    }

    @Basic
    @Column(name = "STATUS")
    public Integer getStatus() {
	return status;
    }

    public void setStatus(Integer status) {
	this.status = status;
    }

    @Basic
    @Column(name = "TRAINING_UNIT_NAME")
    public String getTrainingUnitName() {
	return trainingUnitName;
    }

    public void setTrainingUnitName(String trainingUnitName) {
	this.trainingUnitName = trainingUnitName;
    }

    @Basic
    @Column(name = "COURSE_NAME")
    public String getCourseName() {
	return courseName;
    }

    public void setCourseName(String courseName) {
	this.courseName = courseName;
    }

    @Basic
    @Column(name = "ACTUAL_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getActualStartDate() {
	return actualStartDate;
    }

    public void setActualStartDate(Date actualStartDate) {
	this.actualStartDate = actualStartDate;
    }
}
