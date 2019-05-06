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
	@NamedQuery(name = "hcm_empDataExtraTrnData_searchEmpExtraTrnData",
		query = "select e from EmployeeExtraTransactionData e " +
			" where (:P_ID = -1 or e.id = :P_ID) " +
			" and (:P_EMP_ID = -1 or e.empId = :P_EMP_ID) " +
			" and (:P_DECISCION_NUMBER = '-1' or e.decisionNumber = :P_DECISCION_NUMBER) " +
			" and (:P_TRANSACTION_TYPE = -1 or e.transactionTypeId = :P_TRANSACTION_TYPE) " +
			" order by e.effectiveDate desc")

})

@Entity
@Table(name = "HCM_VW_PRS_EMPS_EXTRA_TRNS")
public class EmployeeExtraTransactionData extends BaseEntity implements Serializable {

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
    private Long transactionTypeId;
    private EmployeeExtraTransaction employeeExtraTransaction;

    public EmployeeExtraTransactionData() {
	employeeExtraTransaction = new EmployeeExtraTransaction();
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
	employeeExtraTransaction.setId(id);
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	employeeExtraTransaction.setEmpId(empId);
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
	employeeExtraTransaction.setRankTitleId(rankTitleId);
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
	employeeExtraTransaction.setSalaryRankId(salaryRankId);
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
	employeeExtraTransaction.setSalaryDegreeId(salaryDegreeId);
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
	employeeExtraTransaction.setSocialStatus(socialStatus);
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
	employeeExtraTransaction.setGeneralSpecialization(generalSpecialization);
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
	employeeExtraTransaction.setEffectiveDate(effectiveDate);
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
	employeeExtraTransaction.setDecisionNumber(decisionNumber);
    }

    @Basic
    @Column(name = "DECISION_DATE")
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
	this.decisionDateString = HijriDateService.getHijriDateString(decisionDate);
	employeeExtraTransaction.setDecisionDate(decisionDate);
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
	employeeExtraTransaction.setMedStaffRankId(medStaffRankId);
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
	employeeExtraTransaction.setMedStaffLevelId(medStaffLevelId);
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
	employeeExtraTransaction.setMedStaffDegreeId(medStaffDegreeId);
    }

    @Basic
    @Column(name = "MED_STAFF_DEGREE_DESCRIPTION")
    public String getMedStaffDegreeDesc() {
	return medStaffDegreeDesc;
    }

    public void setMedStaffDegreeDesc(String medStaffDegreeDesc) {
	this.medStaffDegreeDesc = medStaffDegreeDesc;
    }

    @Basic
    @Column(name = "TRANSACTION_TYPE_ID")
    public Long getTransactionTypeId() {
	return transactionTypeId;
    }

    public void setTransactionTypeId(Long transactionTypeId) {
	this.transactionTypeId = transactionTypeId;
	this.employeeExtraTransaction.setTransactionTypeId(transactionTypeId);
    }

    @Transient
    public EmployeeExtraTransaction getEmployeeExtraTransaction() {
	return employeeExtraTransaction;
    }

    public void setEmployeeExtraTransaction(EmployeeExtraTransaction EmployeeExtraTransaction) {
	this.employeeExtraTransaction = EmployeeExtraTransaction;
    }

}
