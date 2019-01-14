package com.code.dal.orm.hcm.employees;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

@SuppressWarnings("serial")
@NamedQueries({
	@NamedQuery(name = "hcm_empDataExtraTrnData_searchEmpDataExtraTrnData",
		query = "select e from EmployeeDataExtraTransactionData e " +
			" where (:P_EMP_ID = -1 or e.empId = :P_EMP_ID) " +
			" and (:P_DECISCION_NUMBER = '-1' or e.decisionNumber = :P_DECISCION_NUMBER) " +
			" order by e.effectiveDate desc")

})

@Entity
@Table(name = "HCM_VW_PRS_EMPS_DATA_EXT_TRNS")
public class EmployeeDataExtraTransactionData extends BaseEntity implements Serializable {

    private Long id;
    private Long empId;
    private String empName;
    private Long rankTitleId;
    private String rankTitleDesc;
    private Long salaryRankId;
    private String salaryRankDesc;
    private Long salaryDegreeId;
    private String salaryDegreeDesc;
    private Integer socialStatus;
    private String socialStatusDesc;
    private Integer generalSpecialization;
    private String generalSpecDescription;
    private Date effectiveDate;
    private String effectiveDateString;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;
    private Long medStaffRankId;
    private String medStaffRankDesc;
    private Long medStaffLevelId;
    private String medStaffLevelDesc;
    private Long medStaffDegreeId;
    private String medStaffDegreeDesc;
    private EmployeeDataExtraTransaction employeeDataExtraTransaction;

    public EmployeeDataExtraTransactionData() {
	employeeDataExtraTransaction = new EmployeeDataExtraTransaction();
    }

    @SequenceGenerator(name = "HCMEmpsExtraTrnsSeq",
	    sequenceName = "HCM_EMPS_EXTRA_TRNS_SEQ",
	    allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
	    generator = "HCMEmpsExtraTrnsSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	employeeDataExtraTransaction.setId(id);
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	employeeDataExtraTransaction.setEmpId(empId);
    }

    @Basic
    @Column(name = "EMP_NAME")
    public String getEmpName() {
	return empName;
    }

    public void setEmpName(String empName) {
	this.empName = empName;
    }

    @Basic
    @Column(name = "RANK_TITLE_ID")
    public Long getRankTitleId() {
	return rankTitleId;
    }

    public void setRankTitleId(Long rankTitleId) {
	this.rankTitleId = rankTitleId;
	employeeDataExtraTransaction.setRankTitleId(rankTitleId);
    }

    @Basic
    @Column(name = "RANK_TITLE_DESCRIPTION")
    public String getRankTitleDesc() {
	return rankTitleDesc;
    }

    public void setRankTitleDesc(String rankTitleDesc) {
	this.rankTitleDesc = rankTitleDesc;
    }

    @Basic
    @Column(name = "SALARY_RANK_ID")
    public Long getSalaryRankId() {
	return salaryRankId;
    }

    public void setSalaryRankId(Long salaryRankId) {
	this.salaryRankId = salaryRankId;
	employeeDataExtraTransaction.setSalaryRankId(salaryRankId);
    }

    @Basic
    @Column(name = "SALARY_RANK_DESCRIPTION")
    public String getSalaryRankDesc() {
	return salaryRankDesc;
    }

    public void setSalaryRankDesc(String salaryRankDesc) {
	this.salaryRankDesc = salaryRankDesc;
    }

    @Basic
    @Column(name = "SALARY_DEGREE_ID")
    public Long getSalaryDegreeId() {
	return salaryDegreeId;
    }

    public void setSalaryDegreeId(Long salaryDegreeId) {
	this.salaryDegreeId = salaryDegreeId;
	employeeDataExtraTransaction.setSalaryDegreeId(salaryDegreeId);
    }

    @Basic
    @Column(name = "SALARY_DEGREE_DESCRIPTION")
    public String getSalaryDegreeDesc() {
	return salaryDegreeDesc;
    }

    public void setSalaryDegreeDesc(String salaryDegreeDesc) {
	this.salaryDegreeDesc = salaryDegreeDesc;
    }

    @Basic
    @Column(name = "SOCIAL_STATUS")
    public Integer getSocialStatus() {
	return socialStatus;
    }

    public void setSocialStatus(Integer socialStatus) {
	this.socialStatus = socialStatus;
	employeeDataExtraTransaction.setSocialStatus(socialStatus);
    }

