package com.code.ui.backings.hcm.terminations;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.terminations.TerminationRecordData;
import com.code.dal.orm.hcm.terminations.TerminationRecordDetailData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.terminations.WFTerminationData;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TerminationsService;
import com.code.services.util.CommonService;
import com.code.services.workflow.hcm.TerminationsWorkflow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "TerminationsCollectiveApproval")
@ViewScoped
public class TerminationsCollectiveApproval extends BaseBacking {

    // object[0] - WFTerminationData
    // object[1] - WFTask
    // object[2] - WFInstance
    // object[3] - processName
    // object[4] - requester
    // object[5] - delegatingName
    private List<Object> terminationsTasks;

    private boolean selectAll;
    private int terminationsTasksListSize;

    private final static String taskUrlParam = "&taskId=";
    private final static int pageSize = 10;

    public TerminationsCollectiveApproval() {
	super();
	this.setScreenTitle(getMessage("title_terminationsCollectiveApproval"));
	try {
	    // Load the promotions tasks data
	    searchTerminationsTasks();
	} catch (Exception e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    // Load the terminations tasks data
    private void searchTerminationsTasks() {
	try {
	    selectAll = false;
	    terminationsTasks = (ArrayList<Object>) TerminationsWorkflow.getWFTerminationsTasks(this.loginEmpData.getEmpId(), new String[] { WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), WFTaskRolesEnum.SIGN_MANAGER.getCode() });
	    terminationsTasksListSize = terminationsTasks.size();
	} catch (BusinessException e) {
	    terminationsTasks = new ArrayList<Object>();
	    terminationsTasksListSize = 0;
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    // called from the xhtml on change of the selectAll checkbox
    public void selectUnselectAllRows() {
	for (int i = 0; i < terminationsTasks.size(); i++) {
	    ((WFTerminationData) (((Object[]) terminationsTasks.get(i))[0])).setSelected(selectAll);
	}
    }

    // Loop over the selected tasks for approval
    // called from the xhtml when "Approve" button clicked
    public void doTerminationsCollectiveApprovalSM() {
	try {
	    String unsuccessfulTaskIdsIfAny = "";
	    String comma = "";
	    int unsuccessfulTasksCount = 0;

	    long transactionTypeId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.STE_TERMINATION.getCode(), TransactionClassesEnum.TERMINATIONS.getCode()).getId();
	    for (Object obj : terminationsTasks) {
		WFTerminationData wfTerminationData = (WFTerminationData) (((Object[]) obj)[0]);
		WFTask task = null;
		if (!wfTerminationData.getSelected())
		    continue;

		try {
		    task = (WFTask) (((Object[]) obj)[1]);
		    WFInstance instance = (WFInstance) (((Object[]) obj)[2]);
		    EmployeeData requester = (EmployeeData) (((Object[]) obj)[4]);

		    TerminationRecordData terminationRecordData = new TerminationRecordData();
		    List<TerminationRecordDetailData> terminationRecordDetailDataList = new ArrayList<TerminationRecordDetailData>();

		    if (instance.getProcessId() != WFProcessesEnum.OFFICERS_TERMINATION_REQUEST.getCode() && !task.getAssigneeWfRole().equals(WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode())) {
			terminationRecordData = TerminationsService.getTerminationRecordDataById(wfTerminationData.getRecordId());
			terminationRecordDetailDataList = TerminationsService.getTerminationRecordDetailsByRecordId(wfTerminationData.getRecordId());
		    }

		    if (task.getAssigneeWfRole().equals(WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode()))
			TerminationsWorkflow.doTerminationSSM(requester, instance, wfTerminationData, terminationRecordData, terminationRecordDetailDataList, task, WFActionFlagsEnum.APPROVE.getCode());
		    else if (task.getAssigneeWfRole().equals(WFTaskRolesEnum.SIGN_MANAGER.getCode()))
			TerminationsWorkflow.doTerminationSM(requester, instance, wfTerminationData, terminationRecordData, terminationRecordDetailDataList, task, transactionTypeId, WFActionFlagsEnum.APPROVE.getCode());
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

	    // Call the search method here to reload the tasks
	    searchTerminationsTasks();

	} catch (Exception e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public List<Object> getTerminationsTasks() {
	return terminationsTasks;
    }

    public void setTerminationsTasks(List<Object> terminationsTasks) {
	this.terminationsTasks = terminationsTasks;
    }

    public boolean isSelectAll() {
	return selectAll;
    }

    public void setSelectAll(boolean selectAll) {
	this.selectAll = selectAll;
    }

    public int getTerminationsTasksListSize() {
	return terminationsTasksListSize;
    }

    public void setTerminationsTasksListSize(int terminationsTasksListSize) {
	this.terminationsTasksListSize = terminationsTasksListSize;
    }

    public String getTaskurlparam() {
	return taskUrlParam;
    }

    public int getPagesize() {
	return pageSize;
    }

}
