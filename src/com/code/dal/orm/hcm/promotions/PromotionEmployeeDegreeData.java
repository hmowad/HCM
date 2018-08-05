package com.code.dal.orm.hcm.promotions;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_PromotionEmployeeDegreeData_getPromotionEmployeeDegreeDataByEmpsIds",
		query = " select pd from PromotionEmployeeDegreeData pd " +
			" where pd.empId in (:P_EMPS_IDS) " +
			" and pd.fromRankId = :P_FROM_RANK_ID " +
			" order by pd.empId ")
})

@Entity
@IdClass(PromotionEmployeeDegreeDataId.class)
@Table(name = "HCM_VW_PRM_EMPLOYEES_DEGREES")
public class PromotionEmployeeDegreeData extends BaseEntity {

    private Long empId;
    private Double qualificationDegree;
    private Double seniorityDegree;
    private Double fieldServiceDegree;
    private Integer fileDegree;
    private Double trainingDegree;
    private Integer leaderDegree;
    private Integer examDegree;
    private Integer recruitmentCourseGraduationEvaluation;
    private Integer recruitmentCourseGraduationOrder;
    private Double recruitmentCourseGraduationDegree;
    private String fromRankId;

    @Id
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    @Id
    @Column(name = "FROM_RANK_ID")
    public String getFromRankId() {
	return fromRankId;
    }

    public void setFromRankId(String fromRankId) {
	this.fromRankId = fromRankId;
    }

    @Basic
    @Column(name = "QUALIFICATION_DEGREE")
    public Double getQualificationDegree() {
	return qualificationDegree;
    }

    public void setQualificationDegree(Double qualificationDegree) {
	this.qualificationDegree = qualificationDegree;
    }

    @Basic
    @Column(name = "SENIORITY_DEGREE")
    public Double getSeniorityDegree() {
	return seniorityDegree;
    }

    public void setSeniorityDegree(Double seniorityDegree) {
	this.seniorityDegree = seniorityDegree;
    }

    @Basic
    @Column(name = "FIELD_SERVICE_DEGREE")
    public Double getFieldServiceDegree() {
	return fieldServiceDegree;
    }

    public void setFieldServiceDegree(Double fieldServiceDegree) {
	this.fieldServiceDegree = fieldServiceDegree;
    }

    @Basic
    @Column(name = "FILE_DEGREE")
    public Integer getFileDegree() {
	return fileDegree;
    }

    public void setFileDegree(Integer fileDegree) {
	this.fileDegree = fileDegree;
    }

    @Basic
    @Column(name = "TRAINING_DEGREE")
    public Double getTrainingDegree() {
	return trainingDegree;
    }

    public void setTrainingDegree(Double trainingDegree) {
	this.trainingDegree = trainingDegree;
    }

    @Basic
    @Column(name = "LEADER_DEGREE")
    public Integer getLeaderDegree() {
	return leaderDegree;
    }

    public void setLeaderDegree(Integer leaderDegree) {
	this.leaderDegree = leaderDegree;
    }

    @Basic
    @Column(name = "EXAM_DEGREE")
    public Integer getExamDegree() {
	return examDegree;
    }

    public void setExamDegree(Integer examDegree) {
	this.examDegree = examDegree;
    }

    @Basic
    @Column(name = "REC_COURSE_GRADUATION_EVAL")
    public Integer getRecruitmentCourseGraduationEvaluation() {
	return recruitmentCourseGraduationEvaluation;
    }

    public void setRecruitmentCourseGraduationEvaluation(Integer recruitmentCourseGraduationEvaluation) {
	this.recruitmentCourseGraduationEvaluation = recruitmentCourseGraduationEvaluation;
    }

    @Basic
    @Column(name = "REC_COURSE_GRADUATION_ORDER")
    public Integer getRecruitmentCourseGraduationOrder() {
	return recruitmentCourseGraduationOrder;
    }

    public void setRecruitmentCourseGraduationOrder(Integer recruitmentCourseGraduationOrder) {
	this.recruitmentCourseGraduationOrder = recruitmentCourseGraduationOrder;
    }

    @Basic
    @Column(name = "REC_COURSE_GRADUATION_DEGREE")
    public Double getRecruitmentCourseGraduationDegree() {
	return recruitmentCourseGraduationDegree;
    }

    public void setRecruitmentCourseGraduationDegree(Double recruitmentCourseGraduationDegree) {
	this.recruitmentCourseGraduationDegree = recruitmentCourseGraduationDegree;
    }

}
