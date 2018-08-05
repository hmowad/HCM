package com.code.dal.orm.hcm.employees;

import java.io.Serializable;

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
	@NamedQuery(name = "hcm_employeeQualificationsData_getEmployeeQualificationsByEmpId",
		query = " from EmployeeQualificationsData eq where eq.employeeId = :P_EMP_ID "),

	@NamedQuery(name = "hcm_employeeQualificationsData_searchEmpQualficicationByGraduationPlace",
		query = " from EmployeeQualificationsData eq " +
			"where (:P_GRADUATION_PLACE_ID = -1 or eq.recGraduationPlaceId = :P_GRADUATION_PLACE_ID or eq.curGraduationPlaceId =:P_GRADUATION_PLACE_ID )" +
			" and (:P_GRADUATION_PLACE_DETAIL_ID = -1 or eq.recGraduationPlaceDetailId = :P_GRADUATION_PLACE_DETAIL_ID or eq.curGraduationPlaceDetailId = :P_GRADUATION_PLACE_DETAIL_ID )"),

	@NamedQuery(name = "hcm_employeeQualificationsData_searchEmpQualficicationByQualificationSpec",
		query = " from EmployeeQualificationsData eq " +
			"where (:P_QUAL_MAJOR_SPEC_ID = -1 or eq.recQualMajorSpecId = :P_QUAL_MAJOR_SPEC_ID or eq.curQualMajorSpecId =:P_QUAL_MAJOR_SPEC_ID )" +
			" and (:P_QUAL_MINOR_SPEC_ID = -1 or eq.recQualMinorSpecId = :P_QUAL_MINOR_SPEC_ID or eq.curQualMinorSpecId = :P_QUAL_MINOR_SPEC_ID )")
})
@Entity
@Table(name = "HCM_VW_PRS_EMPS_QUALIFICATIONS")
public class EmployeeQualificationsData extends BaseEntity implements Serializable {
    private Long employeeId;
    private Long recQualLevelId;
    private String recQualLevelDesc;
    private Double recQualPercentage;
    private Integer recGraduationYear;
    private Long recGraduationPlaceId;
    private String recGraduationPlaceDesc;
    private Long recGraduationPlaceDetailId;
    private String recGraduationPlaceDetailDesc;
    private Long recStudyCountryId;
    private String recStudyCountryName;
    private Long recQualMinorSpecId;
    private String recQualMinorSpecDesc;
    private Long recQualMajorSpecId;
    private String recQualMajorSpecDesc;
    private String recQualAttachments;
    private Long curQualLevelId;
    private String curQualLevelDesc;
    private Double curQualPercentage;
    private Integer curGraduationYear;
    private Long curGraduationPlaceId;
    private String curGraduationPlaceDesc;
    private Long curGraduationPlaceDetailId;
    private String curGraduationPlaceDetailDesc;
    private Long curStudyCountryId;
    private String curStudyCountryName;
    private Long curQualMinorSpecId;
    private String curQualMinorSpecDesc;
    private Long curQualMajorSpecId;
    private String curQualMajorSpecDesc;
    private String curQualAttachments;

    private EmployeeQualifications employeeQualifications;

    public EmployeeQualificationsData() {
	employeeQualifications = new EmployeeQualifications();
    }

