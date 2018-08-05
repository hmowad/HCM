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

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_trainingCourseEventDecisionEmployee_getLastTrnCourseEventDecisionEmployees",
		query = " select cde from TrainingCourseEventDecisionEmployee cde, TrainingCourseEventDecision cd where " +
			" ((cd.courseEventId = :P_COURSE_EVENT_ID) " +
			" and (cd.id = cde.courseEventDecisionId) " +
			" and (cd.decisionDate = (select max(c.decisionDate) from TrainingCourseEventDecision c where (c.courseEventId = :P_COURSE_EVENT_ID)))) "),
	@NamedQuery(name = "hcm_trainingCourseEventDecisionEmployee_getTrnCourseEventDecisionExternalEmployees",
		query = " select cde from TrainingCourseEventDecisionEmployee cde where " +
			" (cde.courseEventDecisionId =:P_COURSE_EVENT_DECISION_ID and cde.trainingTransactionId is null)")

})
@Entity
@Table(name = "HCM_TRN_CRS_EVENTS_DECS_EMPS")
public class TrainingCourseEventDecisionEmployee extends BaseEntity {
    private Long id;
    private Long courseEventDecisionId;
    private Long trainingTransactionId;
    private Long transEmpCategoryId;
    private String transEmpJobCode;
    private String transEmpJobName;
    private Long transEmpRankId;
    private String transEmpRankDesc;
    private String transEmpUnitFullName;
    private String externalEmployeeName;
    private String externalEmployeePartyDesc;

    @SequenceGenerator(name = "HCMTrainingPlanSeq",
	    sequenceName = "HCM_TRN_PLAN_SEQ")
    @Id
    @GeneratedValue(generator = "HCMTrainingPlanSeq")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "COURSE_EVENT_DECISION_ID")
    public Long getCourseEventDecisionId() {
	return courseEventDecisionId;
    }

    public void setCourseEventDecisionId(Long courseEventDecisionId) {
	this.courseEventDecisionId = courseEventDecisionId;
    }

    @Basic
    @Column(name = "TRAINING_TRANSACTION_ID")
    public Long getTrainingTransactionId() {
	return trainingTransactionId;
    }

    public void setTrainingTransactionId(Long trainingTransactionId) {
	this.trainingTransactionId = trainingTransactionId;
    }

    @Basic
    @Column(name = "TRANS_EMP_CATEGORY_ID")
    public Long getTransEmpCategoryId() {
	return transEmpCategoryId;
    }

    public void setTransEmpCategoryId(Long transEmpCategoryId) {
	this.transEmpCategoryId = transEmpCategoryId;
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_CODE")
    public String getTransEmpJobCode() {
	return transEmpJobCode;
    }

    public void setTransEmpJobCode(String transEmpJobCode) {
	this.transEmpJobCode = transEmpJobCode;
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_NAME")
    public String getTransEmpJobName() {
	return transEmpJobName;
    }

    public void setTransEmpJobName(String transEmpJobName) {
	this.transEmpJobName = transEmpJobName;
    }

    @Basic
    @Column(name = "TRANS_EMP_RANK_ID")
    public Long getTransEmpRankId() {
	return transEmpRankId;
    }

    public void setTransEmpRankId(Long transEmpRankId) {
	this.transEmpRankId = transEmpRankId;
    }

    @Basic
    @Column(name = "TRANS_EMP_RANK_DESC")
    public String getTransEmpRankDesc() {
	return transEmpRankDesc;
    }

    public void setTransEmpRankDesc(String transEmpRankDesc) {
	this.transEmpRankDesc = transEmpRankDesc;
    }

    @Basic
    @Column(name = "TRANS_EMP_UNIT_FULL_NAME")
    public String getTransEmpUnitFullName() {
	return transEmpUnitFullName;
    }

    public void setTransEmpUnitFullName(String transEmpUnitFullName) {
	this.transEmpUnitFullName = transEmpUnitFullName;
    }

    @Basic
    @Column(name = "EXT_EMPLOYEE_NAME")
    public String getExternalEmployeeName() {
	return externalEmployeeName;
    }

    public void setExternalEmployeeName(String externalEmployeeName) {
	this.externalEmployeeName = externalEmployeeName;
    }

    @Basic
    @Column(name = "EXT_EMPLOYEE_PARTY_DESC")
    public String getExternalEmployeePartyDesc() {
	return externalEmployeePartyDesc;
    }

    public void setExternalEmployeePartyDesc(String externalEmployeePartyDesc) {
	this.externalEmployeePartyDesc = externalEmployeePartyDesc;
    }
}
