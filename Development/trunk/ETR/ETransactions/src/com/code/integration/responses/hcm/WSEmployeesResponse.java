package com.code.integration.responses.hcm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.integration.responses.WSResponseBase;

public class WSEmployeesResponse extends WSResponseBase {

    private List<EmployeeData> employees;

    @XmlElementWrapper(name = "employees")
    @XmlElement(name = "employee")
    public List<EmployeeData> getEmployees() {
	return employees;
    }

    public void setEmployees(List<EmployeeData> employees) {
	this.employees = employees;
    }

}
