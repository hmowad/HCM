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
	@NamedQuery(name = "hcm_employeeAllowancesData_getEmployeeAllowancesData",
		query = "select a "
			+ " from EmployeeAllowancesData a where a.employeeNo = :P_EMP_OLD_ID ") })

@Entity
@Table(name = "BG_VW_TECH_EMP_INFO")
public class EmployeeAllowancesData extends BaseEntity {

    private String employeeNo;
    private String allowanceDescription;
    private Long allowanceClass;
    private Long value;
    private Long percentage;
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
    @Column(name = "TICH_DESC")
    public String getAllowanceDescription() {
	return allowanceDescription;
    }

    public void setAllowanceDescription(String allowanceDescription) {
	this.allowanceDescription = allowanceDescription;
    }

    @Basic
    @Column(name = "TECH_LEVEL")
    public Long getAllowanceClass() {
	return allowanceClass;
    }

    public void setAllowanceClass(Long allowanceClass) {
	this.allowanceClass = allowanceClass;
    }

    @Basic
    @Column(name = "VALE")
    public Long getValue() {
	return value;
    }

    public void setValue(Long value) {
	this.value = value;
    }

    @Basic
    @Column(name = "PERCENTAGE")
    public Long getPercentage() {
	return percentage;
    }

    public void setPercentage(Long percentage) {
	this.percentage = percentage;
    }

    @Basic
    @Column(name = "TECH_ORD_NO")
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
