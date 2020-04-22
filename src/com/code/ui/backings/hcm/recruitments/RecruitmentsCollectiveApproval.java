package com.code.ui.backings.hcm.recruitments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.recruitments.WFRecruitmentData;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.workflow.hcm.RecruitmentsWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "recruitmentsCollectiveApproval")
@ViewScoped
public class RecruitmentsCollectiveApproval extends BaseBacking implements Serializable {
    // object[0] - WFTask
    // object[1] - WFInstance
    // object[2] - processName
    // object[3] - requester
    // object[4] - delegatingName
    private List<Object> recruitmentsTasks;

    private boolean selectAll;
    private int recruitmentsTasksListSize;

    private final static String taskUrlParam = "&taskId=";
    private final static int pageSize = 10;

    public RecruitmentsCollectiveApproval() {
	super();
	this.setScreenTitle(getMessage("title_recruitmentsCollectiveApproval"));
	// Load the recruitments tasks data
	searchRecruitmentTasks();
    }

    // Load the recruitments tasks data
    private void searchRecruitmentTasks() {
	try {
	    selectAll = false;
	    recruitmentsTasks = (ArrayList<Object>) RecruitmentsWorkFlow.getWFRecruitmentsTasks(this.loginEmpData.getEmpId(), WFTaskRolesEnum.SIGN_MANAGER.getCode());
	    recruitmentsTasksListSize = recruitmentsTasks.size();
	} catch (BusinessException e) {
	    recruitmentsTasks = new ArrayList<Object>();
	    recruitmentsTasksListSize = 0;
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    // called from the xhtml on change of the selectAll checkbox
    public void selectUnselectAllRows() {
	for (int i = 0; i < recruitmentsTasks.size(); i++) {
	    ((WFTask) (((Object[]) recruitmentsTasks.get(i))[0])).setSelected(selectAll);
	}
    }

    // Loop over the selected tasks for approval
    // called from the xhtml when "Approve" button clicked
    public void doRecruitmentsCollectiveApprovalSM() {
	try {
	    String unsuccessfulTaskIdsIfAny = "";
	    String comma = "";
	    int unsuccessfulTasksCount = 0;
	    List<Long> selectedRecruitmentRequestsInstanceIds = new ArrayList<Long>();
	    Map<Long, List<WFRecruitmentData>> wfRecruitmentDetails = null;
	    List<Object> selectedRecruitmentsTasks = new ArrayList<Object>();

	    for (Object obj : recruitmentsTasks) {
		WFTask task = (WFTask) (((Object[]) obj)[0]);
		if (task.getSelected()) {
		    selectedRecruitmentRequestsInstanceIds.add(task.getInstanceId());
		    selectedRecruitmentsTasks.add(obj);
		}
	    }

	    Long[] selectedRecruitmentRequestsInstanceIdsArray = new Long[selectedRecruitmentRequestsInstanceIds.size()];
	    wfRecruitmentDetails = RecruitmentsWorkFlow.getWFRecruitmentsByInstanceIds(selectedRecruitmentRequestsInstanceIds.toArray(selectedRecruitmentRequestsInstanceIdsArray));

	    for (Object obj : selectedRecruitmentsTasks) {
		WFTask task = null;
		try {
		    task = (WFTask) (((Object[]) obj)[0]);
		    WFInstance instance = (WFInstance) (((Object[]) obj)[1]);
		    EmployeeData requester = (EmployeeData) (((Object[]) obj)[3]);
		    List<WFRecruitmentData> wfRecruitmentDataList = wfRecruitmentDetails.get(task.getInstanceId());
		    if (wfRecruitmentDataList == null)
			wfRecruitmentDataList = new ArrayList<WFRecruitmentData>();
		    RecruitmentsWorkFlow.doRecruitmentSM(requester, instance, wfRecruitmentDataList, task, 1);
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
	    searchRecruitmentTasks();

	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<Object> getRecruitmentsTasks() {
	return recruitmentsTasks;
    }

    public void setRecruitmentsTasks(ArrayList<Object> recruitmentsTasks) {
	this.recruitmentsTasks = recruitmentsTasks;
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

    public int getRecruitmentsTasksListSize() {
	return recruitmentsTasksListSize;
    }

    public void setRecruitmentsTasksListSize(int recruitmentsTasksListSize) {
	this.recruitmentsTasksListSize = recruitmentsTasksListSize;
    }

}