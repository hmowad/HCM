package com.code.dal.orm.hcm.employees;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@Entity
@Table(name = "HCM_PRS_EMPS_QUALIFICATIONS")
public class EmployeeQualifications extends AuditEntity implements Serializable, InsertableAuditEntity, UpdatableAuditEntity {
    private Long employeeId;
    private Long recQualLevelId;
    private Double recQualPercentage;
    private Integer recGraduationYear;
    private Long recGraduationPlaceDetailId;
    private Long recQualMinorSpecId;
    private String recQualAttachments;
    private Long curQualLevelId;
    private Double curQualPercentage;
    private Integer curGraduationYear;
    private Long curGraduationPlaceDetailId;
    private Long curQualMinorSpecId;
    private String curQualAttachments;

    @Id
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    @Basic
    @Column(name = "REC_QUAL_LEVEL_ID")
    public Long getRecQualLevelId() {
	return recQualLevelId;
    }

    public void setRecQualLevelId(Long recQualLevelId) {
	this.recQualLevelId = recQualLevelId;
    }

    @Basic
    @Column(name = "REC_QUAL_PERCENTAGE")
    public Double getRecQualPercentage() {
	return recQualPercentage;
    }

    public void setRecQualPercentage(Double recQualPercentage) {
	this.recQualPercentage = recQualPercentage;
    }

    @Basic
    @Column(name = "REC_GRADUATION_YEAR")
    public Integer getRecGraduationYear() {
	return recGraduationYear;
    }

    public void setRecGraduationYear(Integer recGraduationYear) {
	this.recGraduationYear = recGraduationYear;
    }

    @Basic
    @Column(name = "REC_GRADUATION_PLACE_DTL_ID")
    public Long getRecGraduationPlaceDetailId() {
	return recGraduationPlaceDetailId;
    }

    public void setRecGraduationPlaceDetailId(Long recGraduationPlaceDetailId) {
	this.recGraduationPlaceDetailId = recGraduationPlaceDetailId;
    }

    @Basic
    @Column(name = "REC_QUAL_MINOR_SPEC_ID")
    public Long getRecQualMinorSpecId() {
	return recQualMinorSpecId;
    }

    public void setRecQualMinorSpecId(Long recQualMinorSpecId) {
	this.recQualMinorSpecId = recQualMinorSpecId;
    }

    @Basic
    @Column(name = "REC_QUAL_ATTACHMENTS")
    public String getRecQualAttachments() {
	return recQualAttachments;
    }

    public void setRecQualAttachments(String recQualAttachments) {
	this.recQualAttachments = recQualAttachments;
    }

    @Basic
    @Column(name = "CUR_QUAL_LEVEL_ID")
    public Long getCurQualLevelId() {
	return curQualLevelId;
    }

    public void setCurQualLevelId(Long curQualLevelId) {
	this.curQualLevelId = curQualLevelId;
    }

    @Basic
    @Column(name = "CUR_QUAL_PERCENTAGE")
    public Double getCurQualPercentage() {
	return curQualPercentage;
    }

    public void setCurQualPercentage(Double curQualPercentage) {
	this.curQualPercentage = curQualPercentage;
    }

    @Basic
    @Column(name = "CUR_GRADUATION_YEAR")
    public Integer getCurGraduationYear() {
	return curGraduationYear;
    }

    public void setCurGraduationYear(Integer curGraduationYear) {
	this.curGraduationYear = curGraduationYear;
    }

    @Basic
    @Column(name = "CUR_GRADUATION_PLACE_DTL_ID")
    public Long getCurGraduationPlaceDetailId() {
	return curGraduationPlaceDetailId;
    }

    public void setCurGraduationPlaceDetailId(Long curGraduationPlaceDetailId) {
	this.curGraduationPlaceDetailId = curGraduationPlaceDetailId;
    }

    @Basic
    @Column(name = "CUR_QUAL_MINOR_SPEC_ID")
    public Long getCurQualMinorSpecId() {
	return curQualMinorSpecId;
    }

    public void setCurQualMinorSpecId(Long curQualMinorSpecId) {
	this.curQualMinorSpecId = curQualMinorSpecId;
    }

    @Basic
    @Column(name = "CUR_QUAL_ATTACHMENTS")
    public String getCurQualAttachments() {
	return curQualAttachments;
    }

    public void setCurQualAttachments(String curQualAttachments) {
	this.curQualAttachments = curQualAttachments;
    }

    @Override
    public Long calculateContentId() {
	return employeeId;
    }

    @Override
    public String calculateContent() {
	return "recQualLevelId:" + recQualLevelId + AUDIT_SEPARATOR +
		"recQualPercentage:" + recQualPercentage + AUDIT_SEPARATOR +
		"recGraduationYear:" + recGraduationYear + AUDIT_SEPARATOR +
		"recGraduationPlaceDetailId:" + recGraduationPlaceDetailId + AUDIT_SEPARATOR +
		"recQualMinorSpecId:" + recQualMinorSpecId + AUDIT_SEPARATOR +
		"recQualAttachments:" + recQualAttachments + AUDIT_SEPARATOR +
		"curQualLevelId:" + curQualLevelId + AUDIT_SEPARATOR +
		"curQualPercentage:" + curQualPercentage + AUDIT_SEPARATOR +
		"curGraduationYear:" + curGraduationYear + AUDIT_SEPARATOR +
		"curGraduationPlaceDetailId:" + curGraduationPlaceDetailId + AUDIT_SEPARATOR +
		"curQualMinorSpecId:" + curQualMinorSpecId + AUDIT_SEPARATOR +
		"curQualAttachments:" + curQualAttachments + AUDIT_SEPARATOR;
    }
}
