package com.code.integration.responses.hcm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.hcm.payroll.SummaryDifferenceData;
import com.code.integration.responses.WSResponseBase;

public class WSPayrollDifferencesResponse extends WSResponseBase {

    private List<SummaryDifferenceData> payrollDifferences;

    @XmlElementWrapper(name = "payrollDifferences")
    @XmlElement(name = "payrollDifferences")
    public List<SummaryDifferenceData> getPayrollDifferences() {
	return payrollDifferences;
    }

    public void setPayrollDifferences(List<SummaryDifferenceData> payrollDifferences) {
	this.payrollDifferences = payrollDifferences;
    }

}
