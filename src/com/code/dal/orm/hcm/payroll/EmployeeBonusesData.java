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
	@NamedQuery(name = "hcm_employeeBonusesData_getEmployeeBonusesData",
		query = "select b "
			+ " from EmployeeBonusesData b where b.employeeNo = :P_EMP_OLD_ID ") })

@Entity
@Table(name = "BG_VW_BONUS_EMP_INFO")
public class EmployeeBonusesData extends BaseEntity {

    private String employeeNo;
    private String bonusDesicion;
    private String decisionNumber;
    private String decisionTypeDescription;
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
    @Column(name = "BONUS_NAME")
    public String getBonusDesicion() {
	return bonusDesicion;
    }

    public void setBonusDesicion(String bonusDesicion) {
	this.bonusDesicion = bonusDesicion;
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
    @Column(name = "R_NAME")
    public String getDecisionTypeDescription() {
	return decisionTypeDescription;
    }

    public void setDecisionTypeDescription(String decisionTypeDescription) {
	this.decisionTypeDescription = decisionTypeDescription;
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
