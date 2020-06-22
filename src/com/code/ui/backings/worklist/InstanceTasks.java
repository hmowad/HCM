package com.code.ui.backings.worklist;

import java.util.HashSet;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.servlet.http.HttpServletRequest;

import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONObject;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.WFInstanceData;
import com.code.dal.orm.workflow.WFTaskData;
import com.code.enums.SessionAttributesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.services.hcm.EmployeesService;
import com.code.services.workflow.BaseWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "instanceTasks")
@RequestScoped
public class InstanceTasks extends BaseBacking {
    private WFInstanceData instance;
    private JSONArray nodes;
    private boolean onHoldProcessWarningFlag;

    @SuppressWarnings("unchecked")
    public InstanceTasks() {
	try {
	    HttpServletRequest req = getRequest();
	    long instanceId = Long.parseLong(req.getParameter("instanceId"));

	    HashSet<Long> instancesIds = (HashSet<Long>) req.getSession().getAttribute(SessionAttributesEnum.ACCESSED_WF_INSTANCES_IDS.getCode());
	    if (instancesIds != null && instancesIds.contains(instanceId)) {
		instance = BaseWorkFlow.getWFInstanceDataById(instanceId);
		List<WFTaskData> tasks = BaseWorkFlow.getWFInstanceTasksData(instanceId);
		onHoldProcessWarningFlag = BaseWorkFlow.getUnassignedWFTasksByInstanceId(instanceId).size() > 0 ? true : false;

		nodes = new JSONArray();
		JSONObject node;
		JSONArray tempArray;

		// Start Process.
		node = new JSONObject();
		node.put("level", "1");
		node.put("image", req.getContextPath() + "/resources/images/start.png");
		nodes.put(node);

		// Instance data.
		node = new JSONObject();

		tempArray = new JSONArray();
		// tempArray.put(getMessage("label_empId"));
		// tempArray.put(instance.getRequesterId());
		tempArray.put(getMessage("label_name"));
		tempArray.put(instance.getRequesterRankDesc() + "/ " + instance.getRequesterName());

		EmployeeData requester = EmployeesService.getEmployeeData(instance.getRequesterId());
		if (requester.getJobId() != null) {
		    tempArray.put(getMessage("label_currentJob"));
		    tempArray.put(requester.getJobDesc());
		} else {
		    tempArray.put(getMessage("label_status"));
		    tempArray.put(requester.getStatusDesc());

		}

		node.put("top", tempArray);

		tempArray = new JSONArray();
		tempArray.put(getMessage("label_requestDate"));
		tempArray.put(instance.getHijriRequestDateString());
		node.put("bottom", tempArray);

		node.put("level", "1");
		node.put("image", req.getContextPath() + "/resources/images/receive.png");
		nodes.put(node);

		// Tasks Data.
		for (WFTaskData task : tasks) {
		    // Adjusting delegation information.
		    if (task.getDelegatingName() != null) {
			EmployeeData assigneeEmp = EmployeesService.getEmployeeData(task.getAssigneeId());
			task.setOriginalName(task.getOriginalName() + " (" + getMessage("label_delegateeName") + ": " + assigneeEmp.getEmpId() + " - " + assigneeEmp.getRankDesc() + "/ " + assigneeEmp.getName() + " ) ");
		    }

		    // JSON data.
		    node = new JSONObject();

		    tempArray = new JSONArray();
		    // tempArray.put(getMessage("label_empId"));
		    // tempArray.put(task.getOriginalId());
		    tempArray.put(getMessage("label_name"));
		    tempArray.put(task.getOriginalRankDesc() + "/ " + task.getOriginalName());

		    EmployeeData originalEmployee = EmployeesService.getEmployeeData(task.getOriginalId());
		    if (originalEmployee.getJobId() != null) {
			tempArray.put(getMessage("label_currentJob"));
			tempArray.put(originalEmployee.getJobDesc());
		    } else {
			tempArray.put(getMessage("label_status"));
			tempArray.put(originalEmployee.getStatusDesc());

		    }

		    if (task.getAction() != null) {
			tempArray.put(getMessage("label_taskAction"));
			tempArray.put(task.getAction());
		    }
		    node.put("top", tempArray);

		    tempArray = new JSONArray();
		    tempArray.put(getMessage("label_taskDate"));
		    tempArray.put(task.getHijriAssignDateString());
		    if (task.getAction() != null) {
			tempArray.put(getMessage("label_taskActionDate"));
			tempArray.put(task.getHijriActionDateString());
		    }
		    node.put("bottom", tempArray);

		    node.put("level", task.getLevel());

		    String image = "/resources/images/task_open.png";
		    if (task.getAction() != null)
			image = "/resources/images/task_done.png";

		    node.put("image", req.getContextPath() + image);

		    nodes.put(node);

		    if ((task.getAssigneeWfRole().equals(WFTaskRolesEnum.NOTIFICATION.getCode()) && task.getAction() != null)
			    || (tasks.get(tasks.size() - 1).getTaskId().equals(task.getTaskId()) && task.getAction() != null)) {
			node = new JSONObject();
			node.put("level", task.getLevel());
			node.put("image", req.getContextPath() + "/resources/images/end.png");
			nodes.put(node);
		    }
		}
	    }
	} catch (Exception e) {
	    System.out.println(e.getMessage());
	}
    }

    public void setInstance(WFInstanceData instance) {
	this.instance = instance;
    }

    public WFInstanceData getInstance() {
	return instance;
    }

    public void setNodes(JSONArray nodes) {
	this.nodes = nodes;
    }

    public JSONArray getNodes() {
	return nodes;
    }

    public boolean isOnHoldProcessWarningFlag() {
	return onHoldProcessWarningFlag;
    }

    public void setOnHoldProcessWarningFlag(boolean onHoldProcessWarningFlag) {
	this.onHoldProcessWarningFlag = onHoldProcessWarningFlag;
    }
}