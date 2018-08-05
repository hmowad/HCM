package com.code.integration.responses.workflow;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.integration.responses.WSResponseBase;

public class WSWFInstanceTasksResponse extends WSResponseBase {

    private List<WSWFInstanceTaskInfo> wFInstanceTasksInfo;

    @XmlElementWrapper(name = "wFInstanceTasksInfo")
    @XmlElement(name = "wFInstanceTaskInfo")
    public List<WSWFInstanceTaskInfo> getWFInstanceTasksInfo() {
	return wFInstanceTasksInfo;
    }

    public void setWFInstanceTasksInfo(List<WSWFInstanceTaskInfo> wFInstanceTasksInfo) {
	this.wFInstanceTasksInfo = wFInstanceTasksInfo;
    }
}
