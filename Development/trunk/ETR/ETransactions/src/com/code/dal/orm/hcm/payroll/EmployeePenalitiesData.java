package com.code.dal.orm.hcm.payroll;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_employeePenalitiesData_getEmployeePenalitiesData",
		query = "select p "
			+ " from EmployeePenalitiesData p where p.employeeNo = :P_EMP_OLD_ID ") })
@Entity
@Table(name = "BG_VW_PUNCH_EMP_INFO")
public class EmployeePenalitiesData extends BaseEntity {

    private String employeeNo;
    private String punchName;
    private String decisionTypeDescription;
    private Long punchPeriod;
    private Long punchStopPeriod;
    private String decisionNumber;
    private String decisionDate;
    private String id;

    @Id
    @Column(name = "ROWID")
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    @Basic
    @Column(name = "EMP_NO")
    public String getEmployeeNo() {
	return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
	this.employeeNo = employeeNo;
    }

    @Basic
    @Column(name = "PU_NAME")
    public String getPunchName() {
	return punchName;
    }

    public void setPunchName(String punchName) {
	this.punchName = punchName;
    }

    @Basic
    @Column(name = "R_NAME")
    public String getDecisionTypeDescription() {
	return decisionTypeDescription;
    }

    public void setDecisionTypeDescription(String decisionTypeDescription) {
	this.decisionTypeDescription = decisionTypeDescription;
    }

    @Basic
    @Column(name = "PUN_PERIOD")
    public Long getPunchPeriod() {
	return punchPeriod;
    }

    public void setPunchPeriod(Long punchPeriod) {
	this.punchPeriod = punchPeriod;
    }

    @Basic
    @Column(name = "PUN_PERIOD_1")
    public Long getPunchStopPeriod() {
	return punchStopPeriod;
    }

    public void setPunchStopPeriod(Long punchStopPeriod) {
	this.punchStopPeriod = punchStopPeriod;
    }

    @Basic
    @Column(name = "ORD_NO")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    @Basic
    @Column(name = "ORD_DATE_H")
    public String getDecisionDate() {
	try {
	    return new StringBuilder(decisionDate).insert(decisionDate.length() - 6, "/").insert(decisionDate.length() - 3, "/").toString();
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}

    }

    public void setDecisionDate(String decisionDate) {
	this.decisionDate = decisionDate;
    }

}
