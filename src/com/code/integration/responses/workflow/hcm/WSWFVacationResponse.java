package com.code.integration.responses.workflow.hcm;

import javax.xml.bind.annotation.XmlElement;

import com.code.dal.orm.workflow.hcm.vacations.WFVacation;
import com.code.integration.responses.WSResponseBase;

public class WSWFVacationResponse extends WSResponseBase {

    private String employeeNameAndRank;
    private String employeeJobDescription;
    private String employeePhysicalUnitFullName;

    private WFVacation wfVacation;

    private Integer lastVacationPeriod;

    public String getEmployeeNameAndRank() {
	return employeeNameAndRank;
    }

    public void setEmployeeNameAndRank(String employeeNameAndRank) {
	this.employeeNameAndRank = employeeNameAndRank;
    }

    public String getEmployeeJobDescription() {
	return employeeJobDescription;
    }

    public void setEmployeeJobDescription(String employeeJobDescription) {
	this.employeeJobDescription = employeeJobDescription;
    }

    public String getEmployeePhysicalUnitFullName() {
	return employeePhysicalUnitFullName;
    }

    public void setEmployeePhysicalUnitFullName(String employeePhysicalUnitFullName) {
	this.employeePhysicalUnitFullName = employeePhysicalUnitFullName;
    }

    @XmlElement(name = "wfVacation")
    public WFVacation getWfVacation() {
	return wfVacation;
    }

    public void setWfVacation(WFVacation wfVacation) {
	this.wfVacation = wfVacation;
    }

    @XmlElement(nillable = true)
    public Integer getLastVacationPeriod() {
	return lastVacationPeriod;
    }

    public void setLastVacationPeriod(Integer lastVacationPeriod) {
	this.lastVacationPeriod = lastVacationPeriod;
    }

}