    @Basic
    @Column(name = "SOCIAL_STATUS_DESCRIPTION")
    public String getSocialStatusDesc() {
	return socialStatusDesc;
    }

    public void setSocialStatusDesc(String socialStatusDesc) {
	this.socialStatusDesc = socialStatusDesc;
    }

    @Basic
    @Column(name = "GENERAL_SPECIALIZATION")
    public Integer getGeneralSpecialization() {
	return generalSpecialization;
    }

    public void setGeneralSpecialization(Integer generalSpecialization) {
	this.generalSpecialization = generalSpecialization;
	employeeDataExtraTransaction.setGeneralSpecialization(generalSpecialization);
    }

    @Basic
    @Column(name = "GENERAL_SPEC_DESCRIPTION")
    public String getGeneralSpecDescription() {
	return generalSpecDescription;
    }

    public void setGeneralSpecDescription(String generalSpecDescription) {
	this.generalSpecDescription = generalSpecDescription;
    }

    @Basic
    @Column(name = "EFFECTIVE_DATE")
    public Date getEffectiveDate() {
	return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
	this.effectiveDate = effectiveDate;
	this.effectiveDateString = HijriDateService.getHijriDateString(effectiveDate);
	employeeDataExtraTransaction.setEffectiveDate(effectiveDate);
    }

    @Transient
    public String getEffectiveDateString() {
	return effectiveDateString;
    }

    public void setEffectiveDateString(String effectiveDateString) {
	this.effectiveDateString = effectiveDateString;
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
	employeeDataExtraTransaction.setDecisionNumber(decisionNumber);
    }

    @Basic
    @Column(name = "DECISION_DATE")
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
	this.decisionDateString = HijriDateService.getHijriDateString(decisionDate);
	employeeDataExtraTransaction.setDecisionDate(decisionDate);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
    }

    @Basic
    @Column(name = "MED_STAFF_RANK_ID")
    public Long getMedStaffRankId() {
	return medStaffRankId;
    }

    public void setMedStaffRankId(Long medStaffRankId) {
	this.medStaffRankId = medStaffRankId;
	employeeDataExtraTransaction.setMedStaffRankId(medStaffRankId);
    }

    @Basic
    @Column(name = "MED_STAFF_RANK_DESCRIPTION")
    public String getMedStaffRankDesc() {
	return medStaffRankDesc;
    }

    public void setMedStaffRankDesc(String medStaffRankDesc) {
	this.medStaffRankDesc = medStaffRankDesc;
    }

    @Basic
    @Column(name = "MED_STAFF_LEVEL_ID")
    public Long getMedStaffLevelId() {
	return medStaffLevelId;
    }

    public void setMedStaffLevelId(Long medStaffLevelId) {
	this.medStaffLevelId = medStaffLevelId;
	employeeDataExtraTransaction.setMedStaffLevelId(medStaffLevelId);
    }

    @Basic
    @Column(name = "MED_STAFF_LEVEL_DESCRIPTION")
    public String getMedStaffLevelDesc() {
	return medStaffLevelDesc;
    }

    public void setMedStaffLevelDesc(String medStaffLevelDesc) {
	this.medStaffLevelDesc = medStaffLevelDesc;
    }

    @Basic
    @Column(name = "MED_STAFF_DEGREE_ID")
    public Long getMedStaffDegreeId() {
	return medStaffDegreeId;
    }

    public void setMedStaffDegreeId(Long medStaffDegreeId) {
	this.medStaffDegreeId = medStaffDegreeId;
	employeeDataExtraTransaction.setMedStaffDegreeId(medStaffDegreeId);
    }

    @Basic
    @Column(name = "MED_STAFF_DEGREE_DESCRIPTION")
    public String getMedStaffDegreeDesc() {
	return medStaffDegreeDesc;
    }

    public void setMedStaffDegreeDesc(String medStaffDegreeDesc) {
	this.medStaffDegreeDesc = medStaffDegreeDesc;
    }

    @Transient
    public EmployeeDataExtraTransaction getEmployeeDataExtraTransaction() {
	return employeeDataExtraTransaction;
    }

    public void setEmployeeDataExtraTransaction(EmployeeDataExtraTransaction employeeDataExtraTransaction) {
	this.employeeDataExtraTransaction = employeeDataExtraTransaction;
    }

}