package com.code.dal.orm.security;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "sec_employeeDecisionPrivilege_getEmployeeDecisionPrivilege",
		query = " select d from EmployeeDecisionPrivilege d" +
			" where (:P_EMP_ID = -1 or d.empId = :P_EMP_ID)" +
			" and (:P_PROCESS_GROUP_ID = -1 or d.processGroupId = :P_PROCESS_GROUP_ID)" +
			" and (:P_CATEGORY_ID = -1 or d.beneficiaryCategoryId = :P_CATEGORY_ID)" + " order by d.id "
	)
})
@Entity
@Table(name = "ETR_VW_EMPS_DECS_PRIVILEGES")
public class EmployeeDecisionPrivilege extends BaseEntity {
    private Long id;
    private Long empId;
    private Long processGroupId;
    private Long beneficiaryCategoryId;
    private Long beneficiaryRegionId;
    private Long beneficiaryUnitId;
    private String beneficiaryUnitHKey;

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    @Basic
    @Column(name = "BENEFICAIRY_CATEGORY_ID")
    public Long getBeneficiaryCategoryId() {
	return beneficiaryCategoryId;
    }

    public void setBeneficiaryCategoryId(Long beneficiaryCategoryId) {
	this.beneficiaryCategoryId = beneficiaryCategoryId;
    }

    @Basic
    @Column(name = "PROCESS_GROUP_ID")
    public Long getProcessGroupId() {
	return processGroupId;
    }

    public void setProcessGroupId(Long processGroupId) {
	this.processGroupId = processGroupId;
    }

    @Basic
    @Column(name = "BENEFICAIRY_REGION_ID")
    public Long getBeneficiaryRegionId() {
	return beneficiaryRegionId;
    }

    public void setBeneficiaryRegionId(Long beneficiaryRegionId) {
	this.beneficiaryRegionId = beneficiaryRegionId;
    }

    @Basic
    @Column(name = "BENEFICAIRY_UNIT_ID")
    public Long getBeneficiaryUnitId() {
	return beneficiaryUnitId;
    }

    public void setBeneficiaryUnitId(Long beneficiaryUnitId) {
	this.beneficiaryUnitId = beneficiaryUnitId;
    }

    @Basic
    @Column(name = "BENEFICIARY_UNIT_HKEY")
    public String getBeneficiaryUnitHKey() {
	return beneficiaryUnitHKey;
    }

    public void setBeneficiaryUnitHKey(String beneficiaryUnitHKey) {
	this.beneficiaryUnitHKey = beneficiaryUnitHKey;
    }

}