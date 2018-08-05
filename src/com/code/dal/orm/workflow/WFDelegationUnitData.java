package com.code.dal.orm.workflow;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "wf_delegationUnitData_getDelegateIdSpecificHierarchy",
		query = " select d.delegateId from WFDelegationUnitData d" +
			" where d.empId = :P_EMP_ID " +
			" and ((:P_PROCESS_ID = -1 and d.processId is null) or (:P_PROCESS_ID <> -1 and d.processId = :P_PROCESS_ID)) " +
			" and d.unitEmployeeId = :P_UNIT_EMPLOYEE_ID ")
})
@Entity
@Table(name = "ETR_VW_WF_DELEGATIONS_UNITS")
public class WFDelegationUnitData extends BaseEntity {
    private Long id;
    private Long empId;
    private Long delegateId;
    private Long processId;
    private Long unitEmployeeId;

    // Take care that this entity is designed to select only one record of data so it is applicable to mark id as PK otherwise it is not applicable for repetition.
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
    @Column(name = "DELEGATE_ID")
    public Long getDelegateId() {
	return delegateId;
    }

    public void setDelegateId(Long delegateId) {
	this.delegateId = delegateId;
    }

    @Basic
    @Column(name = "PROCESS_ID")
    public Long getProcessId() {
	return processId;
    }

    public void setProcessId(Long processId) {
	this.processId = processId;
    }

    @Basic
    @Column(name = "UNIT_EMP_ID")
    public Long getUnitEmployeeId() {
	return unitEmployeeId;
    }

    public void setUnitEmployeeId(Long unitEmployeeId) {
	this.unitEmployeeId = unitEmployeeId;
    }
}