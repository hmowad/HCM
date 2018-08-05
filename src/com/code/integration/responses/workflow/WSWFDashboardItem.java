package com.code.integration.responses.workflow;

import java.io.Serializable;

public class WSWFDashboardItem implements Serializable {

    private Long processGroupId;
    private String processGroupName;
    private Long tasksCount;

    public Long getProcessGroupId() {
	return processGroupId;
    }

    public void setProcessGroupId(Long processGroupId) {
	this.processGroupId = processGroupId;
    }

    public String getProcessGroupName() {
	return processGroupName;
    }

    public void setProcessGroupName(String processGroupName) {
	this.processGroupName = processGroupName;
    }

    public Long getTasksCount() {
	return tasksCount;
    }

    public void setTasksCount(Long tasksCount) {
	this.tasksCount = tasksCount;
    }

}
