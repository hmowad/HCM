package com.code.integration.responses.hcm;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.integration.responses.WSResponseBase;

public class WSEmployeeResponse extends WSResponseBase {

    private EmployeeData employee;

    public EmployeeData getEmployee() {
	return employee;
    }

    public void setEmployee(EmployeeData employee) {
	this.employee = employee;
    }

}
