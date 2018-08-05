package com.code.ui.backings.hcm.missions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.missions.WFMissionData;
import com.code.dal.orm.workflow.hcm.missions.WFMissionDetailData;
import com.code.enums.FlagsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.workflow.hcm.MissionsWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "missionsCollectiveApproval")
@ViewScoped
public class MissionsCollectiveApproval extends BaseBacking {
    // object[0] - WFmissionData
    // object[1] - WFTask
    // object[2] - WFInstance
    // object[3] - processName
    // object[4] - requester
    // object[5] - delegatingName
    private List<Object> missionsTasks;

    private boolean selectAll;
    private int missionsTasksListSize;

    private final static String taskUrlParam = "&taskId=";
    private final static int pageSize = 10;
    private int mode = -1; // 1 means acceptance and 2 means approval

    public MissionsCollectiveApproval() {
	super();
	if (getRequest().getAttribute("mode") != null)
	    this.mode = Integer.parseInt(getRequest().getAttribute("mode").toString());

	if (mode == 1) {
	    this.setScreenTitle(getMessage("title_missionsCollectiveAcceptance"));
	} else if (mode == 2) {
	    this.setScreenTitle(getMessage("title_missionsCollectiveApproval"));
	}
	try {
	    // Load the missions tasks data
	    searchMissionTasks();
	} catch (Exception e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    // Load the missions tasks data
    private void searchMissionTasks() {
	try {
	    selectAll = false;
	    missionsTasks = (ArrayList<Object>) MissionsWorkFlow.getWFMissionsTasks(this.loginEmpData.getEmpId(), mode == 1 ? WFTaskRolesEnum.DIRECT_MANAGER.getCode() : WFTaskRolesEnum.SIGN_MANAGER.getCode());
	    missionsTasksListSize = missionsTasks.size();
	} catch (BusinessException e) {
	    missionsTasks = new ArrayList<Object>();
	    missionsTasksListSize = 0;
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    // called from the xhtml on change of the selectAll checkbox
    public void selectUnselectAllRows() {
	for (int i = 0; i < missionsTasks.size(); i++) {
	    ((WFMissionData) (((Object[]) missionsTasks.get(i))[0])).setSelected(selectAll);
	}
    }

    // Loop over the selected tasks for approval
    // called from the xhtml when "Approve" button clicked
    public void doMissionsCollectiveAction() {
	try {
	    String unsuccessfulTaskIdsIfAny = "";
	    String comma = "";
	    int unsuccessfulTasksCount = 0;
	    List<Long> selectedMissionRequestsInstanceIds = new ArrayList<Long>();
	    Map<Long, List<WFMissionDetailData>> wfMissionDetails = null;
	    List<Object> selectedMissionsTasks = new ArrayList<Object>();

	    for (Object obj : missionsTasks) {
		WFMissionData missionRequest = (WFMissionData) (((Object[]) obj)[0]);
		if (missionRequest.getSelected()) {
		    selectedMissionRequestsInstanceIds.add(missionRequest.getInstanceId());
		    selectedMissionsTasks.add(obj);
		}
	    }

	    Long[] selectedMissionRequestsInstanceIdsArray = new Long[selectedMissionRequestsInstanceIds.size()];
	    wfMissionDetails = MissionsWorkFlow.getWFMissionDetailsByInstanceIds(selectedMissionRequestsInstanceIds.toArray(selectedMissionRequestsInstanceIdsArray));

	    for (Object obj : selectedMissionsTasks) {
		WFMissionData missionRequest = (WFMissionData) (((Object[]) obj)[0]);
		WFTask task = null;
		try {
		    task = (WFTask) (((Object[]) obj)[1]);
		    WFInstance instance = (WFInstance) (((Object[]) obj)[2]);
		    EmployeeData requester = (EmployeeData) (((Object[]) obj)[4]);
		    List<WFMissionDetailData> wfMissionDetailDataList = wfMissionDetails.get(missionRequest.getInstanceId());
		    if (wfMissionDetailDataList == null)
			wfMissionDetailDataList = new ArrayList<WFMissionDetailData>();
		    if (mode == 1) {
			MissionsWorkFlow.doMissionDM(requester, instance, missionRequest, wfMissionDetailDataList, task, true);
		    } else if (mode == 2) {
			MissionsWorkFlow.doMissionSM(requester, instance, missionRequest, wfMissionDetailDataList, task, FlagsEnum.ON.getCode());
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
	    searchMissionTasks();

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public List<Object> getMissionsTasks() {
	return missionsTasks;
    }

    public void setMissionsTasks(ArrayList<Object> missionsTasks) {
	this.missionsTasks = missionsTasks;
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

    public int getMissionsTasksListSize() {
	return missionsTasksListSize;
    }

    public void setMissionsTasksListSize(int missionsTasksListSize) {
	this.missionsTasksListSize = missionsTasksListSize;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

}