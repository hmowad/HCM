package com.code.dal.orm.workflow;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.code.dal.orm.BaseEntity;

/**
 * The <code>WFDelegationData</code> class represents the WorkFlow delegation data in detailed format.</br>
 * The employee can delegate his tasks to another employee (either these tasks belong to specific processes or all the processes) from either some specific units or all the units.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "wf_delegationData_getWFDelegationData",
		query = " select d from WFDelegationData d" +
			" where (:P_EMP_ID = -1 or d.empId = :P_EMP_ID) " +
			" and (:P_DELEGATE_ID = -1 or d.delegateId = :P_DELEGATE_ID) " +
			" and ((:P_PROCESS_ID_FLAG = -1 and d.processId is null) or (:P_PROCESS_ID_FLAG = 1 and d.processId is not null)) " +
			" order by d.empId, d.delegateId, d.processId ")
})
@Entity
@Table(name = "ETR_VW_WF_DELEGATIONS")
public class WFDelegationData extends BaseEntity implements Serializable {
    private Long id;
    private Long empId;
    private Long delegateId;
    private Long processId;
    private Long unitId;

    private String empName;
    private String delegateName;
    private String processName;
    private String unitFullName;
    private String unitHKey;

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
    @XmlElement(nillable = true)
    public Long getProcessId() {
	return processId;
    }

    public void setProcessId(Long processId) {
	this.processId = processId;
    }

    @Basic
    @Column(name = "UNIT_ID")
    @XmlElement(nillable = true)
    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
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
    @Column(name = "DELEGATE_NAME")
    public String getDelegateName() {
	return delegateName;
    }

    public void setDelegateName(String delegateName) {
	this.delegateName = delegateName;
    }

    @Basic
    @Column(name = "PROCESS_NAME")
    @XmlElement(nillable = true)
    public String getProcessName() {
	return processName;
    }

    public void setProcessName(String processName) {
	this.processName = processName;
    }

    @Basic
    @Column(name = "UNIT_FULL_NAME")
    @XmlElement(nillable = true)
    public String getUnitFullName() {
	return unitFullName;
    }

    public void setUnitFullName(String unitFullName) {
	this.unitFullName = unitFullName;
    }

    @Basic
    @Column(name = "UNIT_HKEY")
    @XmlTransient
    public String getUnitHKey() {
	return unitHKey;
    }

    public void setUnitHKey(String unitHKey) {
	this.unitHKey = unitHKey;
    }
}