    @Id
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
	employeeQualifications.setEmployeeId(employeeId);
    }

    @Basic
    @Column(name = "REC_QUAL_LEVEL_ID")
    public Long getRecQualLevelId() {
	return recQualLevelId;
    }

    public void setRecQualLevelId(Long recQualLevelId) {
	this.recQualLevelId = recQualLevelId;
	employeeQualifications.setRecQualLevelId(recQualLevelId);
    }

    @Basic
    @Column(name = "REC_QUAL_LEVEL_DESC")
    public String getRecQualLevelDesc() {
	return recQualLevelDesc;
    }

    public void setRecQualLevelDesc(String recQualLevelDesc) {
	this.recQualLevelDesc = recQualLevelDesc;
    }

    @Basic
    @Column(name = "REC_QUAL_PERCENTAGE")
    public Double getRecQualPercentage() {
	return recQualPercentage;
    }

    public void setRecQualPercentage(Double recQualPercentage) {
	this.recQualPercentage = recQualPercentage;
	employeeQualifications.setRecQualPercentage(recQualPercentage);
    }

    @Basic
    @Column(name = "REC_GRADUATION_YEAR")
    public Integer getRecGraduationYear() {
	return recGraduationYear;
    }

    public void setRecGraduationYear(Integer recGraduationYear) {
	this.recGraduationYear = recGraduationYear;
	employeeQualifications.setRecGraduationYear(recGraduationYear);
    }

    @Basic
    @Column(name = "REC_GRADUATION_PLACE_ID")
    public Long getRecGraduationPlaceId() {
	return recGraduationPlaceId;
    }

    public void setRecGraduationPlaceId(Long recGraduationPlaceId) {
	this.recGraduationPlaceId = recGraduationPlaceId;
    }

    @Basic
    @Column(name = "REC_GRADUATION_PLACE_DESC")
    public String getRecGraduationPlaceDesc() {
	return recGraduationPlaceDesc;
    }

    public void setRecGraduationPlaceDesc(String recGraduationPlaceDesc) {
	this.recGraduationPlaceDesc = recGraduationPlaceDesc;
    }

    @Basic
    @Column(name = "REC_GRADUATION_PLACE_DTL_ID")
    public Long getRecGraduationPlaceDetailId() {
	return recGraduationPlaceDetailId;
    }

    public void setRecGraduationPlaceDetailId(Long recGraduationPlaceDetailId) {
	this.recGraduationPlaceDetailId = recGraduationPlaceDetailId;
	employeeQualifications.setRecGraduationPlaceDetailId(recGraduationPlaceDetailId);
    }

    @Basic
    @Column(name = "REC_GRADUATION_PLACE_DTL_DESC")
    public String getRecGraduationPlaceDetailDesc() {
	return recGraduationPlaceDetailDesc;
    }

    public void setRecGraduationPlaceDetailDesc(String recGraduationPlaceDetailDesc) {
	this.recGraduationPlaceDetailDesc = recGraduationPlaceDetailDesc;
    }

    @Basic
    @Column(name = "REC_STUDY_COUNTRY_ID")
    public Long getRecStudyCountryId() {
	return recStudyCountryId;
    }

    public void setRecStudyCountryId(Long recStudyCountryId) {
	this.recStudyCountryId = recStudyCountryId;
    }

    @Basic
    @Column(name = "REC_STUDY_COUNTRY_NAME")
    public String getRecStudyCountryName() {
	return recStudyCountryName;
    }

    public void setRecStudyCountryName(String recStudyCountryName) {
	this.recStudyCountryName = recStudyCountryName;
    }

    @Basic
    @Column(name = "REC_QUAL_MINOR_SPEC_ID")
    public Long getRecQualMinorSpecId() {
	return recQualMinorSpecId;
    }

    public void setRecQualMinorSpecId(Long recQualMinorSpecId) {
	this.recQualMinorSpecId = recQualMinorSpecId;
	employeeQualifications.setRecQualMinorSpecId(recQualMinorSpecId);
    }

    @Basic
    @Column(name = "REC_QUAL_MINOR_SPEC_DESC")
    public String getRecQualMinorSpecDesc() {
	return recQualMinorSpecDesc;
    }

    public void setRecQualMinorSpecDesc(String recQualMinorSpecDesc) {
	this.recQualMinorSpecDesc = recQualMinorSpecDesc;
    }

    @Basic
    @Column(name = "REC_QUAL_MAJOR_SPEC_ID")
    public Long getRecQualMajorSpecId() {
	return recQualMajorSpecId;
    }

    public void setRecQualMajorSpecId(Long recQualMajorSpecId) {
	this.recQualMajorSpecId = recQualMajorSpecId;
    }

    @Basic
    @Column(name = "REC_QUAL_MAJOR_SPEC_DESC")
    public String getRecQualMajorSpecDesc() {
	return recQualMajorSpecDesc;
    }

    public void setRecQualMajorSpecDesc(String recQualMajorSpecDesc) {
	this.recQualMajorSpecDesc = recQualMajorSpecDesc;
    }

    @Basic
    @Column(name = "REC_QUAL_ATTACHMENTS")
    public String getRecQualAttachments() {
	return recQualAttachments;
    }

    public void setRecQualAttachments(String recQualAttachments) {
	this.recQualAttachments = recQualAttachments;
	employeeQualifications.setRecQualAttachments(recQualAttachments);
    }

    @Basic
    @Column(name = "CUR_QUAL_LEVEL_ID")
    public Long getCurQualLevelId() {
	return curQualLevelId;
    }

    public void setCurQualLevelId(Long curQualLevelId) {
	this.curQualLevelId = curQualLevelId;
	employeeQualifications.setCurQualLevelId(curQualLevelId);
    }

    @Basic
    @Column(name = "CUR_QUAL_LEVEL_DESC")
    public String getCurQualLevelDesc() {
	return curQualLevelDesc;
    }

    public void setCurQualLevelDesc(String curQualLevelDesc) {
	this.curQualLevelDesc = curQualLevelDesc;
    }

    @Basic
    @Column(name = "CUR_QUAL_PERCENTAGE")
    public Double getCurQualPercentage() {
	return curQualPercentage;
    }

    public void setCurQualPercentage(Double curQualPercentage) {
	this.curQualPercentage = curQualPercentage;
	employeeQualifications.setCurQualPercentage(curQualPercentage);
    }

    @Basic
    @Column(name = "CUR_GRADUATION_YEAR")
    public Integer getCurGraduationYear() {
	return curGraduationYear;
    }

    public void setCurGraduationYear(Integer curGraduationYear) {
	this.curGraduationYear = curGraduationYear;
	employeeQualifications.setCurGraduationYear(curGraduationYear);
    }

    @Basic
    @Column(name = "CUR_GRADUATION_PLACE_ID")
    public Long getCurGraduationPlaceId() {
	return curGraduationPlaceId;
    }

    public void setCurGraduationPlaceId(Long curGraduationPlaceId) {
	this.curGraduationPlaceId = curGraduationPlaceId;
    }

    @Basic
    @Column(name = "CUR_GRADUATION_PLACE_DESC")
    public String getCurGraduationPlaceDesc() {
	return curGraduationPlaceDesc;
    }

    public void setCurGraduationPlaceDesc(String curGraduationPlaceDesc) {
	this.curGraduationPlaceDesc = curGraduationPlaceDesc;
    }

    @Basic
    @Column(name = "CUR_GRADUATION_PLACE_DTL_ID")
    public Long getCurGraduationPlaceDetailId() {
	return curGraduationPlaceDetailId;
    }

    public void setCurGraduationPlaceDetailId(Long curGraduationPlaceDetailId) {
	this.curGraduationPlaceDetailId = curGraduationPlaceDetailId;
	employeeQualifications.setCurGraduationPlaceDetailId(curGraduationPlaceDetailId);
    }

    @Basic
    @Column(name = "CUR_GRADUATION_PLACE_DTL_DESC")
    public String getCurGraduationPlaceDetailDesc() {
	return curGraduationPlaceDetailDesc;
    }

    public void setCurGraduationPlaceDetailDesc(String curGraduationPlaceDetailDesc) {
	this.curGraduationPlaceDetailDesc = curGraduationPlaceDetailDesc;
    }

    @Basic
    @Column(name = "CUR_STUDY_COUNTRY_ID")
    public Long getCurStudyCountryId() {
	return curStudyCountryId;
    }

    public void setCurStudyCountryId(Long curStudyCountryId) {
	this.curStudyCountryId = curStudyCountryId;
    }

    @Basic
    @Column(name = "CUR_STUDY_COUNTRY_NAME")
    public String getCurStudyCountryName() {
	return curStudyCountryName;
    }

    public void setCurStudyCountryName(String curStudyCountryName) {
	this.curStudyCountryName = curStudyCountryName;
    }

    @Basic
    @Column(name = "CUR_QUAL_MINOR_SPEC_ID")
    public Long getCurQualMinorSpecId() {
	return curQualMinorSpecId;
    }

    public void setCurQualMinorSpecId(Long curQualMinorSpecId) {
	this.curQualMinorSpecId = curQualMinorSpecId;
	employeeQualifications.setCurQualMinorSpecId(curQualMinorSpecId);
    }

    @Basic
    @Column(name = "CUR_QUAL_MINOR_SPEC_DESC")
    public String getCurQualMinorSpecDesc() {
	return curQualMinorSpecDesc;
    }

    public void setCurQualMinorSpecDesc(String curQualMinorSpecDesc) {
	this.curQualMinorSpecDesc = curQualMinorSpecDesc;
    }

    @Basic
    @Column(name = "CUR_QUAL_MAJOR_SPEC_ID")
    public Long getCurQualMajorSpecId() {
	return curQualMajorSpecId;
    }

    public void setCurQualMajorSpecId(Long curQualMajorSpecId) {
	this.curQualMajorSpecId = curQualMajorSpecId;
    }

    @Basic
    @Column(name = "CUR_QUAL_MAJOR_SPEC_DESC")
    public String getCurQualMajorSpecDesc() {
	return curQualMajorSpecDesc;
    }

    public void setCurQualMajorSpecDesc(String curQualMajorSpecDesc) {
	this.curQualMajorSpecDesc = curQualMajorSpecDesc;
    }

    @Basic
    @Column(name = "CUR_QUAL_ATTACHMENTS")
    public String getCurQualAttachments() {
	return curQualAttachments;
    }

    public void setCurQualAttachments(String curQualAttachments) {
	this.curQualAttachments = curQualAttachments;
	employeeQualifications.setCurQualAttachments(curQualAttachments);
    }

    @Transient
    public EmployeeQualifications getEmployeeQualifications() {
	return employeeQualifications;
    }

    public void setEmployeeQualifications(EmployeeQualifications employeeQualifications) {
	this.employeeQualifications = employeeQualifications;
    }
}
