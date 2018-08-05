package com.code.integration.responses.workflow;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.integration.responses.WSResponseBase;

public class WSWFDashboardResponse extends WSResponseBase {

    private List<WSWFDashboardItem> dashBoardData;

    @XmlElementWrapper(name = "dashBoardData")
    @XmlElement(name = "dashBoardItemData")
    public List<WSWFDashboardItem> getDashBoardData() {
	return dashBoardData;
    }

    public void setDashBoardData(List<WSWFDashboardItem> dashBoardData) {
	this.dashBoardData = dashBoardData;
    }

}
