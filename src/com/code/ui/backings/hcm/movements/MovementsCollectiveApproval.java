package com.code.ui.backings.hcm.movements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.movements.WFMovementData;
import com.code.dal.orm.workflow.hcm.movements.WFMovementWish;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.workflow.hcm.MovementsWishesWorkFlow;
import com.code.services.workflow.hcm.MovementsWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "movementsCollectiveApproval")
@ViewScoped
public class MovementsCollectiveApproval extends BaseBacking implements Serializable {
    // object[0] - WFTask
    // object[1] - WFInstance
    // object[2] - processName
    // object[3] - requester
    // object[4] - delegatingName
    private List<Object> movementsTasks;

    private boolean selectAll;
    private int movementsTasksListSize;

    private final static String taskUrlParam = "&taskId=";
    private final static int pageSize = 10;

    public MovementsCollectiveApproval() {
	super();
	this.setScreenTitle(getMessage("title_MovementsCollectiveApproval"));
	try {
	    searchMovementTasks(); // Load the movements tasks data
	} catch (Exception e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    // Load the Movements tasks data
    private void searchMovementTasks() {
	try {
	    selectAll = false;
	    movementsTasks = (ArrayList<Object>) MovementsWorkFlow.getWFMovementsTasks(this.loginEmpData.getEmpId(), new String[] { WFTaskRolesEnum.SIGN_MANAGER.getCode(), WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode() });
	    movementsTasksListSize = movementsTasks.size();
	} catch (BusinessException e) {
	    movementsTasks = new ArrayList<Object>();
	    movementsTasksListSize = 0;
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    // Called from the xhtml on change of the selectAll check box
    public void selectUnselectAllRows() {
	for (int i = 0; i < movementsTasks.size(); i++) {
	    ((WFTask) (((Object[]) movementsTasks.get(i))[0])).setSelected(selectAll);
	}
    }

    // Loop over the selected tasks for approval
    // called from the xhtml when "Approve" button clicked
    public void doMovementsCollectiveApprovalSM() {
	try {
	    String unsuccessfulTaskIdsIfAny = "";
	    String comma = "";
	    int unsuccessfulTasksCount = 0;
	    List<Long> selectedMovementRequestsInstanceIds = new ArrayList<Long>();
	    Map<Long, List<WFMovementData>> wfMovementsDetails = null;
	    Map<Long, WFMovementWish> wfMovementWishes = null;
	    List<Object> selectedMovementsTasks = new ArrayList<Object>();

	    for (Object obj : movementsTasks) {
		WFTask task = (WFTask) (((Object[]) obj)[0]);
		if (task.getSelected()) {
		    selectedMovementRequestsInstanceIds.add(task.getInstanceId());
		    selectedMovementsTasks.add(obj);
		}
	    }

	    Long[] selectedMovementRequestsInstanceIdsArray = new Long[selectedMovementRequestsInstanceIds.size()];
	    selectedMovementRequestsInstanceIdsArray = selectedMovementRequestsInstanceIds.toArray(selectedMovementRequestsInstanceIdsArray);
	    wfMovementsDetails = MovementsWorkFlow.getWFMovementsByInstanceIds(selectedMovementRequestsInstanceIdsArray);
	    wfMovementWishes = MovementsWishesWorkFlow.getWFMovementsWishesByInstancesIds(selectedMovementRequestsInstanceIdsArray);

	    for (Object obj : selectedMovementsTasks) {
		WFTask task = null;
		try {
		    task = (WFTask) (((Object[]) obj)[0]);
		    WFInstance instance = (WFInstance) (((Object[]) obj)[1]);
		    EmployeeData requester = (EmployeeData) (((Object[]) obj)[3]);

		    if (wfMovementsDetails.containsKey(instance.getInstanceId())) {
			List<WFMovementData> wfMovementDataList = wfMovementsDetails.get(task.getInstanceId());
			if (wfMovementDataList == null)
			    wfMovementDataList = new ArrayList<WFMovementData>();

			if (task.getAssigneeWfRole().equals(WFTaskRolesEnum.SIGN_MANAGER.getCode()))
			    MovementsWorkFlow.doMovementSM(requester, instance, wfMovementDataList, task, WFActionFlagsEnum.APPROVE.getCode());
			else if (task.getAssigneeWfRole().equals(WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode()))
			    MovementsWorkFlow.doMovementSSM(requester, instance, wfMovementDataList, task, WFActionFlagsEnum.APPROVE.getCode());
		    } else {
			WFMovementWish wfMovementWish = wfMovementWishes.get(task.getInstanceId());
			MovementsWishesWorkFlow.doWFMovementWishSM(requester, instance, wfMovementWish, task, WFActionFlagsEnum.APPROVE.getCode());
		    }
		} catch (BusinessException e) {
		    unsuccessfulTaskIdsIfAny += comma + task.getTaskId();
		    unsuccessfulTasksCount++;
		    comma = ", ";
		}
	    }

	    if (unsuccessfulTasksCount > 0)
		this.setServerSideErrorMessages(getParameterizedMessage("error_thereAreErrorsForNOfTasks", new Object[] { unsuccessfulTasksCount, unsuccessfulTaskIdsIfAny }));
	    else
		this.setServerSideSuccessMessages(getMessage("notify_successOperation"));

	    // Call the search method here to Reload the tasks
	    searchMovementTasks();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public int getPageSize() {
	return pageSize;
    }

    public String getTaskUrlParam() {
	return taskUrlParam;
    }

    public boolean isSelectAll() {
	return selectAll;
    }

    public void setSelectAll(boolean selectAll) {
	this.selectAll = selectAll;
    }

    public List<Object> getMovementsTasks() {
	return movementsTasks;
    }

    public void setMovementsTasks(List<Object> movementsTasks) {
	this.movementsTasks = movementsTasks;
    }

    public int getmovementsTasksListSize() {
	return movementsTasksListSize;
    }

    public void setmovementsTasksListSize(int movementsTasksListSize) {
	this.movementsTasksListSize = movementsTasksListSize;
    }

}