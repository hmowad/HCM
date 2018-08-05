package com.code.integration.responses.hcm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.hcm.payroll.EmployeePayrollDifferenceData;
import com.code.integration.responses.WSResponseBase;

public class WSPayrollDifferencesResponse extends WSResponseBase {

    private List<EmployeePayrollDifferenceData> payrollDifferences;

    @XmlElementWrapper(name = "payrollDifferences")
    @XmlElement(name = "payrollDifference")
    public List<EmployeePayrollDifferenceData> getPayrollDifferences() {
	return payrollDifferences;
    }

    public void setPayrollDifferences(List<EmployeePayrollDifferenceData> payrollDifferences) {
	this.payrollDifferences = payrollDifferences;
    }

}
