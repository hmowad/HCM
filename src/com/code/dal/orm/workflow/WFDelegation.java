package com.code.dal.orm.workflow;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.orm.AuditEntity;

/**
 * The <code>WFDelegation</code> class represents the WorkFlow delegation data.</br>
 * The employee can delegate his tasks to another employee (either these tasks belong to specific processes or all the processes) from either some specific units or all the units.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "wf_delegation_getDelegateIdAllHierarchy",
		query = " select d.delegateId from WFDelegation d " +
			" where d.empId = :P_EMP_ID " +
			" and ((:P_PROCESS_ID = -1 and d.processId is null) or (:P_PROCESS_ID <> -1 and d.processId = :P_PROCESS_ID)) " +
			" and d.unitId is null "),

	@NamedQuery(name = "wf_delegation_getWFDelegationsCount",
		query = " select count(case when d.processId is null and d.empId = :P_EMP_ID then 1 end), " +
			" count(case when d.processId is null and d.delegateId = :P_EMP_ID then 1 end), " +
			" count(case when d.processId is not null and d.empId = :P_EMP_ID then 1 end), " +
			" count(case when d.processId is not null and d.delegateId = :P_EMP_ID then 1 end) " +
			" from WFDelegation d "),

	@NamedQuery(name = "wf_delegation_getWFDelegationsEitherFromOrToEmployees",
		query = " select d from WFDelegation d " +
			" where d.empId in (:P_EMPS_IDS) " +
			" or d.delegateId in (:P_EMPS_IDS) ")
})
@Entity
@Table(name = "WF_DELEGATIONS")
public class WFDelegation extends AuditEntity implements InsertableAuditEntity, DeletableAuditEntity {
    private Long id;
    private Long empId;
    private Long delegateId;
    private Long processId;
    private Long unitId;

    public void setId(Long id) {
	this.id = id;
    }

    @SequenceGenerator(name = "ETRGeneralSeq",
	    sequenceName = "ETR_GENERAL_SEQ")
    @Id
    @GeneratedValue(generator = "ETRGeneralSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setDelegateId(Long delegateId) {
	this.delegateId = delegateId;
    }

    @Basic
    @Column(name = "DELEGATE_ID")
    public Long getDelegateId() {
	return delegateId;
    }

    public void setProcessId(Long processId) {
	this.processId = processId;
    }

    @Basic
    @Column(name = "PROCESS_ID")
    public Long getProcessId() {
	return processId;
    }

    @Basic
    @Column(name = "UNIT_ID")
    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "empId:" + empId + AUDIT_SEPARATOR +
		"delegateId:" + delegateId + AUDIT_SEPARATOR +
		"processId:" + processId + AUDIT_SEPARATOR +
		"unitId:" + unitId;
    }
}