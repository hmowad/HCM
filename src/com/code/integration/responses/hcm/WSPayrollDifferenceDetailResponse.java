package com.code.integration.responses.hcm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.hcm.payroll.SummaryDifferenceDetailData;
import com.code.integration.responses.WSResponseBase;

public class WSPayrollDifferenceDetailResponse extends WSResponseBase {

    private List<SummaryDifferenceDetailData> summaryDifferenceDetailDataList;

    @XmlElementWrapper(name = "payrollDifferenceDetails")
    @XmlElement(name = "payrollDifferenceDetails")
    public List<SummaryDifferenceDetailData> getSummaryDifferenceDetailDataList() {
	return summaryDifferenceDetailDataList;
    }

    public void setSummaryDifferenceDetailDataList(List<SummaryDifferenceDetailData> summaryDifferenceDetailDataList) {
	this.summaryDifferenceDetailDataList = summaryDifferenceDetailDataList;
    }

}
